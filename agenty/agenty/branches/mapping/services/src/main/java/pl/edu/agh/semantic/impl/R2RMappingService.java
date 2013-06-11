package pl.edu.agh.semantic.impl;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import de.fuberlin.wiwiss.r2r.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.semantic.service.MappingService;

import javax.jws.WebService;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * R2R based mapping implementation.
 * <p/>
 * Creation date: 2012.05.29
 *
 * @author Tomasz Zdybał
 */
@WebService(targetNamespace = "http://semantic.agh.edu.pl/",
		endpointInterface = "pl.edu.agh.semantic.service.MappingService", serviceName = "MappingService")
public class R2RMappingService implements MappingService {
	/**
	 * Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(R2RMappingService.class);
	private MappingRepository mappingRepository;

	public R2RMappingService() {
	}

	public R2RMappingService(String path) {
		initMappingRepository(path);
	}

	private void initMappingRepository(String path) {
		mappingRepository = Repository.createFileOrUriRepository(path);
	}

	/**
	 * Kind of test...
	 *
	 * @param args command line arguments (not used)
	 */
	public static void main(String[] args) {
		try {
			if (args.length != 3) {
				System.out.println("3 args required: [query.sparql] [mapping.ttl] [file.ttl]");
				System.exit(0);
			}
			String querySparql = args[0];
			String mappingTtl = args[1];
			String fileToMapTtl = args[2];

			final MappingService service = new R2RMappingService(mappingTtl);
			final String query = FileUtils.readFileToString(new File(querySparql));

			final String transformedQuery = service.transformQuery(query);
			log.info("Transformed query:\n{}\n", transformedQuery);


			final ByteArrayOutputStream out = new ByteArrayOutputStream();

			final Model model = ModelFactory.createDefaultModel();
			model.read(new FileInputStream(fileToMapTtl), "", "TTL");
			QueryExecution exec = QueryExecutionFactory.create(transformedQuery, model);
			ResultSet rs = exec.execSelect();


			ResultSetFormatter.out(System.out, rs);
			//ResultSetFormatter.outputAsRDF(out, "RDF/XML", rs);

			//System.err.println(new String(out.toByteArray()));
			//final InputStream in = new ByteArrayInputStream(out.toByteArray());
			//service.mapResult(query, in, System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String transformQuery(final String query) {
		log.info("Query before transformation: {}", query);
		try {
			final String bgpAsTtlFile = sparqlToTtl(query);
			final Source in = new FileOrURISource(bgpAsTtlFile);

			final StringBuilder newQuery = new StringBuilder();

			final Query q = QueryFactory.create(query);
			final Map<String, String> prefixes = q.getPrefixMapping().getNsPrefixMap();

			for (Map.Entry<String, String> prefix : prefixes.entrySet()) {
				newQuery.append("PREFIX ");
				newQuery.append(prefix.getKey());
				newQuery.append(": <");
				newQuery.append(prefix.getValue());
				newQuery.append(">\n");
			}

			List<String> varsToSelect = q.getResultVars();
			log.debug("Variables to select: " + varsToSelect);

			int suffix = 0xfff;
			final Map<String, String> varToSuffix = new HashMap<String, String>();

			newQuery.append("SELECT ");
			for (String var : varsToSelect) {
				newQuery.append('?');
				newQuery.append(var);
				newQuery.append(' ');
			}
			newQuery.append("{\n");

			Pattern selectPattern = Pattern.compile("SELECT.*\\{(.*)\\}", Pattern.DOTALL);

			Matcher selectMatcher = selectPattern.matcher(query);
			if (selectMatcher.find()) {
				newQuery.append('{');
				newQuery.append(selectMatcher.group(1));
				newQuery.append('}');

				Pattern varPattern = Pattern.compile("\\?([^ ]*)");
				Matcher varMatcher = varPattern.matcher(selectMatcher.group(1));
				while (varMatcher.find()) {
					String var = varMatcher.group(1);
					if (!varToSuffix.containsKey(var)) {
						varToSuffix.put(var, Integer.toHexString(suffix));
						--suffix;
					}
				}
			}

			for (Mapping mapping : getMappingRepository().getMappings().values()) {
				// execute transformation in memory
				final StringWriter writer = new StringWriter();
				final Output out = new NTriplesOutput(writer);
				mapping.executeMapping(in, out);
				out.close();

				// check whether transformation produced any output
				if (writer.getBuffer().length() > 0) {
					newQuery.append("\nUNION\n{\n");
					newQuery.append(writer.getBuffer().toString().replaceAll("_:", "?"));
					newQuery.append("}");
				}
			}
			newQuery.append("\n}\n");

			String queryString = newQuery.toString();
			for (Map.Entry<String, String> e : varToSuffix.entrySet()) {
				queryString = queryString.replaceAll("\\?[^ ]*X3aXX2dX7" + e.getValue(), '?' + e.getKey());
			}

			for (Map.Entry<String, String> e : q.getPrefixMapping().getNsPrefixMap().entrySet()) {
				final Pattern pattern = Pattern.compile("<" + e.getValue() + "([^>]+)>", Pattern.DOTALL);
				final Matcher matcher = pattern.matcher(queryString);
				while (matcher.find()) {
					String args = matcher.group(1);

					queryString = queryString.replaceFirst("<" + e.getValue() + "([^>]+)>", e.getKey() + ":" + args);
				}
			}

			return queryString;
		} catch (IOException e) {
			log.debug("Error", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setMapping(String fileOrURI) {
		initMappingRepository(fileOrURI);
	}

	@Override
	public void mapResult(final String query, final InputStream originalResult, final OutputStream mappedResult) {
		String targetVocabulary = getTargetVocabulary(query);
		//ResultSet rs = ResultSetFactory.fromXML(originalResult);


		final Model model = ModelFactory.createDefaultModel();
		model.read(originalResult, "");
		final Source source = new JenaModelSource(model);
		final Output output = new RDFXMLOutput(mappedResult);
		final Repository repository = (Repository) getMappingRepository();


		Mapper.transform(source, output, repository, targetVocabulary);

	}

	private String getTargetVocabulary(String query) {
		final Query q = QueryFactory.create(query);
		final StringBuilder builder = new StringBuilder("");
		for (Map.Entry<String, String> e : q.getPrefixMapping().getNsPrefixMap().entrySet()) {
			builder.append("@prefix ");
			builder.append(e.getKey());
			builder.append(": <");
			builder.append(e.getValue());
			builder.append("> .\n");
		}
		builder.append('(');
		final Set<String> vocabulary = new HashSet<String>();
		process(q.getQueryPattern(), vocabulary);
		final Iterator<String> iter = vocabulary.iterator();
		while (iter.hasNext()) {
			String val = iter.next();
			builder.append('<');
			builder.append(val);
			builder.append(">, ");
			/*for (Map.Entry<String, String> e : q.getPrefixMapping().getNsPrefixMap().entrySet()) {
				val = val.replace(e.getValue(), e.getKey() + ":");
			}
			if (!val.contains("http://")) {
				builder.append(val);
				builder.append(", ");
			}*/
		}
		builder.setLength(builder.length() - 2);
		builder.append(')');


		return builder.toString();
	}

	private String sparqlToTtl(final String query) throws IOException {
		StringBuilder output = new StringBuilder();

		Pattern outerPattern = Pattern.compile("PREFIX\\s+.*>");
		Pattern innerPattern = Pattern.compile("PREFIX\\s+(\\w+):\\s+<(.+)>");
		Matcher outerMatcher = outerPattern.matcher(query);

		while (outerMatcher.find()) {
			String prefix = outerMatcher.group(0);
			Matcher innerMatcher = innerPattern.matcher(prefix);
			if (innerMatcher.find() && innerMatcher.groupCount() == 2) {
				String id = innerMatcher.group(1);
				String uri = innerMatcher.group(2);
				output.append(String.format("@prefix %s: <%s> .\n", id, uri));
			}
		}
		output.append('\n');

		Pattern selectPattern = Pattern.compile("SELECT.*\\{(.*)\\}", Pattern.DOTALL);

		Matcher selectMatcher = selectPattern.matcher(query);
		if (selectMatcher.find()) {
			String args = selectMatcher.group(1);
			output.append(args.replaceAll("\\?", "_:"));
		}

		final File tempFile = File.createTempFile("SPARQL", ".ttl");
		BufferedWriter tmpout = new BufferedWriter(new FileWriter(tempFile));
		tmpout.write(output.toString());
		tmpout.close();
		tempFile.deleteOnExit();

		// 'mock'
		//FileUtils.copyFile(new File(PROJECT_PREFIX + "src/app/sample_data/my_example.ttl"), tempFile);

		return tempFile.getCanonicalPath();
	}

	public MappingRepository getMappingRepository() {
		return mappingRepository;
	}

	/**
	 * Processes element of parsing tree.
	 * <p/>
	 * Depending on type of a element different actions are performed.
	 * If element is a ElementGroup, recursive processing occurs.
	 * If element is ElementPathBlock, information about RDF triples is stored.
	 * If element is ElementFilter, filters are stored.
	 * For any other type of element recursive processing stops.
	 *
	 * @param element element to process.
	 */
	private void process(Element element, Set<String> vocabulary) {
		log.debug("Entering element (" + element.getClass().getSimpleName() + "): " + element + "\n");
		if (element instanceof ElementGroup) {
			ElementGroup group = (ElementGroup) element;
			for (Element elem : group.getElements()) {
				process(elem, vocabulary);
			}
		} else if (element instanceof ElementPathBlock) {
			ElementPathBlock block = (ElementPathBlock) element;

			final Iterator<TriplePath> iterator = block.patternElts();
			while (iterator.hasNext()) {
				TriplePath p = iterator.next();
				if (p.getSubject().isURI()) {
					vocabulary.add(p.getSubject().getURI());
				}
				if (p.getPredicate().isURI()) {
					vocabulary.add(p.getPredicate().getURI());
				}
				if (p.getObject().isURI()) {
					vocabulary.add(p.getObject().getURI());
				}
			}
		}
	}
}

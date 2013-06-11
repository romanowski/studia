/*
 * Copyright (c) 2011,2012, Krzysztof Styrc and Tomasz Zdybał
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the project nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE PROJECT AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE PROJECT OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package pl.edu.agh.semantic.impl;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.semantic.dto.IndexEntry;
import pl.edu.agh.semantic.enums.DataSourceType;
import pl.edu.agh.semantic.service.IndexService;
import pl.edu.agh.semantic.service.MappingService;
import pl.edu.agh.semantic.service.QueryService;
import pl.edu.agh.semantic.utils.OntologyUtil;
import pl.edu.agh.semantic.utils.PrefixUtil;
import pl.edu.agh.semantic.utils.WebServiceUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Default implementation of QueryService.
 *
 * @author Tomasz Zdybał
 */
public class QueryServiceImpl implements QueryService {
	/**
	 * Logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(QueryServiceImpl.class);

	/**
	 * Maps prefixes to URIs of ontologies.
	 */
	private Map<String, String> prefixes;

	/**
	 * Maps ontology URIs to lists of triples in which they appear.
	 */
	private Map<String, List<TriplePath>> uriToVar = new HashMap<String, List<TriplePath>>();

	/**
	 * Object representation of query used in parsing and processing.
	 */
	private Query query;

	/**
	 * Set of filters found in query during processing.
	 */
	private Set<ElementFilter> filters = new HashSet<ElementFilter>();

	/**
	 * Parses the query. It is the first step in query processing.
	 * <p/>
	 * This method reads prefixes from query and prepares all necessary fields of class.
	 *
	 * @param queryString query to parse
	 * @see pl.edu.agh.semantic.service.QueryService#parse(String)
	 */
	@Override
	public void parse(String queryString) {
		// temporary data for mapping
		query = QueryFactory.create(queryString);
		PrefixMapping prefixMapping = query.getPrefixMapping();

		prefixes = prefixMapping.getNsPrefixMap();

		/*
		 * For every every ontology prefix try to get mapping service.
		 * If its available, use it to generate query with mapping.
		 */
		for (Map.Entry<String, String> prefix : getPrefixes(false).entrySet()) {
			String ontologyURI = prefix.getValue();

			try {
				String indexURL = OntologyUtil.getMappingServiceURL(ontologyURI);
				MappingService mapper = WebServiceUtil.getMappingService(indexURL);

				queryString = mapper.transformQuery(queryString);
			} catch (Exception e) {
				log.error(String.format("Unable to get data sources for %s", ontologyURI), e);
			}
		}

		// processing of mapped query
		query = QueryFactory.create(queryString);
		prefixMapping = query.getPrefixMapping();

		prefixes = prefixMapping.getNsPrefixMap();
		uriToVar.clear();
		filters.clear();
	}

	/**
	 * Returns prefixes used in query.
	 *
	 * @param omitWellKnown whether omit well known prefixes or not
	 * @return map of prefixes used in the query. Keys are the short names of a prefixes and values are the URIs
	 *         of ontologies.
	 * @see pl.edu.agh.semantic.service.QueryService#getPrefixes(boolean)
	 */
	@Override
	public Map<String, String> getPrefixes(boolean omitWellKnown) {
		Map<String, String> map = new HashMap<String, String>();
		for (Map.Entry<String, String> prefix : prefixes.entrySet()) {
			if (omitWellKnown && !PrefixUtil.isWellKnown(prefix.getValue())) {
				map.put(prefix.getKey(), prefix.getValue());
			}
		}
		return map;
	}


	/**
	 * Returns data sources (named graphs and SPARQL endpoint), grouped by ontology.
	 * <p/>
	 * Sources map is build as follows:<br />
	 * 1) for each prefix get the URL of indexing service from ontology<br />
	 * 2) use that URL to acquire data sources for prefix<br />
	 * 3) add data sources to the map<br />
	 *
	 * @return map ontology URL -> set of data sources containing data for ontology
	 * @see pl.edu.agh.semantic.service.QueryService#getSourcesURLs()
	 */
	@Override
	public Map<String, Set<IndexEntry>> getSourcesURLs() {
		Map<String, Set<IndexEntry>> map = new HashMap<String, Set<IndexEntry>>();

		for (Map.Entry<String, String> prefix : getPrefixes(true).entrySet()) {
			String ontologyURI = prefix.getValue();

			try {
				String indexURL = OntologyUtil.getIndexServiceURL(ontologyURI);
				IndexService index = WebServiceUtil.getIndexService(indexURL);

				Set<IndexEntry> dataSources = index.getAll(ontologyURI);

				map.put(prefix.getValue(), dataSources);
			} catch (Exception e) {
				log.error(String.format("Unable to get data sources for %s", ontologyURI), e);
			}
		}

		return map;
	}

	/**
	 * Builds new, federated query and returns string representation of query.
	 * <p/>
	 * Query is build as follows:<br />
	 * 1) for each prefix in original query append corresponding PREFIX clause<br />
	 * 2) append 'SELECT' and any variables found in corresponding clause in original query<br />
	 * 3) append 'FROM <http://www.w3.org/2000/01/rdf-schema#>', because query must have FROM clause<br />
	 * 4) for each named graph found in indexes append 'FROM NAMED' clause<br />
	 * 5) for each prefix all related triples from original query are found<br />
	 * 6) for each above triple all index entries are get<br />
	 * 7) for each above entry append triples to the query string inside SERVICE or GRAPH clause<br />
	 * 8) append filter clauses to SERVICE or GRAPH clause<br />
	 * 8) if there's more than one entry for a prefix, use UNION clause to gather results<br />
	 *
	 * @return processed query
	 * @throws Exception
	 * @see pl.edu.agh.semantic.service.QueryService#getProcessedQuery()
	 */
	@Override
	public String getProcessedQuery() throws Exception {
		process();

		StringBuilder newQuery = new StringBuilder();

		for (Map.Entry<String, String> prefix : prefixes.entrySet()) {
			newQuery.append("PREFIX ");
			newQuery.append(prefix.getKey());
			newQuery.append(": <");
			newQuery.append(prefix.getValue());
			newQuery.append(">\n");
		}

		List<String> varsToSelect = query.getResultVars();
		log.debug("Variables to select: " + varsToSelect);

		newQuery.append("SELECT ");
		for (String var : varsToSelect) {
			newQuery.append('?');
			newQuery.append(var);
			newQuery.append(' ');
		}
		newQuery.append("FROM <http://www.w3.org/2000/01/rdf-schema#>\n");

		final Set<IndexEntry> namedGraphs = getUniqueGraphs();
		for (IndexEntry e : namedGraphs) {
			newQuery.append("FROM NAMED <").append(e.getUrl()).append(">\n");
		}

		newQuery.append(" {\n");

		for (String uri : prefixes.values()) {
			List<TriplePath> triples = getTriplesForURI(uri);
			if (triples != null && triples.size() > 0) {
				final Set<IndexEntry> sourcesURLs = getSourcesURLs().get(uri);
				if (sourcesURLs != null && sourcesURLs.size() > 0) {
					int i = 0;
					for (IndexEntry source : sourcesURLs) {
						if (i != 0) {
							newQuery.append("UNION\n");
						}
						++i;
						handleSource(newQuery, triples, source);
					}

				}
			}
		}

		newQuery.append("}\n");
		final String newQueryString = newQuery.toString();
		log.info("Query string (not validated): \n{}", newQueryString);
		// validation
		QueryFactory.create(newQueryString).validate();

		return newQueryString;
	}

	/**
	 * Returns set of unique URLs to named graphs.
	 *
	 * @return set of URLs to named graphs
	 */
	private Set<IndexEntry> getUniqueGraphs() {
		Set<IndexEntry> namedGraphs = new HashSet<IndexEntry>();
		for (Set<IndexEntry> set : getSourcesURLs().values()) {
			for (IndexEntry e : set) {
				if (DataSourceType.RDF_NAMED_GRAPH.equals(e.getType())) {
					namedGraphs.add(e);
				}
			}
		}
		return namedGraphs;
	}

	/**
	 * Appends RDF triples and FILTER clauses to query inside GRAPH or SERVICE clauses.
	 *
	 * @param newQuery partially processed query
	 * @param triples  triples to add
	 * @param source   data source
	 * @throws URISyntaxException
	 */
	private void handleSource(StringBuilder newQuery, List<TriplePath> triples, IndexEntry source)
			throws URISyntaxException {
		String endpointUrl = source.getUrl();
		newQuery.append("{ ");
		if (DataSourceType.SPARQL_ENDPOINT.equals(source.getType())) {
			newQuery.append("SERVICE");
		} else {
			newQuery.append("GRAPH");
		}
		newQuery.append(" <");
		newQuery.append(endpointUrl);
		newQuery.append("> {\n");
		for (TriplePath path : triples) {
			appendNode(newQuery, path.getSubject());
			appendNode(newQuery, path.getPredicate());
			appendNode(newQuery, path.getObject());
			newQuery.append(".\n");
		}

		for (ElementFilter filter : filters) {
			newQuery.append(filter.toString());
			newQuery.append("\n");
		}
		newQuery.append("}\n}\n");
	}

	/**
	 * Appends string representation of a node to stringBuilder.
	 *
	 * @param stringBuilder string builder
	 * @param node          node to add/append
	 * @throws URISyntaxException
	 */
	private void appendNode(StringBuilder stringBuilder, Node node) throws URISyntaxException {
		if (node.isVariable()) {
			stringBuilder.append('?');
			stringBuilder.append(node.getName());
		} else if (node.isURI()) {
			stringBuilder.append(formPrefix(node.getURI()));
		} else if (node.isLiteral()) {
			stringBuilder.append('"');
			stringBuilder.append(node.getLiteral().getValue());
			stringBuilder.append("\"^^");
			stringBuilder.append(formPrefix(node.getLiteralDatatypeURI()));
		} else {
			stringBuilder.append(node.toString());
		}
		stringBuilder.append(" ");
	}

	/**
	 * Forms prefix string representation for URI
	 *
	 * @param uri URI
	 * @return prefix string representation
	 * @throws URISyntaxException
	 */
	private String formPrefix(String uri) throws URISyntaxException {
		StringBuilder stringBuilder = new StringBuilder();
		String fragment = new URI(uri).getFragment();
		if (fragment == null) {
			fragment = uri.substring(uri.lastIndexOf('/') + 1);
		}
		stringBuilder.append(query.getPrefixMapping().getNsURIPrefix(uri.substring(0,
				uri.length() - fragment.length())));
		stringBuilder.append(":");
		stringBuilder.append(fragment);
		return stringBuilder.toString();
	}

	/**
	 * Returns RDF triples for given uri.
	 *
	 * @param uri uri
	 * @return list of RDF triples
	 */
	private List<TriplePath> getTriplesForURI(String uri) {
		return uriToVar.get(uri);
	}

	/**
	 * Processes the query - gathers all information needed to create federated query.
	 * <p/>
	 * Starts recursive processing on tree-representation of a query.
	 *
	 * @see pl.edu.agh.semantic.service.QueryService#process()
	 */
	@Override
	public void process() {
		process(query.getQueryPattern());
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
	private void process(Element element) {
		log.debug("Entering element (" + element.getClass().getSimpleName() + "): " + element + "\n");
		if (element instanceof ElementGroup) {
			ElementGroup group = (ElementGroup) element;
			for (Element elem : group.getElements()) {
				process(elem);
			}
		} else if (element instanceof ElementPathBlock) {
			ElementPathBlock block = (ElementPathBlock) element;

			final Iterator<TriplePath> iterator = block.patternElts();
			while (iterator.hasNext()) {
				TriplePath p = iterator.next();
				addTripple(p);
			}
		} else if (element instanceof ElementFilter) {
			filters.add((ElementFilter) element);
		}
	}

	/**
	 * Saves information about RDF triple.
	 *
	 * @param p RDF triple to add
	 */
	private void addTripple(TriplePath p) {
		String namespace = p.getPredicate().getNameSpace();
		if (p.getObject().isURI()) {
			namespace = p.getObject().getNameSpace();
		}
		if (PrefixUtil.isWellKnown(namespace)) {
			namespace = getNamespaceForVariable((Var) p.getSubject());
		}
		List<TriplePath> vars = uriToVar.get(namespace);
		if (vars == null) {
			vars = new ArrayList<TriplePath>();
		}
		vars.add(p);
		uriToVar.put(namespace, vars);
	}

	/**
	 * Returns namespace of variable.
	 *
	 * @param variable variable
	 * @return namespace of variable
	 */
	private String getNamespaceForVariable(Var variable) {
		for (Map.Entry<String, List<TriplePath>> entry : uriToVar.entrySet()) {
			for (TriplePath path : entry.getValue()) {
				if (path.getSubject().equals(variable)) {
					return entry.getKey();
				}
			}
		}
		return null;
	}
}

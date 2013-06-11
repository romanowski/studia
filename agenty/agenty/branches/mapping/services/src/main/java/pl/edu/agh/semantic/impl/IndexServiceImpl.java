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

import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.jws.WebService;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.httpclient.HttpURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import pl.edu.agh.semantic.dto.IndexEntry;
import pl.edu.agh.semantic.enums.DataSourceType;
import pl.edu.agh.semantic.service.IndexService;

/**
 * Default IndexService implementation using Java Map
 * class. The index is stored persistently as XML file with
 * custom XSD definition. The class is thread-safe.
 *
 * @author kstyrc
 * @see IndexService
 */
@WebService(targetNamespace = "http://semantic.agh.edu.pl/",
		endpointInterface = "pl.edu.agh.semantic.service.IndexService", serviceName = "IndexService")
public class IndexServiceImpl implements IndexService {
	private static final Logger log = LoggerFactory.getLogger(IndexService.class);

	/**
	 * Index datatype structure. Using Map/HashMap
	 * for fast retrieval.
	 */
	private Map<String, Set<IndexEntry>> index = new HashMap<String, Set<IndexEntry>>();

	private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
	private final Lock read = lock.readLock();
	private final Lock write = lock.writeLock();
	private String saveFileName;
	/**
	 * Creates IndexServiceImpl instance. Index initial content is 
	 * read from the index.xml resource file.
	 */
	public IndexServiceImpl() {
		this.saveFileName = URLDecoder.decode(IndexServiceImpl.class.getClassLoader().getResource("index.xml").getFile());
		readFromXML(this.saveFileName);
	}
	
	public void  writeToXML() {
		writeToXML(this.saveFileName);
	}

	/**
	 * @see IndexService
	 */
	@Override
	public boolean add(String ontology, IndexEntry entry) {
		log.info("Adding {} to index for ontology {}", entry, ontology);
		boolean status = false;

		write.lock();
		try {
			Set<IndexEntry> subIndex = index.get(ontology);
			boolean created = false;
			if (subIndex == null) {
				subIndex = new HashSet<IndexEntry>();
				created = true;
			}
			status = subIndex.add(entry);
			if (created) {
				index.put(ontology, subIndex);
			}
		} catch (RuntimeException e) {
			log.error("Runtime error", e);
			throw e;
		} finally {
			write.unlock();
		}

		return status;
	}

	/**
	 * @see IndexService
	 */
	@Override
	public boolean remove(String ontology, IndexEntry entry) {
		log.info("Removing {} from index for ontology {}", entry, ontology);
		boolean status = false;

		write.lock();
		try {
			Set<IndexEntry> subIndex = index.get(ontology);
			if (subIndex != null) {
				status = subIndex.remove(entry);
			}
		} catch (RuntimeException e) {
			log.error("Runtime error", e);
			throw e;
		} finally {
			write.unlock();
		}

		return status;
	}

	/**
	 * @see IndexService
	 */
	@Override
	public Set<IndexEntry> getAll(String ontology) {
		log.debug("Returning all index entries for ontology {}", ontology);
		read.lock();
		try {
			return index.get(ontology);
		} catch (RuntimeException e) {
			log.error("Runtime error", e);
			throw e;
		} finally {
			read.unlock();
		}
	}

	/**
	 * @see IndexService
	 */
	@Override
	public Set<String> getOntologySet() {
		log.debug("getOntologySet called");
		read.lock();
		try {
			return index.keySet();
		} catch (RuntimeException e) {
			log.error("Runtime error", e);
			throw e;
		} finally {
			read.unlock();
		}
	}

	private void setIndex(Map<String, Set<IndexEntry>> index) {
		write.lock();
		try {
			this.index = index;
		} catch (RuntimeException e) {
			log.error("Runtime error", e);
			throw e;
		} finally {
			write.unlock();
		}
	}

	/**
	 * Reads index content from XML file.
	 *
	 * @param filename XML filename
	 */
	synchronized private void readFromXML(String filename) {
		log.info("Reading index from a file {}", filename);
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(filename));

			Element rootElement = doc.getDocumentElement();
			NodeList ontologyIndexes = rootElement.getElementsByTagName("ontology-index");
			log.info("Number of ontology indexes: {}", ontologyIndexes.getLength());

			// get all ontology-indexes
			Map<String, Set<IndexEntry>> index = new HashMap<String, Set<IndexEntry>>();
			for (int i = 0; i < ontologyIndexes.getLength(); i++) {
				Element ontologyIndex = (Element) ontologyIndexes.item(i);

				// get ontology name
				String ontology = ontologyIndex.getElementsByTagName("ontology").item(0).getTextContent();
				log.info("Ontology name: {}", ontology);
				NodeList ontologyIndexEntries = ((Element) ontologyIndex.getElementsByTagName("entries").item(
						0)).getElementsByTagName("entry");
				log.info("Number of ontology entries: {}", ontologyIndexEntries.getLength());

				// get all index entries
				Set<IndexEntry> indexEntries = new HashSet<IndexEntry>();
				for (int j = 0; j < ontologyIndexEntries.getLength(); j++) {
					Element entry = (Element) ontologyIndexEntries.item(j);

					// get entry properties
					String type = entry.getElementsByTagName("type").item(0).getTextContent();
					String url = entry.getElementsByTagName("url").item(0).getTextContent();
					log.info("Ontology entry: type={}, url={}", type, url);

					// add proper entry type
					if (type.equals("SPARQL_ENDPOINT")) {
						indexEntries.add(new IndexEntry(DataSourceType.SPARQL_ENDPOINT, url));
					} else if (type.equals("RDF_NAMED_GRAPH")) {
						indexEntries.add(new IndexEntry(DataSourceType.RDF_NAMED_GRAPH, url));
					}
				}

				// add to index
				index.put(ontology, indexEntries);
			}

			// update index
			setIndex(index);

		} catch (Exception e) {
			log.error("Reading index from a file error", e);
		}
	}

	/**
	 * Writes index content to XML file.
	 *
	 * @param filename XML filename
	 */

	synchronized public void writeToXML(String filename) {
		log.info("Writing index to a file {}", filename);
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();

			// write root node
			Element rootElement = doc.createElement("index");
			doc.appendChild(rootElement);

			// write all ontology-indexes
			Set<Entry<String, Set<IndexEntry>>> indexes = index.entrySet();
			for (Entry<String, Set<IndexEntry>> index : indexes) {

				// write ontology-index tag
				Element ontologyIndex = doc.createElement("ontology-index");
				rootElement.appendChild(ontologyIndex);

				// write ontology property of ontology-index
				Element ontology = doc.createElement("ontology");
				ontology.appendChild(doc.createTextNode(index.getKey()));
				ontologyIndex.appendChild(ontology);

				// write entries property of ontology-index
				Element entries = doc.createElement("entries");
				ontologyIndex.appendChild(entries);

				// write all entries
				for (IndexEntry indexEntry : index.getValue()) {
					Element entry = doc.createElement("entry");
					entries.appendChild(entry);

					Element type = doc.createElement("type");
					if (indexEntry.getType().equals(DataSourceType.SPARQL_ENDPOINT)) {
						type.appendChild(doc.createTextNode("SPARQL_ENDPOINT"));
					} else {
						type.appendChild(doc.createTextNode("RDF_NAMED_GRAPH"));
					}
					entry.appendChild(type);

					Element url = doc.createElement("url");
					url.appendChild(doc.createTextNode(indexEntry.getUrl()));
					entry.appendChild(url);
				}

				// write to xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(filename));
				transformer.transform(source, result);
			}

		} catch (Exception e) {
			log.error("Writing index to a file error", e);
		}
	}
}

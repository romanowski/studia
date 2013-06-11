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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.semantic.dto.IndexEntry;
import pl.edu.agh.semantic.enums.DataSourceType;
import pl.edu.agh.semantic.service.AutomaticIndexingService;
import pl.edu.agh.semantic.service.IndexService;
import pl.edu.agh.semantic.utils.OntologyUtil;
import pl.edu.agh.semantic.utils.PrefixUtil;
import pl.edu.agh.semantic.utils.WebServiceUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AutomaticIndexingServiceImpl implements AutomaticIndexingService {
	private static final Logger log = LoggerFactory.getLogger(AutomaticIndexingServiceImpl.class);

	/**
	 * @see AutomaticIndexingService
	 */
	@Override
	public Set<String> indexEndpoint(String endpoint, String ontology) {
		try {
			String indexURL = OntologyUtil.getIndexServiceURL(ontology);
			IndexService index = WebServiceUtil.getIndexService(indexURL);
			index.add(ontology, new IndexEntry(DataSourceType.SPARQL_ENDPOINT, endpoint));
			index.writeToXML();
			final HashSet<String> set = new HashSet<String>();
			set.add(indexURL);
			return set;
		} catch (Exception e) {
			log.error("Unable to add endpoint to index!", e);
		}
		return null;
	}

	/**
	 * @see AutomaticIndexingService
	 */
	@Override
	public Set<String> indexGraph(String url, String type) {
		final Model remoteModel = ModelFactory.createDefaultModel();
		if (remoteModel != null) {
			remoteModel.getReader(type).read(remoteModel, url);
			try {
				return indexModel(remoteModel, url, DataSourceType.RDF_NAMED_GRAPH);
			} catch (Exception e) {
				log.error("Unable to add named graph to index!", e);
			}
		}
		return null;
	}

	
	/**
	 * @see AutomaticIndexingService
	 */
	@Override
	public Set<String> indexModel(Model remoteModel, String url, DataSourceType dataSourceType) {
		return indexModel(remoteModel, url, dataSourceType, "");
	}
	
	/**
	 * @see AutomaticIndexingService
	 */
	@Override
	public Set<String> indexModel(Model remoteModel, String url, DataSourceType dataSourceType, String prefixToOmmit) {
		if (!remoteModel.getNsPrefixMap().isEmpty()) {
			final Collection<String> prefixes = remoteModel.getNsPrefixMap().values();
			return indexPrefixes(prefixes, url, dataSourceType, prefixToOmmit);
		} else {
			return null;
		}
	}

	/**
	 * Add given dataSource(url & type) to all indexes of prefixes.
	 *
	 * @param prefixes	   prefixes to be indexed
	 * @param dataSourceURL  data source URL
	 * @param dataSourceType data source type
	 * @return set of URLs of indexes
	 */
	private Set<String> indexPrefixes(final Collection<String> prefixes, String dataSourceURL,
									  DataSourceType dataSourceType, String prefixToOmmit) {
		Set<String> registeredPrefixes = new HashSet<String>();
		Set<String> indexes = new HashSet<String>();
		for (String prefix : prefixes) {
			if (!registeredPrefixes.contains(prefix) && !PrefixUtil.isWellKnown(prefix) && !prefixToOmmit.equals(prefix)) {
				log.debug("Getting index service URL...");

				try {
					String indexURL = OntologyUtil.getIndexServiceURL(prefix);
					IndexService index = WebServiceUtil.getIndexService(indexURL);

					log.debug("Adding data source to index {}", indexURL);
					index.add(prefix, new IndexEntry(dataSourceType, dataSourceURL));
					index.writeToXML();
					registeredPrefixes.add(prefix);
					indexes.add(indexURL);
				} catch (Exception e) {
					log.error("Cannot index ontology", e);
				}
			}
		}
		return indexes;
	}
	
	/**
	 * @see AutomaticIndexingService
	 */
	@Override
	public Set<String> unIndexEndpoint(String endpoint, String ontology) {
		try {
			String indexURL = OntologyUtil.getIndexServiceURL(ontology);
			IndexService index = WebServiceUtil.getIndexService(indexURL);
			index.remove(ontology, new IndexEntry(DataSourceType.SPARQL_ENDPOINT, endpoint));
			index.writeToXML();
			final HashSet<String> set = new HashSet<String>();
			set.add(indexURL);
			return set;
		} catch (Exception e) {
			log.error("Unable to remove endpoint from index!", e);
		}
		return null;
	}

	/**
	 * @see AutomaticIndexingService
	 */
	@Override
	public Set<String> unIndexGraph(String url, String type) {
		final Model remoteModel = ModelFactory.createDefaultModel();
		if (remoteModel != null) {
			remoteModel.getReader(type).read(remoteModel, url);
			try {
				return unIndexModel(remoteModel, url, DataSourceType.RDF_NAMED_GRAPH);
			} catch (Exception e) {
				log.error("Unable to remove named graph from index!", e);
			}
		}
		return null;
	}

	/**
	 * @see AutomaticIndexingService
	 */
	@Override
	public Set<String> unIndexModel(Model remoteModel, String url, DataSourceType dataSourceType) {
		if (!remoteModel.getNsPrefixMap().isEmpty()) {
			final Collection<String> prefixes = remoteModel.getNsPrefixMap().values();
			return unIndexPrefixes(prefixes, url, dataSourceType);
		} else {
			return null;
		}
		
	}

	/**
	 * Add given dataSource(url & type) to all indexes of prefixes.
	 *
	 * @param prefixes	   prefixes to be indexed
	 * @param dataSourceURL  data source URL
	 * @param dataSourceType data source type
	 * @return set of URLs of indexes
	 */
	private Set<String> unIndexPrefixes(final Collection<String> prefixes, String dataSourceURL,
									  DataSourceType dataSourceType) {
		Set<String> registeredPrefixes = new HashSet<String>();
		Set<String> indexes = new HashSet<String>();
		for (String prefix : prefixes) {
			if (!registeredPrefixes.contains(prefix) && !PrefixUtil.isWellKnown(prefix)) {
				log.debug("Getting index service URL...");

				try {
					String indexURL = OntologyUtil.getIndexServiceURL(prefix);
					IndexService index = WebServiceUtil.getIndexService(indexURL);

					log.debug("Removeing data source from index {}", indexURL);
					index.remove(prefix, new IndexEntry(dataSourceType, dataSourceURL));
					index.writeToXML();
					registeredPrefixes.add(prefix);
					indexes.add(indexURL);
				} catch (Exception e) {
					log.error("Cannot unindex ontology", e);
				}
			}
		}
		return indexes;
	}
}

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

import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.semantic.enums.DataSourceType;
import pl.edu.agh.semantic.service.AutomaticIndexingService;
import pl.edu.agh.semantic.service.StorageService;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;

/**
 * Default StorageService implementation.
 *
 * @author Tomasz Zdybał
 */
public class StorageServiceImpl implements StorageService {
	private static final Logger log = LoggerFactory.getLogger(StorageServiceImpl.class);

	private static final String DEFAULT_GRAPH = "default";
	private String endpointURI;
	private String graphURI;
	private Dataset dataset;
	private String directory;

	@Override
	public void init(String directory, String endpointURI, String graphURI) {
		this.directory = directory;
		if (dataset == null) {
			log.info("Storage location: " + directory);
			dataset = TDBFactory.createDataset(directory);
			this.endpointURI = endpointURI;
			this.graphURI = graphURI;
		}
	}

	@Override
	public void addDataToNamedGraph(InputStream dataStream, String type, String graphName) {
		log.info("Creating named graph {}", graphName);
		final Model namedModel = dataset.getNamedModel(getNamedGraphURL(graphName));
		if (namedModel == null) {
			TDBFactory.createNamedModel(graphName, directory);
		}

		addData(namedModel, DataSourceType.RDF_NAMED_GRAPH, dataStream, type, graphName);
	}

	@Override
	public void printNamedGraph(OutputStream output, String graphName) {
		if (graphName != null) {
			log.info("Reading named graph {}", graphName);
			final Model namedModel = dataset.getNamedModel(getNamedGraphURL(graphName));
			if (namedModel != null) {
				namedModel.write(output/*, "RDF/XML"*/);
			} else {
				log.error("No such graph {}", graphName);
			}
		} else {
			log.info("Reading default graph");
			dataset.getDefaultModel().write(output);
		}

	}

	@Override
	public void addDataToDefaultGraph(InputStream dataStream, String type) {
		final Model model = dataset.getDefaultModel();
		if (model.isClosed()) {
			throw new RuntimeException("Storage must be opened!");
		}
		addData(model, DataSourceType.SPARQL_ENDPOINT, dataStream, type);
	}

	private void addData(Model model, DataSourceType dataSourceType, InputStream dataStream, String type) {
		addData(model, dataSourceType, dataStream, type, DEFAULT_GRAPH);
	}


	private void addData(Model model, DataSourceType dataSourceType, InputStream dataStream, String type,
						 String graphName) {
		log.info("Adding data to {} graph", graphName);
		model.read(dataStream, null, type);

		log.info("Adding information about data in {} graph to indices.", graphName);
		String realGraphNameName;
		if (DEFAULT_GRAPH.equals(graphName)) {
			realGraphNameName = "";
		} else {
			realGraphNameName = graphName;
		}
		try {
			AutomaticIndexingService indexingService = new AutomaticIndexingServiceImpl();
			indexingService.indexModel(model, getNamedGraphURL(realGraphNameName), dataSourceType);
		} catch (Exception e) {
			log.error("Unable to index prefixes!", e);
		}
	}

	private String getNamedGraphURL(String graphName) {
		if (graphName != null && !graphName.equals("")) {
			return graphURI + '/' + graphName;
		} else {
			return endpointURI;
		}
	}
}

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jws.WebService;

import org.apache.commons.io.IOUtils;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.semantic.enums.DataSourceType;
import pl.edu.agh.semantic.service.AutomaticIndexingService;
import pl.edu.agh.semantic.service.StorageService;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelCon;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * Default StorageService implementation.
 *
 * @author Tomasz Zdybał, Jakub Cwajna
 */
@WebService(targetNamespace = "http://semantic.agh.edu.pl/",
endpointInterface = "pl.edu.agh.semantic.service.StorageService", serviceName = "StorageService")
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
	public Set<String> getAllNamedGraphs(){
		Set<String> allNamedGraphs = new HashSet<String>();
		Iterator<String> iterator = dataset.listNames();
		while (iterator.hasNext()){
			allNamedGraphs.add(iterator.next());
		}
		return allNamedGraphs;
	}
	
	@Override
	public String getDefaultGraph(){
		if(dataset.getDefaultModel() == null)
			return null;
		return getNamedGraphURL(DEFAULT_GRAPH);
	}
	
	@Override
	public void addDataToNamedGraph(InputStream dataStream, String type, String graphName) {
		log.info("Creating named graph {}", graphName);
		
		
		final Model namedModel = dataset.getNamedModel(getNamedGraphURL(graphName));
		if (namedModel == null) {
			TDBFactory.createNamedModel(graphName, directory);
		}
		addData(namedModel, DataSourceType.RDF_NAMED_GRAPH, dataStream, type, graphName);
		namedModel.close();
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
	public void addStringDataToDefaultGraph(String data, String type){
		addDataToDefaultGraph(IOUtils.toInputStream(data), type);
	}
	
	@Override
	public void addDataToDefaultGraph(InputStream dataStream, String type) {
		final Model model = dataset.getDefaultModel();
		if (model.isClosed()) {
			throw new RuntimeException("Storage must be opened!");
		}
		addData(model, DataSourceType.SPARQL_ENDPOINT, dataStream, type);
		model.close();
	}

	private void addData(Model model, DataSourceType dataSourceType, InputStream dataStream, String type) {
		addData(model, dataSourceType, dataStream, type, DEFAULT_GRAPH);
	}


	private void addData(Model model, DataSourceType dataSourceType, InputStream dataStream, String type,
						 String graphName) {
		log.info("Adding data to {} graph", graphName);
		model.read(dataStream, null, type);
		addData(model, dataSourceType, graphName);
	}
	
	private void addData(Model model, DataSourceType dataSourceType, String graphName) {
		log.info("Adding information about data in {} graph to indices.", graphName);
		String realGraphNameName;
		if (DEFAULT_GRAPH.equals(graphName)) {
			realGraphNameName = "";
		} else {
			realGraphNameName = graphName;
		}
		try {
			AutomaticIndexingService indexingService = new AutomaticIndexingServiceImpl();
			indexingService.indexModel(model, getNamedGraphURL(realGraphNameName), dataSourceType, this.endpointURI);
		} catch (Exception e) {
			log.error("Unable to index prefixes!", e);
		}
	}
	
	public void removeData(String graphName) {
		Model model;
		DataSourceType dataSourceType = DataSourceType.SPARQL_ENDPOINT;
		log.info("Removing data from {} graph", graphName);
			
		log.info("Removing information about data in {} graph to indices.", graphName);
		String realGraphNameName;
		if (DEFAULT_GRAPH.equals(graphName)) {
			model = dataset.getDefaultModel();
			realGraphNameName = "";
		} else {
			realGraphNameName = graphName;
			model = dataset.getNamedModel(getNamedGraphURL(graphName));
		}
		try {
			AutomaticIndexingService indexingService = new AutomaticIndexingServiceImpl();
			indexingService.unIndexModel(model, getNamedGraphURL(realGraphNameName), dataSourceType);
			dataset.getNamedModel(realGraphNameName).removeAll();
		} catch (Exception e) {
			log.error("Unable to unindex prefixes!", e);
		}
	}

	private String getNamedGraphURL(String graphName) {
		if (graphName != null && !graphName.equals("")) {
			return graphURI + '/' + graphName;
		} else {
			return endpointURI;
		}
	}

	@Override
	public void addAutogeneratedDataForOntologies(List<String> ontologiesUrlsList, List<Integer> ontologiesExamplesCountList, String storagePrefix) {
		Model model = dataset.getDefaultModel();
		int ontoNo=0;
		for (String url: ontologiesUrlsList){
			int ontologiesExamplesCount = ontologiesExamplesCountList.get(ontoNo);
			ontoNo++;
			OntModel ontologyModel = ModelFactory.createOntologyModel();
			OntModel ontologyModelIndividuals = ModelFactory.createOntologyModel();
			ontologyModel.read(url);

			String DEFAULT_NS = ontologyModel.getNsPrefixURI("");
			Ontology firstOntology = ontologyModel.listOntologies().next();
			String ontologyUri = firstOntology.getURI();
			String ontologyPrefix = ontologyUri.substring(ontologyUri.lastIndexOf('/')+1);
			if (ontologyPrefix.endsWith("#") && ontologyPrefix.length()>1)
				ontologyPrefix = ontologyPrefix.substring(0, ontologyPrefix.length()-1);
			Log.info("ontologyPrefix of 1 ontology: " + ontologyPrefix + "uri: "+ ontologyUri + " default_NS" + DEFAULT_NS);
			ExtendedIterator<OntClass> allClasses =  ontologyModel.listClasses();
			for (OntClass ontoClass; allClasses.hasNext();){
				ontoClass = allClasses.next();
				for (int i=1; i <= ontologiesExamplesCount; i++){
					Individual  individual = addClassIndividual(ontologyModelIndividuals, this.endpointURI + "#", ontoClass, i, storagePrefix);
					StmtIterator propertiesIterator = individual.listProperties();
					model.add(propertiesIterator);
				}
			}
			model.setNsPrefix("", this.endpointURI+"#");
			model.setNsPrefix(ontologyPrefix, ontologyUri);
			ontologyModelIndividuals.close();
			ontologyModel.close();
		}
		Log.info("Saving model..");
		addData(model, DataSourceType.SPARQL_ENDPOINT, DEFAULT_GRAPH);
		model.close();
	}


	private Individual addClassIndividual(OntModel ontologyModelIndividuals, String DEFAULT_NS, OntClass ontoClass, int i, String storagePrefix) {
		String individualUri=DEFAULT_NS + storagePrefix+ontoClass.getLocalName() + i;
		Log.info("Trying to add individual: " + individualUri);
		Individual individual = ontologyModelIndividuals.createIndividual(individualUri, ontoClass);
		ExtendedIterator<OntProperty> classProperties = ontoClass.listDeclaredProperties();
		for (OntProperty ontologyProperty; classProperties.hasNext();){
			ontologyProperty = classProperties.next();
			Log.info("Trying to add property: " + individualUri+ontologyProperty.getLocalName());
			if (ontologyProperty.isDatatypeProperty()){
				Log.info("  property is dataType ");
				//todo: add default value for each type, eq for bool - false
				//RDFDatatype dataType 
				//model.createTypedLiteral(Object)
				individual.addProperty(ontologyProperty, storagePrefix + "_" + ontologyProperty.getLocalName() + i);
			}
			if (ontologyProperty.isObjectProperty()){
				OntClass objectPropertyClass = ontologyProperty.getRange().asClass();
				Log.info("  property is object of type" + objectPropertyClass.getLocalName());
				Individual propertyIndividual = addClassIndividual(ontologyModelIndividuals, DEFAULT_NS,  objectPropertyClass, i, storagePrefix);
				Log.info("Adding object property " +  objectPropertyClass.getLocalName() + i);
				individual.addProperty(ontologyProperty, propertyIndividual);
			}
		}
		Log.info("Created Individual:" + individual.getURI());
		return individual;
	}
}

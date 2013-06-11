/*
 * Copyright (c) 2012, Jakub Cwajna
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
import javax.servlet.http.HttpUtils;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import pl.edu.agh.semantic.dto.IndexEntry;
import pl.edu.agh.semantic.enums.DataSourceType;
import pl.edu.agh.semantic.nodes.NodeManager;
import pl.edu.agh.semantic.service.IndexService;
import pl.edu.agh.semantic.service.NodesManagementService;

/**
 *
 * @author Jakub Cwajna
 * @see IndexService
 */
@WebService(targetNamespace = "http://semantic.agh.edu.pl/",
		endpointInterface = "pl.edu.agh.semantic.service.NodesManagment", serviceName = "NodesManagment")
public class NodesManagementServiceImpl implements NodesManagementService {
	private static final Logger log = LoggerFactory.getLogger(NodesManagementService.class);

	private final String fileName = "nodes.xml";
	
	private NodeManager ontologyNodeManager = new NodeManager("ontology-nodes");
	private NodeManager indexNodeManager = new NodeManager("index-nodes");
	private NodeManager storageNodeManager = new NodeManager("storage-nodes");

	/**
	 * Creates IndexServiceImpl instance. Index initial content is 
	 * read from the index.xml resource file.
	 */
	public NodesManagementServiceImpl() {
		readFromXML();
	}

	/**
	 * @see IndexService
	 */
	@Override
	public boolean addOntologyNode(String url) {
		return ontologyNodeManager.addNode(url);
	}

	/**
	 * @see IndexService
	 */
	@Override
	public boolean removeOntologyNode(String url) {
		return ontologyNodeManager.removeNode(url);
	}
	
	/**
	 * @see IndexService
	 */
	@Override
	public Set<String> getAllOntologyNodes() {
		return ontologyNodeManager.getAllNodes();
	}
	
	/**
	 * @see IndexService
	 */
	@Override
	public boolean addIndexNode(String url) {
		return indexNodeManager.addNode(url);
	}

	/**
	 * @see IndexService
	 */
	@Override
	public boolean removeIndexNode(String url) {
		return indexNodeManager.removeNode(url);
	}

	/**
	 * @see IndexService
	 */
	@Override
	public Set<String> getAllIndexNodes() {
		return indexNodeManager.getAllNodes();
	}
	
	/**
	 * @see IndexService
	 */
	@Override
	public boolean addStorageNode(String url) {
		return storageNodeManager.addNode(url);
	}

	/**
	 * @see IndexService
	 */
	@Override
	public boolean removeStorageNode(String url) {
		return storageNodeManager.removeNode(url);
	}

	/**
	 * @see IndexService
	 */
	@Override
	public Set<String> getAllStorageNodes() {
		return storageNodeManager.getAllNodes();
	}
	
	/**
	 * Reads index content from XML file.
	 *
	 * @param document XML document
	 */
	synchronized private void readFromXML() {
		log.info("Reading nodes from a file");
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			String filename = URLDecoder.decode(NodesManagementServiceImpl.class.getClassLoader().getResource(this.fileName).getFile());
			Document doc = db.parse(new File(filename));
			
			ontologyNodeManager.readFromXML(doc);
			indexNodeManager.readFromXML(doc);
			storageNodeManager.readFromXML(doc);

			
		} catch (Exception e) {
			log.error("Reading index from a file error", e);
		}
	}

	
	
	/**
	 * Writes index content to XML file.
	 *
	 * @param filename XML filename
	 */

	synchronized private void writeToXML(String filename) {
		log.info("Writing nodes to a file {}", filename);
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();

			// write root node
			Element rootElement = doc.createElement("nodes");
			doc.appendChild(rootElement);
			
			ontologyNodeManager.writeToXML(doc);
			indexNodeManager.writeToXML(doc);
			storageNodeManager.writeToXML(doc);
			
			// write to xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filename));
			transformer.transform(source, result);
			

		} catch (Exception e) {
			log.error("Writing nodes to a file error", e);
		}
	}

	@Override
	public void writeToXML() {
		writeToXML(URLDecoder.decode(NodesManagementServiceImpl.class.getClassLoader().getResource(this.fileName).getFile()));
		
	}
}

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

package pl.edu.agh.semantic.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;

/**
 * Various ontology-related utilities.
 *
 * @author Tomasz Zdybał
 */
public class OntologyUtil {
	private static final Logger log = LoggerFactory.getLogger(OntologyUtil.class);

	/**
	 * Retrieves index service URL from ontology URI.
	 *
	 * @param ontologyURI ontology URI
	 * @return index service URL for the ontology
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws XPathExpressionException
	 */
	public static String getIndexServiceURL(String ontologyURI)
			throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {

		log.debug("Checking index service URL for ontology {}", ontologyURI);
		Document doc = getDocumentBuilder().parse(ontologyURI);
		XPathExpression indexExpression = getXPathExpression("/rdf:RDF/indexingService");

		final String indexURL = (String) indexExpression.evaluate(doc, XPathConstants.STRING);
		log.debug("Index service URL: {}", indexURL);

		return indexURL;
	}

	/**
	 * Retrieves mapping service URL from ontology URI.
	 *
	 * @param ontologyURI ontology URI
	 * @return mapping service URL for the ontology
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws XPathExpressionException
	 */
	public static String getMappingServiceURL(String ontologyURI)
			throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {

		log.debug("Checking mapping service URL for ontology {}", ontologyURI);
		Document doc = getDocumentBuilder().parse(ontologyURI);
		XPathExpression indexExpression = getXPathExpression("/rdf:RDF/mappingService");

		final String indexURL = (String) indexExpression.evaluate(doc, XPathConstants.STRING);
		log.debug("Mapping service URL: {}", indexURL);

		return indexURL;
	}

	private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		return dbf.newDocumentBuilder();
	}

	private static XPathExpression getXPathExpression(String expression) throws XPathExpressionException {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();

		NamespaceContext ctx = new RdfNamespaceContext();
		xpath.setNamespaceContext(ctx);

		XPathExpression indexExpression = xpath.compile(expression);
		return indexExpression;
	}

}

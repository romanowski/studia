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
import pl.edu.agh.semantic.service.IndexService;
import pl.edu.agh.semantic.service.OntologyService;
import pl.edu.agh.semantic.service.OntologyWebService;
import pl.edu.agh.semantic.service.StorageService;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Creates service instances.
 *
 * @author Tomasz Zdybał
 */
public class WebServiceUtil {

	private static final Logger log = LoggerFactory.getLogger(WebServiceUtil.class);

	/**
	 * Returns IndexService binding for the given index URL.
	 * 
	 * @param indexURL index URL
	 * @return IndexService binding
	 * @throws MalformedURLException
	 */
	public static IndexService getIndexService(String indexURL) throws MalformedURLException {
		URL wsdlUrl = new URL(indexURL);
		Service service = Service.create(wsdlUrl, new QName("http://semantic.agh.edu.pl/", "IndexService"));
		IndexService indexService = service.getPort(IndexService.class);
		String[] path = wsdlUrl.getPath().split("/");
		indexService.initStorage(path[path.length-2]);
		return indexService;
	}
	
	/**
	 * Returns StorageService binding for the given storage URL.
	 * 
	 * @param storageURL storage URL
	 * @return StorageService binding
	 * @throws MalformedURLException
	 */
	public static StorageService getStorageService(String storageURL) throws MalformedURLException {
		URL wsdlUrl = new URL(storageURL);
		Service service = Service.create(wsdlUrl, new QName("http://semantic.agh.edu.pl/", "StorageService"));
		StorageService storageService = service.getPort(StorageService.class);
		return storageService;
	}
	
	
	/**
	 * Returns OntologyService binding for the given storage URL.
	 * 
	 * @param storageURL storage URL
	 * @return OntologyService binding
	 * @throws MalformedURLException
	 */
	public static OntologyWebService getOntologyService(String ontologryURL) throws MalformedURLException {
		URL wsdlUrl = new URL(ontologryURL);
		Service service = Service.create(wsdlUrl, new QName("http://semantic.agh.edu.pl/", "OntologyWebService"));
		OntologyWebService ontologyService = service.getPort(OntologyWebService.class);
		return ontologyService;
	}
}

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

package pl.edu.agh.semantic.service;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * RDF data storage.
 *
 * @author Tomasz Zdybał
 */
public interface StorageService {
	/**
	 * Sets configuration of data storage.
	 *
	 * @param configFile Joseki TDB configuration file
	 * @param datasetURI URI of dataset
	 * @param graphURI URI of graph servlet
	 */
	void init(String configFile, String datasetURI, String graphURI);

	/**
	 * Adds data to default graph.
	 *
	 * @param dataStream stream with data
	 * @param type	   Joseki stream type
	 */
	void addDataToDefaultGraph(InputStream dataStream, String type);

	/**
	 * Adds data to specified graph.
	 *
	 * @param dataStream stream with data
	 * @param type	   Joseki stream type
	 * @param graphName  name of named graph
	 */
	void addDataToNamedGraph(InputStream dataStream, String type, String graphName);

	/**
	 * Prints named graph to output stream.
	 * @param output output stream
	 * @param graphName name of graph
	 */
	void printNamedGraph(OutputStream output, String graphName);
}

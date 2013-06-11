/*
 * Copyright (c) 2011,2012, Jakub Cwajna
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

import pl.edu.agh.semantic.dto.IndexEntry;

import javax.jws.WebService;
import java.util.Set;

/**
 *
 * @author jakub cwajna
 */
@WebService
public interface NodesManagementService {

	/**
	 * Removes index entry from ontology.
	 *
	 * @param ontology ontology URI
	 * @param entry	data source URL entry
	 * @return true if successful, false otherwise
	 */
	boolean removeIndexNode(String url);

	/**
	 * Get all index entries from ontology.
	 *
	 * @param ontology ontology URL
	 * @return all index entries from ontology
	 */
	Set<String> getAllIndexNodes();

	boolean addIndexNode(String url);
	
	/**
	 * saves cache to file
	 */
	void writeToXML();

	boolean addStorageNode(String url);

	boolean removeStorageNode(String url);

	Set<String> getAllStorageNodes();

	boolean addOntologyNode(String url);

	boolean removeOntologyNode(String url);

	Set<String> getAllOntologyNodes();
}

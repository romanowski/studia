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
import pl.edu.agh.semantic.service.MappingService;

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
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(WebServiceUtil.class);

	/**
	 * Returns IndexService binding for the given index URL.
	 *
	 * @param indexURL index URL
	 * @return IndexService binding
	 * @throws MalformedURLException
	 */
	public static IndexService getIndexService(String indexURL) throws MalformedURLException {
		Service service = Service.create(new URL(indexURL), new QName("http://semantic.agh.edu.pl/", "IndexService"));
		return service.getPort(IndexService.class);
	}

	/**
	 * Returns IndexService binding for the given index URL.
	 *
	 * @param mapperURL mapper URL
	 * @return MappingService binding
	 * @throws MalformedURLException
	 */
	public static MappingService getMappingService(String mapperURL) throws MalformedURLException {
		Service service = Service.create(new URL(mapperURL), new QName("http://semantic.agh.edu.pl/",
				"MappingService"));
		return service.getPort(MappingService.class);
	}
}

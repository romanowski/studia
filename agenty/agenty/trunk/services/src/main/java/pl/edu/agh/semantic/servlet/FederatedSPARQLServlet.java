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

package pl.edu.agh.semantic.servlet;

import org.joseki.processors.SPARQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.semantic.impl.QueryServiceImpl;
import pl.edu.agh.semantic.service.QueryService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Exposes federated query service as a sparql endpoint.
 * 
 * @author kstyrc
 */
@SuppressWarnings("serial")
public class FederatedSPARQLServlet extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(QueryServiceImpl.class);

	protected QueryService queryService;

	@Override
	public void init() throws ServletException {
		super.init();
		queryService = new QueryServiceImpl();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	/**
	 * To achieve transparency as sparql endpoint:<br />
	 * 1) get query parameter<br />
	 * 2) parse query using QueryService<br />
	 * 3) process query using QueryService<br />
	 * 4) redirect to the existing /sparql? endpoint<br />
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String queryString = request.getParameter(SPARQL.P_QUERY);
			queryService.parse(queryString);
			String query = queryService.getProcessedQuery();
			log.info("query parameter = " + request.getParameter(SPARQL.P_QUERY));
			log.info("processed query = " + query);

			ServletContext sc = getServletContext();
			RequestDispatcher rd = sc.getRequestDispatcher("/sparql?" + SPARQL.P_QUERY + "=" + query);
			rd.forward(request, response);

		} catch (Exception ex) {
			log.error("Error: ", ex);

		} finally {

		}
	}
}

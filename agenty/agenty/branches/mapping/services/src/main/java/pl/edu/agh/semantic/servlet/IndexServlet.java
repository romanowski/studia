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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.semantic.impl.AutomaticIndexingServiceImpl;
import pl.edu.agh.semantic.service.AutomaticIndexingService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

/**
 * Handle index add request submitted by HTML form.
 *
 * @author Tomasz Zdybał
 */
@SuppressWarnings("serial")
public class IndexServlet extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(IndexServlet.class);

	protected AutomaticIndexingService indexingService = new AutomaticIndexingServiceImpl();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String actionType = req.getParameter("action_type");
		String noWrite = req.getParameter("no_write");
		String url = req.getParameter("url");
		String type = req.getParameter("type");
		String endpoint = req.getParameter("endpoint_url");
		String ontology = req.getParameter("ontology_url");
		String removeUrl = req.getParameter("remove_url");
		String removeType = req.getParameter("remove_type");
		String removeEndpoint = req.getParameter("remove_endpoint_url");
		String removeOntology = req.getParameter("remove_ontology_url");
		String returnTo = req.getParameter("returnTo");

		boolean ind = false;
		
		StringWriter servletResult =  new StringWriter();
		PrintWriter out = new PrintWriter(servletResult);

		log.debug("IndexServlet request with parameters {}, {}, {}, {},{}, {}, {}, {}", new Object[]{url, type, endpoint, ontology, removeUrl, removeType, removeEndpoint, removeOntology});
		//out.println(url+ type+ endpoint+ ontology+ removeUrl+ removeType+ removeEndpoint+ removeOntology);
		Set<String> indexes = null;
		
		if(actionType!=null && actionType.equals("edit")){
			if (removeUrl != null && removeType != null) {
				indexes = indexingService.unIndexGraph(removeUrl, removeType);
			} else if (removeEndpoint != null && removeOntology != null) {
				indexes = indexingService.unIndexEndpoint(removeEndpoint, removeOntology);
			}		
			if (url != null && type != null) {
				indexes = indexingService.indexGraph(url, type);
			} else if (endpoint != null && ontology != null) {
				indexes = indexingService.indexEndpoint(endpoint, ontology);
			}
			if(noWrite==null)
				out.println("Data source successfully updated");
		}
		else{	
			if (url != null && type != null) {
				indexes = indexingService.indexGraph(url, type);
			} else if (endpoint != null && ontology != null) {
				indexes = indexingService.indexEndpoint(endpoint, ontology);
			}
	
			if (indexes != null) {
				if(noWrite==null)
					out.println("Data source was added to the following indexes:");
				ind = true;
				for (String  idx : indexes) {
					if(noWrite==null)
						out.println(String.format("%s<br>", idx));
				}
			} else if (url != null && type != null || endpoint != null && ontology != null) {
				if(noWrite==null)
					out.println("Unable to add data source to indexes");
			}
	
			
			if (removeUrl != null && removeType != null) {
				indexes = indexingService.unIndexGraph(removeUrl, removeType);
			} else if (removeEndpoint != null && removeOntology != null) {
				indexes = indexingService.unIndexEndpoint(removeEndpoint, removeOntology);
			}
	
			if (indexes != null && !ind) {
				if(noWrite==null)
					out.println("Data source was removed from the following indexes:");
				for (String  idx : indexes) {
					out.println(String.format("%s<br>", idx));
				}
			} else if (removeUrl != null && removeType != null || removeEndpoint != null && removeOntology != null) {
				if(noWrite==null)
					out.println("Unable to remove data source from indexes");
			}
		}
		out.flush();
		req.setAttribute("servlet-result", servletResult.toString());
		log.info("return to:" + returnTo);
		String returnToPage="/index.jsp";;
		if (returnTo == null)
			returnToPage="/indexing.jsp";
        req.getRequestDispatcher(returnToPage).forward(req, resp);
	}
}

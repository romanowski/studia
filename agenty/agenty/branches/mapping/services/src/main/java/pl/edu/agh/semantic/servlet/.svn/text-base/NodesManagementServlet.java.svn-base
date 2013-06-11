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

package pl.edu.agh.semantic.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.semantic.impl.AutomaticIndexingServiceImpl;
import pl.edu.agh.semantic.impl.NodesManagementServiceImpl;
import pl.edu.agh.semantic.nodes.TomcatNodeDeployment;
import pl.edu.agh.semantic.service.AutomaticIndexingService;
import pl.edu.agh.semantic.service.NodesManagementService;
import pl.edu.agh.semantic.utils.Result;

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
 * @author Jakub Cwajna
 */
@SuppressWarnings("serial")
public class NodesManagementServlet extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(NodesManagementServlet.class);

	protected NodesManagementService nodesManagementService = new NodesManagementServiceImpl();
	protected TomcatNodeDeployment tomcatNodeDeployment = new TomcatNodeDeployment();
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String serverUrl = req.getParameter("server-url");
		String nodeName = req.getParameter("node-name");
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		String SPARQLname = req.getParameter("SPARQL-name");

		StringWriter servletResult =  new StringWriter();
		PrintWriter out = new PrintWriter(servletResult);

		out.println("<html><head><title>Index node edit</title></head><body>");
		out.flush();

		log.info("NodeManagementServlet request with parameters {}{}{}{}", new Object[]{serverUrl, nodeName, username, password});
		//TODO: refactoring, to much copy-paste
		//ontology nodes
		try{
			if (serverUrl != null && nodeName != null && username != null && password != null){
				if (req.getParameter("add-ontology") !=null) {
					
					StringBuilder ontologyNodeUrl = new StringBuilder();
					Result result = tomcatNodeDeployment.deployOntologyNode(serverUrl, nodeName, username, password, ontologyNodeUrl);
					log.debug("Tomcat war deployment on URL: {}", new Object[]{ontologyNodeUrl.toString()});
					if (result.isSuccess()){
						nodesManagementService.addOntologyNode(ontologyNodeUrl.toString());
						nodesManagementService.writeToXML();
						out.println("Ontology node "+ontologyNodeUrl+" successfully added.");
						out.println("<br>Tomcat manager result: " + result.getResult());
					}
					else {
						out.println("Ontology node "+nodeName+" on server "+serverUrl+" not added.");
						out.println("<br>Tomcat manager result: " + result.getResult());
					}
			}
			//removing ontology node
			else if (req.getParameter("remove-ontology") !=null){
				Result result = tomcatNodeDeployment.undeployNode(serverUrl, nodeName, username, password);
				log.info("Tomcat war undeployment with result: {}", new Object[]{result});
				if (result.isSuccess()){
					nodesManagementService.removeIndexNode("http://" + serverUrl + nodeName);
					nodesManagementService.writeToXML();
					out.println("Ontology node "+serverUrl + nodeName+" successfully removed with result: " + result);
					out.println("<br>Tomcat manager result: " + result.getResult());
				}
				else {
					out.println("Ontology node "+serverUrl + nodeName+" not removed");
					out.println("<br>Tomcat manager result: " + result.getResult());
				}
			}
				
			//adding new index node
			if (req.getParameter("add-index") !=null) {
				
				StringBuilder indexNodeUrl = new StringBuilder();
				Result result =  tomcatNodeDeployment.deployIndexNode(serverUrl, nodeName, username, password, indexNodeUrl);
				log.debug("Tomcat war deployment on URL: {}", new Object[]{indexNodeUrl.toString()});
				if (result.isSuccess()){
					nodesManagementService.addIndexNode(indexNodeUrl.toString());
					nodesManagementService.writeToXML();
					out.println("Index node "+indexNodeUrl.toString()+" successfully added.");
					out.println("<br>Tomcat manager result: " + result.getResult());
				}
				else {
					out.println("Index node "+nodeName+" on server "+serverUrl+" not added.");
					out.println("<br>Tomcat manager result: " + result.getResult());
				}
			
			}
				//removing index node
			else if (req.getParameter("remove-index") !=null){
				Result result = tomcatNodeDeployment.undeployNode(serverUrl, nodeName, username, password);
				log.info("Tomcat war undeployment with result: {}", new Object[]{result});
				if (result.isSuccess()){
					nodesManagementService.removeIndexNode("http://" + serverUrl + nodeName);
					nodesManagementService.writeToXML();
					out.println("Index node "+serverUrl + nodeName+" successfully removed");
					out.println("<br>Tomcat manager result: " + result.getResult());
				}
				else {
					out.println("Index node "+serverUrl + nodeName+" not removed with result: " + result);
					out.println("<br>Tomcat manager result: " + result.getResult());
				}
			}
			
			
			// editing storage nodes
			if (req.getParameter("add-storage") !=null) {
				StringBuilder storageNodeUrl = new StringBuilder();
				Result result = tomcatNodeDeployment.deployStorageNode(serverUrl, nodeName, SPARQLname, username, password, storageNodeUrl);
				log.debug("Tomcat war deployment on URL: {}", new Object[]{storageNodeUrl.toString()});
				if (result.isSuccess()){
					nodesManagementService.addStorageNode(storageNodeUrl.toString());
					nodesManagementService.writeToXML();
					out.println("Storage node "+storageNodeUrl+" successfully added.");
					out.println("<br>Tomcat manager result: " + result.getResult());
				}
				else {
					out.println("Storage node "+nodeName+" on server "+serverUrl+" not added.");
					out.println("<br>Tomcat manager result: " + result.getResult());
				}
			
			}//removing storage node
			else if (req.getParameter("remove-storage") !=null){
				Result result = tomcatNodeDeployment.undeployNode(serverUrl, nodeName, username, password);
				log.info("Tomcat war undeployment with result: {}", new Object[]{result});
				if (result.isSuccess()){
					nodesManagementService.removeStorageNode("http://" +serverUrl + nodeName);
					nodesManagementService.writeToXML();
					out.println("Storage node "+serverUrl + nodeName+" successfully");
					out.println("<br>Tomcat manager result: " + result.getResult());
				}
				else {
					out.println("Storage node "+serverUrl + nodeName+" not removed");
					out.println("<br>Tomcat manager result: " + result.getResult());
				}
			}
			}
		}
		catch (Exception e){
			log.info("On node deployment:",e);
			out.println("There was an error durring node deployment:" + e.getMessage());
		}
		finally{
			out.flush();
			req.setAttribute("servlet-result", servletResult.toString());
	        req.getRequestDispatcher("/node-management.jsp").forward(req, resp);
		}
	}
}

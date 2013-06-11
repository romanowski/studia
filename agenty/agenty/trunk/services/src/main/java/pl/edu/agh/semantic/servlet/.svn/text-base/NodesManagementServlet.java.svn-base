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


import org.apache.commons.lang.StringUtils;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.semantic.impl.NodesManagementServiceImpl;
import pl.edu.agh.semantic.nodes.TomcatNodeDeployment;
import pl.edu.agh.semantic.service.NodesManagementService;
import pl.edu.agh.semantic.service.StorageService;
import pl.edu.agh.semantic.utils.OntologyUtil;
import pl.edu.agh.semantic.utils.Result;
import pl.edu.agh.semantic.utils.WebServiceUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
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
		
		boolean validationOk = validateParameters(req, SPARQLname,serverUrl, nodeName, username,password, out);
		
		if (validationOk == false){	
			exit(req, resp, servletResult, out);
			return;
		}

		log.info("NodeManagementServlet request with parameters {}{}{}{}", new Object[]{serverUrl, nodeName, username, password});
		//TODO: refactoring, to much copy-paste
		//ontology nodes
		try{
			String rangeFromParam = req.getParameter("range-from");
			String rangeToParam = req.getParameter("range-to");
			int rangeFrom;
			int rangeTo;
			try{
				if (StringUtils.isNotBlank(rangeFromParam))
					rangeFrom = Integer.parseInt(rangeFromParam);
				else
					rangeFrom = 1;
				if (StringUtils.isNotBlank(rangeToParam))
					rangeTo = Integer.parseInt(rangeToParam);
				else
					rangeTo = 1;
			}
			catch (NumberFormatException exc){
				out.println("Error: Range fields must be a number");
				exit(req, resp, servletResult, out);
				return;
			}
			boolean addSufix = StringUtils.isNotBlank(rangeFromParam) && StringUtils.isNotBlank(rangeToParam);
			
			if (req.getParameter("autogenerate-storage")!= null){
				autogenerateStorage(req, out);				
			}			
			else if (serverUrl != null && nodeName != null && username != null && password != null){
				if (req.getParameter("add-ontology") !=null) {
					deployOntologyNodes(serverUrl, nodeName, username,
							password, out, rangeFrom, rangeTo, addSufix);
			}
			//removing ontology node
			else if (req.getParameter("remove-ontology") !=null){
				Result result = tomcatNodeDeployment.undeployNode(serverUrl, nodeName, username, password);
				log.info("Tomcat war undeployment with result: {}", result.getResult());
				if (result.isSuccess()){
					nodesManagementService.removeOntologyNode("http://" + serverUrl + nodeName);
					nodesManagementService.writeToXML();
					out.println("Ontology node "+serverUrl + nodeName+" successfully removed");
					out.println("<br>Tomcat manager result: " + result.getResult()+ "<br>");
				}
				else {
					out.println("Ontology node "+serverUrl + nodeName+" not removed");
					out.println("<br>Tomcat manager result: " + result.getResult()+ "<br>");
				}
			}
				
			//adding new index node
			if (req.getParameter("add-index") !=null) {
				deployIndexNodes(serverUrl, nodeName, username, password, out,
						rangeFrom, rangeTo, addSufix);
			}
				//removing index node
			else if (req.getParameter("remove-index") !=null){
				Result result = tomcatNodeDeployment.undeployNode(serverUrl, nodeName, username, password);
				log.info("Tomcat war undeployment with result: {}", result.getResult());
				if (result.isSuccess()){
					nodesManagementService.removeIndexNode("http://" + serverUrl + nodeName);
					nodesManagementService.writeToXML();
					out.println("Index node "+serverUrl + nodeName+" successfully removed");
					out.println("<br>Tomcat manager result: " + result.getResult()+ "<br>");
				}
				else {
					out.println("Index node "+serverUrl + nodeName+" not removed with result: " + result);
					out.println("<br>Tomcat manager result: " + result.getResult()+ "<br>");
				}
			}
			
			
			// editing storage nodes
			if (req.getParameter("add-storage") !=null) {
				deployStorageNodes(serverUrl, nodeName, username, password,
						SPARQLname, out, rangeFrom, rangeTo, addSufix, false);
			
			}//removing storage node
			else if (req.getParameter("remove-storage") !=null){
				Result result = tomcatNodeDeployment.undeployNode(serverUrl, nodeName, username, password);
				log.info("Tomcat war undeployment with result: {}", result.getResult());
				if (result.isSuccess()){
					nodesManagementService.removeStorageNode("http://" +serverUrl + nodeName);
					nodesManagementService.writeToXML();
					out.println("Storage node "+serverUrl + nodeName+" successfully removed");
					out.println("<br>Tomcat manager result: " + result.getResult()+ "<br>");
				}
				else {
					out.println("Storage node "+serverUrl + nodeName+" not removed");
					out.println("<br>Tomcat manager result: " + result.getResult()+ "<br>");
				}
			}
			}
		}
		catch (NumberFormatException nfe){
			log.info("On node deployment:",nfe);
			out.println("Range from or to parameter is not a number:" + nfe.getMessage());
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

	private void exit(HttpServletRequest req, HttpServletResponse resp,
			StringWriter servletResult, PrintWriter out)
			throws ServletException, IOException {
		out.flush();
		req.setAttribute("servlet-result", servletResult.toString());
		req.getRequestDispatcher("/node-management.jsp").forward(req, resp);
		return;
	}

	private boolean validateParameters(HttpServletRequest req,
			String SPARQLname, String serverUrl, String nodeName, String username, String password, PrintWriter out) {
		boolean validationOk = true;
		if ( StringUtils.isEmpty(serverUrl)){
			out.println("Error: serverUrl must be specified");
			validationOk=false;
		}
		
		if (req.getParameter("autogenerate-storage") == null){
			if ( StringUtils.isEmpty(nodeName)){
				out.println("Error: nodeName must be specified");
				validationOk=false;
			}
		}
		else{
			String storageNodeName = req.getParameter("storage-node-name");
			if ( StringUtils.isEmpty(storageNodeName)){
				out.println("Error: storageNodeName must be specified");
				validationOk=false;
			}
			
		}
		if ( StringUtils.isEmpty(username)){
			out.println("Error: username must be specified");
			validationOk=false;
		}
		if ( StringUtils.isEmpty(password)){
			out.println("Error: password must be specified");
			validationOk=false;
		}
		if (req.getParameter("add-storage") !=null) {
			if ( StringUtils.isEmpty(SPARQLname)){
				out.println("Error: SPARQLname must be specified");
				validationOk=false;
			}
		}
		return validationOk;
	}
	
	private List<Integer> deployStorageNodes(String serverUrl, String nodeName,
			String username, String password, String SPARQLname,
			PrintWriter out, int rangeFrom, int rangeTo, boolean addSufix, boolean createAnother) throws Exception {
		List<Integer> postfixes = new ArrayList<Integer>();
		for (int i = rangeFrom; i <= rangeTo; i++){
			String nodeEffectiveName = addSufix ? nodeName + i : nodeName;
			String SPARQLEffectiveName = addSufix ? SPARQLname + i : SPARQLname;
			if (nodesManagementService.existsStorageNode("http://" + serverUrl + "/" + nodeName + i)){
				if (createAnother)
					rangeTo++;
				else
					out.println("Storage node already exists: " + "http://" + serverUrl + "/" + nodeName + i);
				continue;
			}
			StringBuilder storageNodeUrl = new StringBuilder();
			Result result = tomcatNodeDeployment.deployStorageNode(serverUrl, nodeEffectiveName, SPARQLEffectiveName, username, password, storageNodeUrl);
			log.debug("Tomcat war deployment on URL: {}", new Object[]{storageNodeUrl.toString()});
			if (result.isSuccess()){
				nodesManagementService.addStorageNode(storageNodeUrl.toString());
				nodesManagementService.writeToXML();
				postfixes.add(i);
				out.println("Storage node "+storageNodeUrl+" successfully added.");
				out.println("<br>Tomcat manager result: " + result.getResult()+ "<br>");
			}
			else {
				out.println("Storage node "+nodeEffectiveName+" on server "+serverUrl+" not added.");
				out.println("<br>Tomcat manager result: " + result.getResult()+ "<br>");
			}
		}
		return postfixes;
	}
	
	private void deployIndexNodes(String serverUrl, String nodeName,
			String username, String password, PrintWriter out, int rangeFrom,
			int rangeTo, boolean addSufix) throws Exception {
		for (int i = rangeFrom; i <= rangeTo; i++){
			String nodeEffectiveName = addSufix ? nodeName + i : nodeName;
			StringBuilder indexNodeUrl = new StringBuilder();
			if (nodesManagementService.existsIndexNode("http://" + serverUrl + "/" + nodeName)){
				continue;
			}
			Result result =  tomcatNodeDeployment.deployIndexNode(serverUrl, nodeEffectiveName, username, password, indexNodeUrl);
			log.debug("Tomcat war deployment on URL: {}", new Object[]{indexNodeUrl.toString()});
			if (result.isSuccess()){
				nodesManagementService.addIndexNode(indexNodeUrl.toString());
				nodesManagementService.writeToXML();
				out.println("Index node "+indexNodeUrl.toString()+" successfully added.");
				out.println("<br>Tomcat manager result: " + result.getResult()+ "<br>");
			}
			else {
				out.println("Index node "+nodeEffectiveName+" on server "+serverUrl+" not added.");
				out.println("<br>Tomcat manager result: " + result.getResult()+ "<br>");
			}
		}
	}
	private void deployOntologyNodes(String serverUrl, String nodeName,
			String username, String password, PrintWriter out, int rangeFrom,
			int rangeTo, boolean addSufix) throws Exception {
		for (int i = rangeFrom; i <= rangeTo; i++){
			StringBuilder ontologyNodeUrl = new StringBuilder();
			String nodeEffectiveName = addSufix ? nodeName + i : nodeName;
			Result result = tomcatNodeDeployment.deployOntologyNode(serverUrl, nodeEffectiveName, username, password, ontologyNodeUrl);
			log.debug("Tomcat war deployment on URL: {}", new Object[]{ontologyNodeUrl.toString()});
			if (result.isSuccess()){
				nodesManagementService.addOntologyNode(ontologyNodeUrl.toString());
				nodesManagementService.writeToXML();
				out.println("Ontology node "+ontologyNodeUrl+" successfully added.");
				out.println("<br>Tomcat manager result: " + result.getResult() + "<br>");
			}
			else {
				out.println("Ontology node "+nodeEffectiveName+" on server "+serverUrl+" not added.");
				out.println("<br>Tomcat manager result: " + result.getResult()+ "<br>");
			}
		}
	}
	
	//todo: if storage/index nodes are creating, web services may be not yet deployed 
	private void autogenerateStorage(HttpServletRequest req, PrintWriter out) throws Exception {
		String serverUrl = req.getParameter("server-url");
		String storageNodeName = req.getParameter("storage-node-name");
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		String SPARQLname = req.getParameter("SPARQL-name");
		List<String> ontologiesUrlsList = new ArrayList<String>();
		List<Integer> ontologiesExamplesCountList = new ArrayList<Integer>();
		List<Integer> storagesCountForOntology =  new ArrayList<Integer>();
		
		int storageNodeCount = 0;
		Set<String> allParameters = req.getParameterMap().keySet();
		for (String o: allParameters){
			if (o.startsWith("ontology-url_")){
				char numberC = o.charAt(o.length()-1);
				int paramNo = Character.getNumericValue(numberC);
				if (paramNo> 0){
					int examplesCount = tryParse(req.getParameter("ontology-examples_"+ paramNo));
					int storagesCount = tryParse(req.getParameter("storages_count_"+ paramNo));
					if (examplesCount <= 0 || storagesCount <=0){
						out.println("Warning - ontology "+ o +" skiped because of invalid number parameter ");
						continue;
					}
					String storageUrl = req.getParameter(o);
					if (StringUtils.isEmpty(storageUrl)){
						out.println("Warning - One of ontology url parameter is empty and will be skiped");
						continue;
					}
					ontologiesUrlsList.add(storageUrl );
					storagesCountForOntology.add(storagesCount);
					storageNodeCount += storagesCount;
					ontologiesExamplesCountList.add(examplesCount);
				}
			}

		}
		 
		
		List<Integer> postfixes = new ArrayList<Integer>();
		if (storageNodeCount > 0){
			postfixes = deployStorageNodes(serverUrl, storageNodeName, username, password, SPARQLname, out,  1, storageNodeCount, true, true);
		}
		
		for (String ontologyUrl :ontologiesUrlsList){
			String indexServiceUrl;
			try{
				indexServiceUrl = OntologyUtil.getIndexServiceURL(ontologyUrl);
			}
			catch (Exception e){
				out.println("Adress of indexing service is not included in ontology" + ontologyUrl);
				continue;
			}
			String path = indexServiceUrl.substring(0, indexServiceUrl.lastIndexOf('/'));
			String nodeName = path.substring(path.lastIndexOf('/')+1, path.length());
			String indexServerUrl = path.substring(0, path.lastIndexOf('/'));
			indexServerUrl = indexServerUrl.replace("http://", "");
			Log.info(indexServiceUrl + indexServerUrl +  nodeName);
			deployIndexNodes(indexServerUrl, nodeName, username, password, out, 1, 1, false);
		}
		
		int i=0;
		for (int j = 0; j< storagesCountForOntology.size(); j++){
			List<String> ontologiesForStorage = new ArrayList<String>();
			List<Integer> ontologiesExamplesCountListForStorage = new ArrayList<Integer>();
			ontologiesForStorage.add(ontologiesUrlsList.get(j));
			ontologiesExamplesCountListForStorage.add(ontologiesExamplesCountList.get(j));
			for (int k=0; k< storagesCountForOntology.get(j); k++, i++){
				String storageNodeEffName = storageNodeName + postfixes.get(i);
				String sparqlEff = "/tmp/"+ SPARQLname + postfixes.get(i);
				String url = "http://" + serverUrl + "/" + storageNodeEffName + "/";
				String datasetURI = url + SPARQLname + postfixes.get(i);
				String graphURI = url + "graph";
				String storageServiceUrl = url + "Storage?wsdl";
				StorageService storageService = WebServiceUtil.getStorageService(storageServiceUrl);
				storageService.init(sparqlEff, datasetURI, graphURI);				
				storageService.addAutogeneratedDataForOntologies(ontologiesForStorage, ontologiesExamplesCountListForStorage, "sn" + postfixes.get(i));
			}
		}				
	}
	
	public Integer tryParse(String obj) {
		  Integer retVal;
		  try {
		    retVal = Integer.parseInt(obj);
		  } catch (NumberFormatException nfe) {
		    retVal = 0;
		  }
		  return retVal;
	}
}

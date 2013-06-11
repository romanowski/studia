<%--
  ~ Copyright (c) 2012, Jakub Cwajna
  ~ All rights reserved.
  ~
  ~ Redistribution and use in source and binary forms, with or without
  ~ modification, are permitted provided that the following conditions
  ~ are met:
  ~ 1. Redistributions of source code must retain the above copyright
  ~    notice, this list of conditions and the following disclaimer.
  ~ 2. Redistributions in binary form must reproduce the above copyright
  ~    notice, this list of conditions and the following disclaimer in the
  ~    documentation and/or other materials provided with the distribution.
  ~ 3. Neither the name of the project nor the names of its contributors
  ~    may be used to endorse or promote products derived from this software
  ~    without specific prior written permission.
  ~
  ~ THIS SOFTWARE IS PROVIDED BY THE PROJECT AND CONTRIBUTORS ``AS IS'' AND
  ~ ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  ~ IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
  ~ ARE DISCLAIMED.  IN NO EVENT SHALL THE PROJECT OR CONTRIBUTORS BE LIABLE
  ~ FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
  ~ DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
  ~ OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
  ~ HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
  ~ LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
  ~ OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
  ~ SUCH DAMAGE.
  --%>
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="pl.edu.agh.semantic.dto.IndexEntry" %>
<%@ page import="pl.edu.agh.semantic.impl.NodesManagementServiceImpl" %>
<%@ page import="pl.edu.agh.semantic.service.NodesManagementService" %>
<%@ page import="pl.edu.agh.semantic.utils.WebServiceUtil"%>
<%@ page import="pl.edu.agh.semantic.utils.OntologyUtil"%>
<%@ page import="pl.edu.agh.semantic.service.IndexService"%>
<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="pl.edu.agh.semantic.enums.*"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.net.URL" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head><title>Nodes content</title>
	<link rel="stylesheet" type="text/css" href="style.css"/>
	<script type="text/javascript">
 		function openEditForm(ontology,entry,type)
 		{
 		    testwindow = window.open("", "mywindow", "location=1,status=1,scrollbars=1,width=500,height=300");
 		    testwindow.moveTo(100, 100);
 		    if(type=="Endpoint"){
	 		   	testwindow.document.write('\
	 		   		<form action="indexService" method="post">\
	 				<fieldset>\
	 					<legend>Edit index</legend>\
	 					<div class="fm-opt">\
	 						<label for="endpoint_url">Endpoint URL:</label>\
	 						<input type="text" name="endpoint_url" id="endpoint_url">\
	 					</div>\
	 					<div class="fm-opt">\
	 						<label for="ontology_url">Ontology URL:</label>\
	 						<input type="text" name="ontology_url" id="ontology_url">\
	 					</div>\
	 					<input type="hidden" name="remove_endpoint_url" id="remove_endpoint_url" value="'+entry+'">\
	 					<input type="hidden" name="remove_ontology_url" id="remove_ontology_url" value="'+ontology+'">\
	 					<input type="hidden" name="action_type" id="action_type" value="edit">\
	 					<div class="fm-submit">\
	 						<input type="submit" value="Submit data">\
	 					</div>\
	 				</fieldset>\
	 			</form>');
 		    }
	 		else if (type=='Graph'){
	 		   	testwindow.document.write('\
		 		   		<form action="indexService" method="post">\
		 				<fieldset>\
		 					<legend>Edit index</legend>\
		 					<div class="fm-opt">\
		 						<label for="url">URL:</label>\
		 						<input type="text" name="url" id="url">\
		 					</div>\
		 					<div class="fm-opt">\
		 						<label for="type">Type:</label>\
		 						<input type="text" name="type" id="type">\
		 					</div>\
		 					<input type="hidden" name="remove_url" id="remove_url" value="'+entry+'">\
		 					<input type="hidden" name="action_type" id="type" value="edit">\
		 					<input type="hidden" name="remove_type" id="remove_type" value="TURTLE">\
		 					<div class="fm-submit">\
		 						<input type="submit" value="Submit data">\
		 					</div>\
		 				</fieldset>\
		 			</form>');			
	 		}
 		}
 	</script>
</head>

<body style="text-align: left">

<%
	//String queryString = request.getParameter("query");

	//queryService.parse(queryString);

%>
<h1>Query Node</h1>
<ul>
	<li><a href="index.html">Home</a></li>
	<li><a href="node-content.jsp">Node content</a></li>
	<li><a href="node-management.jsp">Node managment</a></li>
	<li><a href="fast-node-management.jsp">Fast demo node management</a></li>
	<li><a href="query-service.html">General purpose federated SPARQL processor</a></li>
</ul>
<hr/>

<div class="frame">
		<div class="header">Index content</div>
		<div class="body">
			<table>
			<tr><th>Ontology</th><th>SPARQL endpoints</th><th>Named graphs</th><th>Delete</th><th>Edit</th></tr>
			
			<% 
			response.setContentType("text/html;charset=UTF-8");
			NodesManagementService nodesManagmentService = new NodesManagementServiceImpl();
			Set<String> indexSet = nodesManagmentService.getAllIndexNodes();
			for(String url : indexSet){
				IndexService index = WebServiceUtil.getIndexService(url + "/Index?wsdl"); 
				Set<String> ontologies = index.getOntologySet();
			
			for (String ontology : ontologies) { 
				Set<IndexEntry> dataSources = index.getAll(ontology);
			%>
		
					<% for (IndexEntry entry : dataSources) if (DataSourceType.SPARQL_ENDPOINT.equals(entry.getType())) { %>
					<tr>
						<td><%= ontology %></td>
						<td>
							<%=  entry.getUrl() %><br />
						</td>
						<td>
						</td>
						<td>
							<form action="indexService" method="post">
								<input type="hidden" name="remove_ontology_url" id="remove_ontology_url" value="<%= ontology%>">
								<input type="hidden" name="remove_endpoint_url" id="remove_endpoint_url" value="<%= entry.getUrl()%>">
								<input type="hidden" name="no_write" id="no_write" value="true">
								<input type="submit" value="Delete">
							</form>
						</td>
						<td>
							<button onclick="openEditForm('<%=ontology%>','<%=entry.getUrl()%>','Endpoint');">
								Edit
							</button>
						</td>
					</tr>
					<% } %>

					<% for (IndexEntry entry : dataSources) if (DataSourceType.RDF_NAMED_GRAPH.equals(entry.getType())) { %>
					<tr>
						<td><%= ontology %></td>
						<td>
						</td>
						<td>
							<%=  entry.getUrl() %><br />
						</td>
						<td>
							<form action="indexService" method="post">
								<input type="hidden" name="remove_type" id="remove_type" value="TURTLE">
								<input type="hidden" name="remove_url" id="remove_url" value="<%= entry.getUrl()%>">
								<input type="hidden" name="no_write" id="no_write" value="true">
								<input type="submit" value="Delete">
							</form>
						</td>
						<td>
							<button onclick="openEditForm('<%=ontology%>','<%=entry.getUrl()%>','Graph');">
								Edit
							</button>
						</td>
					</tr>
					<% } %>
			<% } }%>
			</table>
		</div>
	</div>


</body>
</html>

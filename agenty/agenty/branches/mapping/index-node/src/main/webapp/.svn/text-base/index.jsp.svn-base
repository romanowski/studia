<%@ page import="java.net.URL"%>
<%@ page import="java.util.Set"%>
<%@ page import="pl.edu.agh.semantic.utils.WebServiceUtil"%>
<%@ page import="pl.edu.agh.semantic.utils.OntologyUtil"%>
<%@ page import="pl.edu.agh.semantic.service.IndexService"%>
<%@ page import="pl.edu.agh.semantic.dto.IndexEntry"%>
<%@ page import="pl.edu.agh.semantic.enums.*"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Index content</title>
	<link rel="stylesheet" type="text/css" href="style.css" />
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
	 					<input type="hidden" name="returnTo" id="returnTo" value="index.jsp">\
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
		 					<input type="hidden" name="returnTo" id="returnTo" value="index.jsp">\
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
<h1>Index Node</h1>
<ul>
	<li><a href="index.jsp">Home</a></li>
</ul>
<hr/>
<% 
	String servletResult = (String)request.getAttribute("servlet-result");
	if (servletResult != null){%>
<div class="header">Results:</div>
<div class="results">
	<%= servletResult %>
</div>
<hr/>
<% } %>

<%  
URL indexURL = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath() + "/Index?wsdl");
IndexService index = WebServiceUtil.getIndexService(indexURL.toString());
Set<String> ontologies = index.getOntologySet();
%>
	<div class="frame">
		<div class="header">Index content</div>
		<div class="body">
			<table>
			<tr><th>Ontology</th><th>SPARQL endpoints</th><th>Named graphs</th><th>Delete</th><th>Edit</th></tr>
			<% for (String ontology : ontologies) { 
				Set<IndexEntry> dataSources = index.getAll(ontology);
			%>
		
					<% for (IndexEntry entry : dataSources) if (DataSourceType.SPARQL_ENDPOINT.equals(entry.getType())) { %>
					<tr>
						<td><a href="
							<%= ontology %>"><%= ontology %>
							</a></td>
						<td><a href="
							<%=  entry.getUrl() %>"><%=  entry.getUrl() %>
						</a></td>
						<td>
						</td>
						<td>
							<form action="indexService" method="post">
								<input type="hidden" name="remove_ontology_url" id="remove_ontology_url" value="<%= ontology%>">
								<input type="hidden" name="remove_endpoint_url" id="remove_endpoint_url" value="<%= entry.getUrl()%>">
								<input type="hidden" name="no_write" id="no_write" value="true">
								<input type="hidden" name="returnTo" id="returnTo" value="index.jsp">
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
						<td><a href="
							<%= ontology %>"><%= ontology %>
							</a></td>
						<td>
						</td>
						<td>
							<a href="
							<%=  entry.getUrl() %>"><%=  entry.getUrl() %>
							</a>
						</td>
						<td>
							<form action="indexService" method="post">
								<input type="hidden" name="remove_type" id="remove_type" value="TURTLE">
								<input type="hidden" name="remove_url" id="remove_url" value="<%= entry.getUrl()%>">
								<input type="hidden" name="no_write" id="no_write" value="true">
								<input type="hidden" name="returnTo" id="returnTo" value="index.jsp">
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
			<% } %>
			</table>
		</div>
	</div>

</body>
</html>
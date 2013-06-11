<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ page import="java.net.URL"%>
<%@ page import="java.util.Set"%>
<%@ page import="pl.edu.agh.semantic.utils.WebServiceUtil"%>
<%@ page import="pl.edu.agh.semantic.utils.OntologyUtil"%>
<%@ page import="pl.edu.agh.semantic.service.OntologyService"%>
<%@ page import="pl.edu.agh.semantic.impl.OntologyServiceImpl"%>
<%@ page import="pl.edu.agh.semantic.dto.IndexEntry"%>
<%@ page import="pl.edu.agh.semantic.enums.*"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Ontology content</title>
	<link rel="stylesheet" type="text/css" href="style.css" />
</head>
<body style="text-align: left">


<h1>Ontology Node</h1>
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

OntologyService ontologyService = new OntologyServiceImpl();
String destinationDir = request.getSession().getServletContext().getRealPath("/");
Set<String> ontologies = ontologyService.getAll(destinationDir);
String thisUrl = request.getRequestURL().toString();
int idx = thisUrl.lastIndexOf('/');
if (idx != thisUrl.length())
	thisUrl = thisUrl.substring(0, idx);

%>
	<div class="frame">
		<div class="header">Ontologies</div>
		<div class="body">
			<table>
			<tr><th>Ontology</th><th>Delete</th></tr>
			<% for (String ontology : ontologies) { 
				String ontologyURL = thisUrl  + '/'+  ontology;%>
					<tr>
						<td><a href= <%=ontologyURL %> > <%=ontologyURL %> </a></td>
						<td>
							<form action="ontologyService" method="post">
								<input type="hidden" name="remove_ontology_url" id="remove_ontology_url" value="<%= ontology%>">
								<input type="submit" name="remove" value="Delete">
							</form>
						</td>
					</tr>
			<% } %>

			</table>
		</div>
	</div>
	
	
	<div id="container">
		<form enctype="multipart/form-data" action="ontologyService" method="post">
			<fieldset>
				<legend>Add ontologies</legend>
				<div class="fm-opt">
					<label for="file">Data file:</label>
					<input type="file" name="file[]" id="file" multiple="">
				</div>
				<div class="fm-submit">
					<input type="submit" name="add" value="Submit ontologies">
				</div>
			</fieldset>
		</form>
	
		<hr>
	</div>

</body>
</html>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
		"http://www.w3.org/TR/html4/loose.dtd">
		
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
	<title>Indexing service</title>

	<link rel="stylesheet" type="text/css" href="style.css"/>
</head>
<body>
<h1>Query Node</h1>
<ul>
	<li><a href="index.html">Home</a></li>
	<li><a href="node-content.jsp">Node content</a></li>
	<li><a href="node-management.jsp">Node managment</a></li>
	<li><a href="fast-node-management.jsp">Fast demo node management</a></li>
	<li><a href="query-service.html">General purpose federated SPARQL processor</a></li>
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
<div id="container">
	<form action="indexService" method="post">
		<fieldset>
			<legend>Add named graph to index</legend>
			<div class="fm-opt">
				<label for="type2">File type/syntax:</label>
				<select name="type" id="type2">
					<option>RDF/XML</option>
					<option>RDF/XML-ABBREV</option>
					<option>N-TRIPLE</option>
					<option>N-TRIPLES</option>
					<option>N3</option>
					<option selected="selected">TURTLE</option>
					<option>Turtle</option>
					<option>TTL</option>
					<option>GRDDL</option>
				</select>
			</div>
			<div class="fm-opt">
				<label for="url">URL:</label>
				<input type="text" name="url" id="url">
			</div>
			<div class="fm-submit">
				<input type="submit" value="Submit data">
			</div>
		</fieldset>
	</form>

	<hr>

	<form action="indexService" method="post">
		<fieldset>
			<legend>Add SPARQL endpoint to index</legend>
			<div class="fm-opt">
				<label for="endpoint_url">Endpoint URL:</label>
				<input type="text" name="endpoint_url" id="endpoint_url">
			</div>
			<div class="fm-opt">
				<label for="ontology_url">Ontology URL:</label>
				<input type="text" name="ontology_url" id="ontology_url">
			</div>
			<div class="fm-submit">
				<input type="submit" value="Submit data">
			</div>
		</fieldset>
	</form>


	<form action="indexService" method="post">
		<fieldset>
			<legend>Remove named graph from index</legend>
			<div class="fm-opt">
				<label for="type2">File type/syntax:</label>
				<select name="remove_type" id="remove_type2">
					<option>RDF/XML</option>
					<option>RDF/XML-ABBREV</option>
					<option>N-TRIPLE</option>
					<option>N-TRIPLES</option>
					<option>N3</option>
					<option selected="selected">TURTLE</option>
					<option>Turtle</option>
					<option>TTL</option>
					<option>GRDDL</option>
				</select>
			</div>
			<div class="fm-opt">
				<label for="url">URL:</label>
				<input type="text" name="remove_url" id="remove_url">
			</div>
			<div class="fm-submit">
				<input type="submit" value="Submit data">
			</div>
		</fieldset>
	</form>
	
		<form action="indexService" method="post">
		<fieldset>
			<legend>Remove SPARQL endpoint from index</legend>
			<div class="fm-opt">
				<label for="endpoint_url">Endpoint URL:</label>
				<input type="text" name="remove_endpoint_url" id="remove_endpoint_url">
			</div>
			<div class="fm-opt">
				<label for="ontology_url">Ontology URL:</label>
				<input type="text" name="remove_ontology_url" id="remove_ontology_url">
			</div>
			<div class="fm-submit">
				<input type="submit" value="Submit data">
			</div>
		</fieldset>
	</form>
</div>

</body>
</html>
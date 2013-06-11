<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
		"http://www.w3.org/TR/html4/loose.dtd">

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="pl.edu.agh.semantic.impl.StorageServiceImpl"%>
<%@ page import="pl.edu.agh.semantic.service.StorageService"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title>TDB storage service</title>

<link rel="stylesheet" type="text/css" href="style.css" />
<script type="text/javascript">
	function toggle() {
		document.forms[0].elements['graph_name'].disabled = (document.forms[0].elements['graph'].value != "named")
	}

	function toggleType() {
		if (document.forms[0].elements['publishType'].value == "dataFile") {
			document.getElementById('dataFileDiv').style.display = 'block';
			document.getElementById('directInputDiv').style.display = 'none';
			document.getElementById('fileTypeDiv').style.display = 'block';
			document.getElementById('graphTypeDiv').style.display = 'block';
			document.getElementById('graphNameDiv').style.display = 'block';
			document.getElementById('endpointUrl').style.display = 'none';
			document.getElementById('graphNameDiv').style.display = 'none';
		}
		if (document.forms[0].elements['publishType'].value == "directInput") {
			document.getElementById('dataFileDiv').style.display = 'none';
			document.getElementById('directInputDiv').style.display = 'block';
			document.getElementById('fileTypeDiv').style.display = 'block';
			document.getElementById('graphTypeDiv').style.display = 'block';
			document.getElementById('graphNameDiv').style.display = 'block';
			document.getElementById('endpointUrl').style.display = 'none';
			document.getElementById('ontologyUrl').style.display = 'none';
		}
		if (document.forms[0].elements['publishType'].value == "url") {
			document.getElementById('dataFileDiv').style.display = 'none';
			document.getElementById('directInputDiv').style.display = 'none';
			document.getElementById('fileTypeDiv').style.display = 'block';
			document.getElementById('graphTypeDiv').style.display = 'none';
			document.getElementById('graphNameDiv').style.display = 'none';
			document.getElementById('endpointUrl').style.display = 'block';
			document.getElementById('ontologyUrl').style.display = 'none';
		}
		if (document.forms[0].elements['publishType'].value == "endpointUrl") {
			document.getElementById('dataFileDiv').style.display = 'none';
			document.getElementById('directInputDiv').style.display = 'none';
			document.getElementById('fileTypeDiv').style.display = 'none';
			document.getElementById('graphTypeDiv').style.display = 'none';
			document.getElementById('graphNameDiv').style.display = 'none';
			document.getElementById('endpointUrl').style.display = 'block';
			document.getElementById('ontologyUrl').style.display = 'block';
		}
	}
</script>
</head>
<body>
	<h1>Storage Node</h1>
	<ul>
		<li><a href="index.jsp">Home</a></li>
	</ul>
	<hr />

	<%
		String servletResult = (String) request
				.getAttribute("servlet-result");
		if (servletResult != null) {
	%>
	<div class="header">Results:</div>
	<div class="results">
		<%=servletResult%>
	</div>
	<hr />
	<%
		}
	%>

	<div id="container" style="width: 666px;">
		<form action="storageService" method="post"
			enctype="multipart/form-data">
			<fieldset>
				<legend>Publish data:</legend>
				<div class="fm-opt">
					<label for="graph">Publish type:</label> <select name="publishType"
						id="publishType" onchange="toggleType();">
						<option selected="selected" value="dataFile">Data File</option>
						<option value="directInput">Direct Input</option>
						<option value="url">URL</option>
						<option value="endpointUrl">SPARQL Endpoint</option>
					</select>
				</div>

				<div class="fm-opt" id="dataFileDiv" style="display:block;">
					<label for="file">Data file:</label> <input type="file" name="file"
						id="file">
				</div>
				<div class="fm-opt" id="directInputDiv" style="display:none;">
					<label for="data">Or direct input:</label>
					<textarea name="data" id="data" cols="70" rows="10"></textarea>
				</div>
				<div class="fm-opt" id="endpointUrl" style="display:none;">
					<label for="endpoint_url">Endpoint URL:</label> <input type="text"
						name="endpoint_url" id="endpoint_url">
				</div>
				<div class="fm-opt" id="ontologyUrl" style="display:none;">
					<label for="ontology_url">Ontology URL:</label> <input type="text"
						name="ontology_url" id="ontology_url">
				</div>
				<div class="fm-opt" id="fileTypeDiv" style="display:block;">
					<label for="type">File type/syntax:</label> <select name="type"
						id="type">
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
				<div class="fm-opt" id="graphTypeDiv" style="display:block;">
					<label for="graph">Graph Type:</label> <select name="graph"
						id="graph" onchange="toggle();">
						<option selected="selected" value="default">default</option>
						<option value="named">named</option>
					</select>
				</div>
				<div class="fm-opt" id="graphNameDiv" style="display:block;">
					<label for="graph_name">Graph Name:</label> <input type="text"
						name="graph_name" id="graph_name" disabled="disabled">
				</div>
				<div class="fm-submit">
					<input type="submit" id="submit" value="Submit data">
				</div>
			</fieldset>
		</form>
		<div class="header">Graphs</div>

		<%
			StorageService storageService = new StorageServiceImpl();
			ServletContext servletContext = getServletContext();
			String configFile = servletContext.getInitParameter("configFile");
			String datasetURI = servletContext.getInitParameter("datasetURI");
			String graphURI = servletContext.getInitParameter("graphURI");
			storageService.init(configFile, datasetURI, graphURI);
		%>
		<div class="body">
			<table>
				<tr>
					<th>Named Graphs</th>
				</tr>
				<%
					for (String graphUrl : storageService.getAllNamedGraphs()) {
						//URL url = new URL(ontologyNodeUrl);
				%>

				<tr>
					<td><a href="<%=graphUrl%>"> <%=graphUrl%>
					</a></td>
					<td>
						<form action="storageService" method="post">
							<input type="hidden" name="graph-name" id="graph-name"
								value="<%=graphUrl%>" /> <input type="submit"
								name="remove-graph" value="Remove" />
						</form>
					</td>
				</tr>
				<%
					}
				%>
			</table>
			<table>
				<tr>
					<th>Default Graph</th>
				</tr>
				<tr>
					<td><a href="<%="./graph/"%>"> <%="graph/"%>
					</a></td>
					<td></td>
				</tr>
			</table>
		</div>

	</div>

</body>
</html>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
		"http://www.w3.org/TR/html4/loose.dtd">
		
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
	<title>TDB storage service</title>

	<link rel="stylesheet" type="text/css" href="style.css"/>
	<script type="text/javascript">
		function toggle() {
			document.forms[0].elements['graph_name'].disabled = (document.forms[0].elements['graph'].value != "named")
		}
	</script>
</head>
<body>
<h1>Storage Node</h1>
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

<div id="container" style="width: 666px;">
	<form action="storageService" method="post"
		  enctype="multipart/form-data">
		<fieldset>
			<legend>Add new data to TDB storage</legend>
			<div class="fm-opt">
				<label for="file">Data file:</label>
				<input type="file" name="file" id="file">
			</div>
			<div class="fm-opt">
				<label for="data">Or direct input:</label>
				<textarea name="data" id="data" cols="70" rows="10"></textarea>
			</div>
			<div class="fm-opt">
				<label for="type">File type/syntax:</label>
				<select name="type" id="type">
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
				<label for="graph">Graph Type:</label>
				<select name="graph" id="graph" onchange="toggle();">
					<option selected="selected" value="default">default</option>
					<option value="named">named</option>
				</select>
			</div>
			<div class="fm-opt">
				<label for="graph_name">Graph Name:</label>
				<input type="text" name="graph_name" id="graph_name" disabled="disabled">
			</div>
			<div class="fm-submit">
				<input type="submit" id="submit" value="Submit data">
			</div>
		</fieldset>
	</form>
</div>

</body>
</html>
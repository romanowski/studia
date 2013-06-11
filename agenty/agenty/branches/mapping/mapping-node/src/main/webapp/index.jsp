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
<h1>Mapper Node</h1>
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
	<form action="mapperService" method="post">
		<fieldset>
			<legend>Set mapping from file</legend>

			<div class="fm-opt">
				<label for="url">URL:</label>
				<input type="file" name="file[]" id="file" multiple="">
			</div>
			<div class="fm-submit">
				<input type="submit" value="Submit data">
			</div>
		</fieldset>
	</form>

	<hr>



	<form action="mapperService" method="post">
    		<fieldset>
    			<legend>Set mapping from URI</legend>

    			<div class="fm-opt">
    				<label for="url">URL:</label>
    				<input type="text" name="url" id="url">
    			</div>
    			<div class="fm-submit">
    				<input type="submit" value="Submit data">
    			</div>
    		</fieldset>
    	</form>



</div>

</body>
</html>
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
<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.net.URL" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head><title>Fast Nodes management</title>
	<link rel="stylesheet" type="text/css" href="style.css"/>
	<script type="text/javascript" src="jquery-1.8.3.min.js"></script>          
	 <script type="text/javascript">                                         
 $(function() {
        var scntDiv = $('#p_scents');
        var i = $('#p_scents p').size() + 1;
        
        $('#addScnt').live('click', function() {
                $('<p><label for="ontology-url_1">Ontology URL:</label><input type="text" id="p_scnt" size="20" name="ontology-url_'+i+'" value=""/><label for="ontology-examples_'+i+'">Examples:</label><input type="text" id="p_scnt" size="20" name="ontology-examples_'+i+'" value=""/><label for="storages_count_'+i+'">Storages count:</label><input type="text" id="p_scnt" size="20" name="storages_count_'+i+'" value=""/><a href="#" id="remScnt">Remove</a></p>').appendTo(scntDiv);
                i++;
                return false;
        });
        
        $('#remScnt').live('click', function() { 
                if( i > 2 ) {
                        $(this).parents('p').remove();
                        i--;
                }
                return false;
        });
});                                 
	 </script>     
</head>

<body style="text-align: left">

<%
	//String queryString = request.getParameter("query");
	response.setContentType("text/html;charset=UTF-8");

	NodesManagementService nodesManagementService = new NodesManagementServiceImpl();
	//queryService.parse(queryString);

%>

<div class="frame">

<h1>Query Node</h1>
<ul>
	<li><a href="index.html">Home</a></li>
	<li><a href="node-content.jsp">Node content</a></li>
	<li><a href="node-management.jsp">Node management</a></li>
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
	<form action="nodesManagementService" method="post">
		<div class="fm-opt">
					<label for="server-url">Server address:</label>
					<input type="text" name="server-url" id="server-url"/>

		</div>


		<div class="fm-opt">
			<div id="p_scents">
			<p>
			        <label for="ontology-url_1">Ontology URL:</label>
			        <input type="text" id="p_scnt" size="20" name="ontology-url_1" value=""/>
					<label for="ontology-examples_1">Examples:</label>
					<input type="text" id="p_scnt" size="20" name="ontology-examples_1" value=""/>
					<label for="storages_count_1">Storages count:</label>
					<input type="text" id="p_scnt" size="20" name="storages_count_1" value=""/>	
			</p>
			
			</div>
			<label for="addScnt"> Add ontology:</label><a href="#" id="addScnt">Add</a>			
		</div>
		<div class="fm-opt">
					<label for="storage-node-name">Storage name:</label>
					<input type="text" name="storage-node-name" id="storage-node-name"  value="storage-node"/>
		</div>
		<div class="fm-opt">
					<label for="node-name">SPARQL name:</label>
					<input type="text" name="SPARQL-name" id="node-name" value="b"/>
		</div>
		<div class="fm-opt">
					<label for="username">Username:</label>
					<input type="text" name="username" id="username"/>
		</div>
		<div class="fm-opt">
					<label for="password">Password:</label>
					<input type="password" name="password" id="password"/>
		</div>
		<div class="fm-submit">
					<input type="submit" name="autogenerate-storage" value="Autogenerate"/>
		</div>
	</form>
	</div>
</div>

</body>
</html>

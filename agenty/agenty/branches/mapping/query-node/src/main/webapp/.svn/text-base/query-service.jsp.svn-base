<%--
  ~ Copyright (c) 2011,2012, Krzysztof Styrc and Tomasz Zdybał
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

<%@ page import="com.hp.hpl.jena.query.*" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="pl.edu.agh.semantic.dto.IndexEntry" %>
<%@ page import="pl.edu.agh.semantic.enums.DataSourceType" %>
<%@ page import="pl.edu.agh.semantic.impl.QueryServiceImpl" %>
<%@ page import="pl.edu.agh.semantic.service.QueryService" %>
<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head><title>SPARQLer</title>
	<link rel="stylesheet" type="text/css" href="style.css"/>
</head>

<body style="text-align: left">

<%
	String queryString = request.getParameter("query");

	response.setContentType("text/html;charset=UTF-8");

	QueryService queryService = new QueryServiceImpl();
	queryService.parse(queryString);

%>

<% if ("debug".equals(request.getParameter("debug"))) { %>

<div class="frame">
	<div class="header">Initial query</div>
	<div class="body">
		<pre class="src"><%= StringEscapeUtils.escapeHtml(queryString) %> </pre>
	</div>
</div>

<div class="frame">
	<div class="header">Parsed prefixes omitting well-known</div>
	<div class="body">
		<table>
			<tr>
				<th>Prefix</th>
				<th>Ontology</th>
			</tr>
			<%
				for (Map.Entry<String, String> prefix : queryService.getPrefixes(true).entrySet()) {
			%>
			<tr>
				<td><%= prefix.getKey() %>
				</td>
				<td><%= prefix.getValue() %>
				</td>
			</tr>
			<%
				}
			%>
		</table>
	</div>
</div>

<div class="frame">
	<div class="header">Data sources for prefixes</div>
	<div class="body">
		<table>
			<tr>
				<th>Ontology</th>
				<th>SPARQL endpoints</th>
				<th>Named graphs</th>
			</tr>
			<% for (Map.Entry<String, Set<IndexEntry>> prefix : queryService.getSourcesURLs().entrySet()) { %>
			<tr>
				<td><%= prefix.getKey() %>
				</td>
				<td>
					<% for (IndexEntry entry : prefix.getValue())
						if (DataSourceType.SPARQL_ENDPOINT.equals(entry.getType())) { %>
					<%=  entry.getUrl() %><br/>
					<% } %>
				</td>
				<td>
					<% for (IndexEntry entry : prefix.getValue())
						if (DataSourceType.RDF_NAMED_GRAPH.equals(entry.getType())) { %>
					<%= entry.getUrl() %><br/>
					<% } %>
				</td>
			</tr>
			<% } %>
		</table>
	</div>
</div>

<div class="frame">
	<div class="header">Processed (modified) query</div>
	<div class="body">
		<pre class="src"><%= StringEscapeUtils.escapeHtml(queryService.getProcessedQuery()) %> </pre>
	</div>
</div>

<% } // end if (debug) %>

<%
	Query query = QueryFactory.create(queryService.getProcessedQuery());
	QueryExecution queryExecution = QueryExecutionFactory.create(query);

	ResultSet results = queryExecution.execSelect();
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	ResultSetFormatter.out(baos, results, query);
%>

<div class="frame">
	<div class="header">Query Results</div>
	<div class="body">
		<pre class="result"><%= new String(baos.toByteArray()) %></pre>
	</div>
</div>

</body>
</html>

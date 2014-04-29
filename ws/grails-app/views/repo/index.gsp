<%@ page import="au.com.adtec.realtime.webservice.repo.FileData" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Repository</title>
</head>

<body>
<h1>Repository</h1>
<p>This module allow users to upload and download files through HTTP request</p>

<h2>Actions</h2>

<h3>Upload</h3>
<ul>
    <li>URL: <code><g:createLink action="upload" absolute="true" /></code></li>
    <li>Method: <code>POST</code></li>
    <li>Header: <code>X-Auth-Token = [Token]</code></li>
    <li>Request Body: <code>multipart/form-data</code></li>
    <li>Response: JSON map of File ID and URL</li>
</ul>
<ul><i>Notes:</i>
    <li><i>Token to be provided by the server</i></li>
    <li><i>Token may have restrictions on how many files can be uploaded</i></li>
</ul>
<p>Sample Header: <pre>X-Auth-Token = ts7qh3171d8ckkv5qj4i5s594dhdj4mv</pre></p>
<p>Sample Response: <pre><pre>{"2":"<g:createLink action="download" id="2" absolute="true" />","3":"<g:createLink action="download" id="3" absolute="true" />"}</pre></pre></p>

<h3>Download</h3>
<ul>
    <li>URL: <code><g:createLink action="download" absolute="true"/>/[id]</code></li>
    <li>Method: <code>GET</code></li>
    <li>Header: <code>X-Auth-Token = [Token]</code></li>
    <li>Response: Binary data of the file</li>
</ul>
<ul><i>Notes:</i>
    <li><i>Token to be provided by the server</i></li>
    <li><i>Token is to a particular file and may be restricted on how many times the resource can be accessed</i></li>
</ul>
<p>Sample Header: <pre>X-Auth-Token = ts7qh3171d8ckkv5qj4i5s594dhdj4mv</pre></p>
<p>Sample Request: <pre>GET: <g:createLink action="download" id="1" absolute="true"/></pre></p>

<h2>Test Upload Form:</h2>
<div>
    <g:uploadForm action="upload">
        <input type="file" name="file" multiple="true" />
        <input type="submit" />
    </g:uploadForm>
    <h2>Resources:</h2>
    <ul>
        <g:each in="${FileData.list()}" var="file">
            <li>${file.id}:<g:link action="download" id="${file.id}">${file.filename}</g:link></li>
        </g:each>
    </ul>
</div>
</body>
</html>
<%@ page import="au.com.adtec.realtime.webservice.repo.FileData" %>

<html>
<head>
    <meta name="layout" content="main">
    <title>Repository</title>
</head>
<body>
<div id="page_heading" class="grid_24" role="page_heading">
    <h1 class="page_title">Repository Test</h1>
</div>
<div id="test_upload_form_container">
    <h3>Test Upload Form</h3>
    <g:uploadForm controller="repo" action="upload">
        <input type="file" name="file" id="file" multiple="true" /><input type="submit" />
    </g:uploadForm>
</div>
<div class="grid_24">
    <ul>
    <g:each in="${FileData.list()}" var="f">
        <li>
            <g:link controller="repo" action="download" id="${f.id}"><img src="${g.createLink(action: 'download', controller: 'repo', id: f.id,  params: [thumb: 300])}"/></g:link>
            <g:link controller="repo" action="download" id="${f.id}">${f.filename}</g:link>
        </li>
    </g:each>
    </ul>
</div>
</body>
</html>
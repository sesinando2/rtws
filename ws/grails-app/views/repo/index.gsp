<html>
<head>
    <meta name="layout" content="main">
    <title>Repository</title>
</head>

<body>
<div id="page_heading" class="grid_24" role="page_heading">
    <h1 class="page_title">Repository</h1>
    <div class="page_nav">
        <g:link uri="/repo/web/info">API Info</g:link>
    </div>
    <g:if test="${flash.message}">
        <div class="message" role="status"><p>${flash.message}</p></div>
    </g:if>
</div>
<div id="list-file" class="grid_ 24 content" role="main">
    <table>
        <thead>
        <tr>

            <g:sortableColumn property="id" title="ID" />

            <g:sortableColumn property="filename" title="File Name" />

            <th>Type</th>

            <g:sortableColumn property="dateCreated" title="Date Create" />

            <sec:ifAnyGranted roles="ROLE_ADMIN, ROLE_REPO_ADMIN">
            <th></th>
            </sec:ifAnyGranted>

            <th></th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${files}" status="i" var="file">
            <tr class="${((i + 1) % 2) == 0 ? 'even' : 'odd'}">

                <td class="center"><g:link controller="repo" action="view" id="${file.id}">${fieldValue(bean: file, field: "id")}</g:link></td>

                <td><g:link controller="repo" action="view" id="${file.id}">${fieldValue(bean: file, field: "filename")}</g:link></td>

                <td class="center">${fieldValue(bean: file, field: "fileType")}</td>

                <td class="center">${fieldValue(bean: file, field: "dateCreated")}</td>

                <sec:ifAnyGranted roles="ROLE_ADMIN, ROLE_REPO_ADMIN">
                    <td class="center"><g:link action="delete" id="${file.id}">Delete</g:link></td>
                </sec:ifAnyGranted>

                <td class="center"><g:link action="download" id="${file.id}">Download</g:link></td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <div class="pagination">
        <g:paginate total="${fileCount ?: 0}" />
    </div>
</div>
<div class="grid_24 section_heading" role="section_heading">
    <h3 class="section_heading">Test Upload Form</h3>
</div>
<sec:ifAnyGranted roles="ROLE_ADMIN, ROLE_REPO_ADMIN">
<div id="repo_test_upload_form_section" class="grid_ 24 content" role="section">
    <g:uploadForm controller="repo" action="upload">
        <div class="center">
            <input type="file" name="file" id="file" multiple="true" /><input type="submit" />
        </div>
    </g:uploadForm>
</div>
</sec:ifAnyGranted>
</body>
</html>
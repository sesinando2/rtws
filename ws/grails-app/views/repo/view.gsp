<html>
<head>
    <meta name="layout" content="default">
    <title>View</title>
</head>
<body>
<div id="repo_view" class="content center" role="main">
    <h1 class="page_title">${file.filename}</h1>
    <sec:ifLoggedIn>
        <g:link uri="/">Home</g:link>
        <g:link action="index">Repo Home</g:link>
        <g:link uri="/logout">Logout</g:link>
    </sec:ifLoggedIn>
    <sec:ifNotLoggedIn>
        <g:if test="${token}">
            <g:link controller="token" action="login" params='[token: token, url: g.createLink(url: "/repo/web/$token/view/$id")]'>Login</g:link>
        </g:if>
        <g:else>
            <g:link uri="/login/auth">Login</g:link>
        </g:else>
    </sec:ifNotLoggedIn>
    <p><g:img class="thumb" uri="${thumb}"/></p>
    <p><g:link action="download" id="${file.id}" params="[token: token]">Click here to download</g:link></p>
</div>
</body>
</html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Token</title>
</head>

<body>
<div id="page_heading" class="grid_24" role="page_heading">
    <h1 class="page_title">Token List</h1>
    <div class="page_nav">
        <g:link action="addToken" params="[type: 'upload']">Generate Upload Token</g:link>
        <g:link action="addToken" params="[type: 'download']">Generate Download Token</g:link>
        <g:link action="addToken" params="[type: 'tracked']">Generate Tracked Download Token</g:link>
        <g:link action="addToken" params="[type: 'message']">Generate Messaging Token</g:link>
    </div>
</div>
<div id="list-restToken" class="grid_ 24 content scaffold-list" role="main">
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <table>
        <thead>
        <tr>

            <g:sortableColumn property="token" title="${message(code: 'restToken.token.label', default: 'Token')}" />

            <g:sortableColumn property="login" title="${message(code: 'restToken.login.label', default: 'Login')}" />

            <g:sortableColumn property="dateCreated" title="${message(code: 'restToken.dateCreated.label', default: 'Date Created')}" />

        </tr>
        </thead>
        <tbody>
        <g:each in="${restTokenInstanceList}" status="i" var="restTokenInstance">
            <tr class="${((i + 1) % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link controller="token" action="view" id="${restTokenInstance.id}">${fieldValue(bean: restTokenInstance, field: "token")}</g:link></td>

                <td>${fieldValue(bean: restTokenInstance, field: "login")}</td>

                <td>${fieldValue(bean: restTokenInstance, field: "dateCreated")}</td>

            </tr>
        </g:each>
        </tbody>
    </table>
    <div class="pagination">
        <g:paginate total="${restTokenInstanceCount ?: 0}" />
    </div>
</div>
</body>
</html>
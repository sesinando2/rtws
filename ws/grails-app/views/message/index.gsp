<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>Message List</title>
</head>
<body>
<div id="page_heading" class="grid_24" role="page_heading">
    <h1 class="page_title">Message List</h1>
    <div class="page_nav"></div>
    <g:if test="${flash.message}">
        <div class="message" role="status"><p>${flash.message}</p></div>
    </g:if>
</div>
<div id="list-message" class="grid_ 24 content" role="main">
    <table>
        <thead>
        <tr>

            <g:sortableColumn property="id" title="ID" />

            <g:sortableColumn property="incidentId" title="Incident ID" />

            <g:sortableColumn property="instanceId" title="Instance ID" />

            <g:sortableColumn property="fromAgentId" title="From Agent ID" />

            <g:sortableColumn property="fromMemberId" title="From Member ID" />

            <g:sortableColumn property="dateCreated" title="${message(code: 'restToken.dateCreated.label', default: 'Date Created')}" />

        </tr>
        </thead>
        <tbody>
        <g:each in="${messages}" status="i" var="message">
            <tr class="${((i + 1) % 2) == 0 ? 'even' : 'odd'}">

                <td class="center"><g:link controller="message" action="view" id="${message.id}">${fieldValue(bean: message, field: "id")}</g:link></td>

                <td>${message.incidentId}</td>

                <td>${message.instanceId}</td>

                <td>${message.fromAgentId}</td>

                <td>${message.fromMemberId}</td>

                <td class="center">${fieldValue(bean: message, field: "dateCreated")}</td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <div class="pagination">
        <g:paginate total="${messageCount ?: 0}" />
    </div>
</div>
</body>
</html>
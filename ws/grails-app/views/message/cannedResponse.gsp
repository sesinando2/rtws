<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="default">
    <title>Canned Response</title>
</head>
<body>
<div id="canned-response" class="content center" role="main">
    <g:if test="${hasResponded}">
        <h2>Message has already expired.</h2>
    </g:if>
    <g:else>
        <h2>You have responded with '${responseText}' to '${message}'.</h2>
    </g:else>
</div>
</body>
</html>
</html>

<g:set var="token" value="${restTokenInstance}" />
<html>
<head>
    <meta name="layout" content="main">
    <title>Token</title>
</head>

<body>
<div id="page_heading" class="grid_24" role="page_heading">
    <h1 class="page_title">Token: ${token.id}</h1>
</div>
<div id="token_details_section" class="grid_ 24 content token_view" role="main">
    <form id="token_details">
        <div>
            <p>${token.token}</p>
            <label>Token Value:</label>
        </div>
        <div>
            <p>${token.dateCreated}</p>
            <label>Date Created:</label>
        </div>
    </form>
</div>
<div class="grid_24 section_heading" role="section_heading">
    <h3 class="section_heading">Authority</h3>
</div>
<div id="token_authority_section" class="grid_ 24 content token_view" role="section">
    <form id="token_authority_details">
        <div>
            <p>${token.login}</p>
            <label>Login:</label>
        </div>
        <div>
            <p>${roles}</p>
            <label>Roles:</label>
        </div>
    </form>
</div>
</body>
</html>
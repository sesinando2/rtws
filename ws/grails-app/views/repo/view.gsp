<html>
<head>
    <meta name="layout" content="default">
    <title>View</title>
</head>
<body>
<div id="view" class="content center" role="main">
    <h1 class="page_title">${file.filename}</h1>
    <p><g:img uri="/repo/web/${token}/${file.id}-thumb-450x450.jpg"/></p>
    <p><g:link action="download" id="${file.id}" params="[token: token]">Click here to download</g:link></p>
</div>
</body>
</html>
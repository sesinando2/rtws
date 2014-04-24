<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>File Container Module</title>
</head>

<body>
<h1>File Container Module</h1>
<h2>Test Upload Form:</h2>
<div>
<g:uploadForm action="upload">
    <input type="file" name="file" multiple="true" />
    <input type="submit" />
</g:uploadForm>
</div>
</body>
</html>
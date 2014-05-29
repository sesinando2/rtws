<%@ page import="au.com.adtec.realtime.webservice.repo.FileData; au.com.adtec.realtime.webservice.messaging.Message; au.com.adtec.realtime.webservice.security.token.RestToken" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title>RTWS</title>
</head>
<body>
<div id="page_heading" class="grid_24" role="page_heading">
    <h1 class="page_title">Home</h1>
</div>
<div id="home" class="grid_24" role="main">
    <div class="content">
        <h2>Welcome to QOCCA Web Service</h2>
        <h3>Repository</h3>
        Number of files: ${FileData.count()}
        <h3>Token Management</h3>
        Number of Tokens: ${RestToken.count()}
        <h3>Messaging</h3>
        Number of Messages: ${Message.count()}
    </div>
</div>
</body>
</html>

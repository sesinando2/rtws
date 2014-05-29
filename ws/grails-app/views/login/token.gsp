<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="login">
  <title></title>
</head>
<body>
    <form action='<g:createLink controller="token" action="login" />' method='POST' id='loginForm'>
        <div>
            <input type='text' name='token' id='token'/>
            <label for='token'>Token:</label>
        </div>

        <div id="buttons_and_links">
            <g:link uri="/login/auth">Login using an account</g:link>
            <input type='submit' id="submit" value='${message(code: "springSecurity.login.button")}'/>
        </div>
    </form>
</body>
</html>
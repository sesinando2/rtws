<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="login">
  <title></title>
</head>
<body>
    <form action='<g:createLink controller="token" action="login" />' method='POST' id='loginFormToken'>
        <div>
            <div class="label">
                <label for='token'>Token:</label>
            </div>
            <div>
                <input type='text' name='token' id='token'/>
            </div>
        </div>
        <div class="buttons_and_links">
            <div class="label"></div>
            <div>
                <g:link uri="/login/auth">Login using an account</g:link>
                <input type='submit' id="submit" value='${message(code: "springSecurity.login.button")}'/>
            </div>
        </div>
    </form>
</body>
</html>
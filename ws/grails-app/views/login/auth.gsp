<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="login">
    <title>Login</title>
</head>
<body>
    <form action='${postUrl}' method='POST' id='loginForm' autocomplete='off'>
        <div>
            <input type='text' class='text_' name='j_username' id='username'/>
            <label for='username'><g:message code="springSecurity.login.username.label"/>:</label>
        </div>

        <div>
            <input type='password' class='text_' name='j_password' id='password'/>
            <label for='password'><g:message code="springSecurity.login.password.label"/>:</label>
        </div>

        <div class="remember_me_container">
            <label for='remember_me'><g:message code="springSecurity.login.remember.me.label"/></label>
            <input type='checkbox' class='chk' name='${rememberMeParameter}' id='remember_me' <g:if test='${hasCookie}'>checked='checked'</g:if>/>
        </div>
        <div id="buttons_and_links">
            <g:link uri="/login/token">Login using token</g:link>
            <input type='submit' id="submit" value='${message(code: "springSecurity.login.button")}'/>
        </div>
    </form>
</body>
</html>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="login">
    <title>Login</title>
</head>
<body>
    <form action='${postUrl}' method='POST' id='loginForm' autocomplete='off'>
        <div>
            <div class="label"><label for='username'><g:message code="springSecurity.login.username.label"/>:</label></div>
            <div><input type='text' class='text_' name='j_username' id='username'/></div>
        </div>

        <div>
            <div class="label"><label for='password'><g:message code="springSecurity.login.password.label"/>:</label></div>
            <div><input type='password' class='text_' name='j_password' id='password'/></div>
        </div>

        <div>
            <div class="label"></div>
            <div>
                <input type='checkbox' class='chk' name='${rememberMeParameter}' id='remember_me' <g:if test='${hasCookie}'>checked='checked'</g:if>/>
                <label for='remember_me'><g:message code="springSecurity.login.remember.me.label"/></label>
            </div>
        </div>
        <div class="buttons_and_links">
            <div class="label"></div>
            <div>
                <g:link uri="/login/token">Login using token</g:link>
                <input type='submit' id="submit" value='${message(code: "springSecurity.login.button")}'/>
            </div>
        </div>
    </form>
</body>
</html>
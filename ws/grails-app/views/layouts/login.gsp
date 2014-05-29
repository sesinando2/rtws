<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><g:layoutTitle default="Grails"/></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
		<link rel="apple-touch-icon" href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">
		<link rel="apple-touch-icon" sizes="114x114" href="${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}">
        <link rel="stylesheet" href="${resource(dir: 'css/v-align', file: 'main.css')}" type="text/css">
        <link rel="stylesheet" href="${resource(dir: 'css/gs/css', file: 'reset.css')}" type="text/css">
        <link rel="stylesheet" href="${resource(dir: 'css/gs/css', file: 'text.css')}" type="text/css">
        <link rel="stylesheet" href="${resource(dir: 'css/gs/css', file: '1008_24_10_10.css')}" type="text/css">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'mobile.css')}" type="text/css">
		<g:layoutHead/>
		<g:javascript library="application"/>		
		<r:layoutResources />
	</head>
	<body>
    <div id="login">
        <div class="login_form_title">
            <div class="banner">
                <g:link uri="/"><img src="${resource(dir: 'images', file: 'qocca_logo.png')}" alt="QOCCA"/></g:link>
            </div>
            <h5><g:message code="springSecurity.login.header"/></h5>
            <g:if test='${flash.message}'>
                <div class='login_message'>${flash.message}</div>
            </g:if>
        </div>
        <g:layoutBody/>
    </div>
	</body>
</html>

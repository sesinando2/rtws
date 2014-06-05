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
        <g:javascript library="jquery"/>
        <g:javascript library="application"/>
		<r:layoutResources />
	</head>
	<body>
        <div class="container_24 header" role="header">
            <div id="logo" role="banner" class="grid_5 logo"><g:link uri="/"><img src="${resource(dir: 'images', file: 'qocca_logo.png')}" alt="QOCCA"/></g:link></div>
            <div class="grid_19 navigation items" role="navigation">
                <g:render template="/layouts/nav" />
            </div>
        </div>
        <div class="container_24">
            <g:layoutBody/>
        </div>
        <div class="container_24">
            <div class="footer" role="contentinfo"></div>
            <div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
            <r:layoutResources />
        </div>
	</body>
</html>

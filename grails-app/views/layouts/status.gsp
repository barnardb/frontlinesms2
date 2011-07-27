<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Settings"/></title>
		<g:layoutHead />
		<g:render template="/css"/>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
		<g:javascript library="jquery" plugin="jquery"/>
	</head>
	<body>
		<g:render template="/tabs"/>
		<g:render template="/flash"/>
		<div id="main">
			<g:render template="/status/list"/>
			<div id="status-content">
				<g:layoutBody />
			</div>		
		</div>
	</body>
</html>

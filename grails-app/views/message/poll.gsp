<%@ page import="frontlinesms2.Contact" %>
<html>
	<head>
		<meta name="layout" content="messages" />
		<title>Poll</title>
	</head>
	<body>
		<g:if test="${messageInstance != null}">
			<g:set var="buttons">
				<g:link action="deleteMessage" params="[messageSection: messageSection, ownerId: ownerInstance.id, messageId: messageInstance.id]">Delete</g:link>
			</g:set>
			<g:render template="message_details" model="${[buttons: buttons]}"/>
		</g:if>
		<h2 id="poll-title">${ownerInstance?.title}</h2>
		<g:if test="$responseList">
			<table id="poll-stats">
				<tbody>
					<g:each in="${responseList}" var="r">
						<tr>
							<td>
								${r.value}
							</td>
							<td>
								${r.count}
							</td>
							<td>
								(${r.percent}%)
							</td>
						</tr>
					</g:each>
				</tbody>
			</table>
		</g:if>
		<g:if test="${messageInstance}">
			<g:render  template="categorize_response"/>
		</g:if>
	</body>
</html>

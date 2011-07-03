<%@page session="false" %>
<%@page import="clime.messadmin.model.ResponseStatusInfo" %>
<%@taglib prefix="core" uri="http://messadmin.sf.net/core" %>
<%@taglib prefix="format" uri="http://messadmin.sf.net/fmt" %>
<format:setBundle basename="clime.messadmin.admin.i18n.responseStatus"/>

<% ResponseStatusInfo statusInfo = ResponseStatusInfo.valueOf(request.getParameter("statusInfo")); %>

<table border="0">
	<tr>
		<td><format:message key="1xx">
				<format:param value="<%= statusInfo.getReponseStatus()[1] %>" />
				<format:param value="<%= (float) statusInfo.getReponseStatus()[1] / statusInfo.getTotalResponses() %>" />
			</format:message></td>
		<td><format:message key="2xx">
				<format:param value="<%= statusInfo.getReponseStatus()[2] %>" />
				<format:param value="<%= (float) statusInfo.getReponseStatus()[2] / statusInfo.getTotalResponses() %>" />
			</format:message></td>
		<td><format:message key="3xx">
				<format:param value="<%= statusInfo.getReponseStatus()[3] %>" />
				<format:param value="<%= (float) statusInfo.getReponseStatus()[3] / statusInfo.getTotalResponses() %>" />
			</format:message></td>
		<td><format:message key="4xx">
				<format:param value="<%= statusInfo.getReponseStatus()[4] %>" />
				<format:param value="<%= (float) statusInfo.getReponseStatus()[4] / statusInfo.getTotalResponses() %>" />
			</format:message></td>
		<td><format:message key="5xx">
				<format:param value="<%= statusInfo.getReponseStatus()[5] %>" />
				<format:param value="<%= (float) statusInfo.getReponseStatus()[5] / statusInfo.getTotalResponses() %>" />
			</format:message></td>
		<td><format:message key="0xx">
				<format:param value="<%= statusInfo.getReponseStatus()[0] %>" />
				<format:param value="<%= (float) statusInfo.getReponseStatus()[0] / statusInfo.getTotalResponses() %>" />
			</format:message></td>
	</tr>
	<tr>
		<td colspan="2"></td>
		<td><format:message key="301_302">
				<format:param value="<%= statusInfo.getResponseStatusRedirect() %>" />
				<format:param value="<%= statusInfo.getResponseStatusRedirectPercent() %>" />
			</format:message></td>
		<td><format:message key="403">
				<format:param value="<%= statusInfo.getResponseStatus403Forbidden() %>" />
				<format:param value="<%= statusInfo.getResponseStatus403ForbiddenPercent() %>" />
			</format:message></td>
		<td colspan="2"></td>
	</tr>
	<tr>
		<td colspan="2"></td>
		<td><format:message key="304">
				<format:param value="<%= statusInfo.getResponseStatus304NotModified() %>" />
				<format:param value="<%= statusInfo.getResponseStatus304NotModifiedPercent() %>" />
			</format:message></td>
		<td><format:message key="404">
				<format:param value="<%= statusInfo.getResponseStatus404NotFound() %>" />
				<format:param value="<%= statusInfo.getResponseStatus404NotFoundPercent() %>" />
			</format:message></td>
		<td colspan="2"></td>
	</tr>
</table>

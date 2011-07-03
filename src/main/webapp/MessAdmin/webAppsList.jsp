<?xml version="1.0" encoding="UTF-8"?>
<%@page session="false" contentType="text/html; charset=UTF-8" %>
<%@page import="java.util.Collection" %>
<%@page import="clime.messadmin.model.IApplicationInfo" %>
<%@page import="clime.messadmin.model.ApplicationInfo"%>
<%@page import="clime.messadmin.core.Constants" %>
<%@taglib prefix="core" uri="http://messadmin.sf.net/core" %>
<%@taglib prefix="format" uri="http://messadmin.sf.net/fmt" %>
<%--!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"--%>
<%--!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd"--%>
<!DOCTYPE html 
     PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%--!DOCTYPE html 
     PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"--%>
<%--!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
 "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"--%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="<%= response.getLocale() %>">
<format:setBundle basename="clime.messadmin.admin.i18n.webAppsList"/>
<% Collection applications = (Collection)request.getAttribute("applications"); %>
<%--c:url value="${pageContext.request.servletPath}" var="submitUrl" scope="page"/--%><%-- can use value="${pageContext.request.servletPath}" because this JSP is include()'ed --%>
<%-- or use directly ${pageContext.request.requestURI} --%>
<% String submitUrl = request.getContextPath() + request.getServletPath(); /* Can use +request.getServletPath() because this JSP is include()'ed */ %>
<head>
	<%@ include file="inc/meta.inc" %>
	<title><format:message key="page.title"/></title>
	<%@ include file="inc/css.inc" %>
	<style type="text/css">
	</style>
	<script type="text/javascript">//<![CDATA[
		function reloadPage() {
			window.location.reload();
		}
		function checkApplications(theElementCB, name) {
			if (hasCheckedCB(theElementCB, name)) {
				return true;
			} else {
				alert('<format:message key="js.err.select_one"/>');
				return false;
			}
		}
	//]]>
	</script>
</head>
<body>

<div id="menu">
[
<a href="<%=submitUrl%>?action=serverInfos"><format:message key="menu.serverInfos"/></a>
|
<format:message key="menu.webAppsList"/>
]
</div>

<h1><format:message key="page.title"/></h1>

<div class="error"><core:out value='<%= request.getAttribute("error") %>'/></div>
<div class="message"><core:out value='<%= request.getAttribute("message") %>'/></div>

<p class="noprint" style="text-align: center;"><button type="button" onclick="window.location.reload()"><format:message key="refresh"/></button></p>

<form action="<%= submitUrl %>" method="post" id="applicationsForm">
	<fieldset><legend><format:message key="list.legend"/></legend>
		<input type="hidden" name="action" id="applicationsFormAction" value="injectApplications" />
		<table id="applicationsListTable" class="strippable" border="1" cellpadding="2" cellspacing="2" width="100%">
			<thead>
				<tr>
					<th><format:message key="list.context"/></th>
					<th><format:message key="list.n_sessions"/></th>
					<th title="<format:message key='list.global_message.TT'/>"><format:message key="list.global_message"/></th>
					<th colspan="2"></th>
				</tr>
			</thead>
			<tbody>
<% IApplicationInfo context; %>
<% boolean hasMessageCapabilities = false; %>
<core:forEach items='<%= applications %>' var="context" varStatus="status">
<% context = (IApplicationInfo) pageContext.getAttribute("context"); %>
				<tr><%-- class="${status.count%2==0?'even':'odd'}"--%>
					<td><label><core:if test="<%=((ApplicationInfo) context).isMessAdminFullMode()%>"><% hasMessageCapabilities = true; %><input type="checkbox" name="applicationIds" value="<core:out value='<%= context.getInternalContextPath() %>'/>" /></core:if><core:out value="<%= context.getContextPath() %>"/></label></td>
					<td style="text-align: right;"><format:formatNumber value="<%= context.getActiveSessionsCount() + context.getPassiveSessionsCount() %>" type="number"/></td>
					<td style="text-align: center;">
						<core:if test="<%= context.getAttribute(Constants.GLOBAL_MESSAGE_KEY) != null %>">M</core:if>
					</td>
					<td style="text-align: center;"><a href="<%= submitUrl %>?action=sessionsList&amp;context=<%= context.getInternalContextPath() %>"><format:message key="list.session_list"/></a></td>
					<td style="text-align: center;"><a href="<%= submitUrl %>?action=webAppStats&amp;context=<%= context.getInternalContextPath() %>"><format:message key="list.webapp_stats"/></a></td>
				</tr>
</core:forEach>
			</tbody>
		</table>
<core:if test="<%=hasMessageCapabilities%>">
		<label><input type="checkbox" onclick="javascript:checkUncheckAllCB(this, 'applicationIds');" /><format:message key="select.all"/></label>
		<table border="0">
			<tr>
				<td>
					<div style="text-align: center;">
						<format:message key="msg.title"/><br />
						<textarea rows="4" cols="70" id="applicationsMessage" name="message"></textarea><br />
						<input type="submit" value="<format:message key='msg.send'/>" title="<format:message key='msg.send.TT'/>" onclick="return checkApplications(this, 'applicationIds');" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<label title="<format:message key='msg.permanent.TT'/>"><input type="checkbox" id="permanent" name="permanent" /><format:message key="msg.permanent"/></label>
					</div>
				</td>
				<td>
					<format:message key="tips.title"/>
					<ul>
						<li><format:message key="tips.1"/></li>
					</ul>
				</td>
			</tr>
		</table>
</core:if>
	</fieldset>
</form>

<jsp:include page="inc/footer.jsp"/>

<%@ include file="inc/js.inc" %>
</body>
</html>
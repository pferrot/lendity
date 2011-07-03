<?xml version="1.0" encoding="UTF-8"?>
<%@page session="false" contentType="text/html; charset=UTF-8" %>
<%@page import="clime.messadmin.model.IApplicationInfo" %>
<%@page import="clime.messadmin.model.ApplicationInfo" %>
<%@page import="clime.messadmin.model.ResponseStatusInfo" %>
<%@page import="clime.messadmin.model.DisplayDataHolder" %>
<%@page import="clime.messadmin.utils.Files"%>
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
<format:setBundle basename="clime.messadmin.admin.i18n.webAppStats"/>
<% IApplicationInfo webAppStats = (IApplicationInfo) request.getAttribute("webAppStats");
   String context = (String) request.getAttribute("context"); %>
<%--c:url value="${pageContext.request.servletPath}" var="submitUrl" scope="page"/--%><%-- can use value="${pageContext.request.servletPath}" because this JSP is include()'ed --%>
<%-- or use directly ${pageContext.request.requestURI} --%>
<% String submitUrl = request.getContextPath() + request.getServletPath(); /* Can use +request.getServletPath() because this JSP is include()'ed */ %>
<head>
	<%@ include file="inc/meta.inc" %>
	<title><format:message key="page.title"><format:param><core:out value="<%= webAppStats.getContextPath() %>"/></format:param></format:message></title>
	<%@ include file="inc/css.inc" %>
	<style type="text/css">
	</style>
	<script type="text/javascript">//<![CDATA[
		function reloadPage() {
			window.location.reload();
		}
	//]]>
	</script>
</head>
<body>

<div id="menu">
[
<a href="<%=submitUrl%>?action=serverInfos"><format:message key="menu.serverInfos"/></a>
|
<a href="<%=submitUrl%>?action=webAppsList"><format:message key="menu.webAppsList"/></a>
|
<format:message key="menu.webAppStats"><format:param><core:out value="<%=webAppStats.getContextPath()%>"/></format:param></format:message>
|
<a href="<%=submitUrl%>?action=sessionsList&amp;context=<%=context%>"><format:message key="menu.sessionsList"><format:param><core:out value="<%=webAppStats.getContextPath()%>"/></format:param></format:message></a>
]
</div>

<h1><format:message key="page.title2"><format:param><core:out value="<%= webAppStats.getContextPath() %>"/></format:param><format:param><core:out value="<%= webAppStats.getServletContextName() %>"/></format:param></format:message></h1>

<div><span class="collapsible" id="webAppStats"></span></div>
<table id="webAppStats-target" style="text-align: left;" border="0">
	<tr>
		<th><format:message key="platform"/></th>
		<td><core:out value="<%= webAppStats.getServerInfo() %>"/></td>
	</tr>
	<tr>
		<th><format:message key="tempDir"/></th>
		<td title='<format:message key="tempDir.usableSpace">
				<format:param value='<%= Files.getUsableSpaceForFile((java.io.File)webAppStats.getAttribute("javax.servlet.context.tempdir"))/1024 %>'/>
			</format:message>'><core:out value='<%= webAppStats.getAttribute("javax.servlet.context.tempdir") %>'/></td>
	</tr>
	<tr>
		<th><format:message key="uptime"/></th>
		<td title="<format:message key='uptime.TT'><format:param><format:formatDate value='<%= webAppStats.getStartupTime() %>' type='both' pattern='yyyy-MM-dd HH:mm:ss'/></format:param></format:message>"><format:formatTimeInterval value="<%= System.currentTimeMillis() - webAppStats.getStartupTime().getTime() %>"/></td>
	</tr>
	<tr>
		<td colspan="2"><hr /></td>
	</tr>
	<tr>
		<th><format:message key="sessions.active"/></th>
		<td><format:formatNumber value="<%= webAppStats.getActiveSessionsCount() %>" type="number"/></td>
	</tr>
	<tr>
		<th><format:message key="sessions.passive"/></th>
		<td><format:formatNumber value="<%= webAppStats.getPassiveSessionsCount() %>" type="number"/></td>
	</tr>
	<tr>
		<th><format:message key="sessions.active.size"/></th>
		<td><format:formatNumber value="<%= webAppStats.getActiveSessionsSize() %>" type="bytes"/></td>
	</tr>
	<tr>
		<th><format:message key="sessions.max_concurrent"/></th>
		<td><format:formatNumber value="<%= webAppStats.getMaxConcurrentSessions() %>" type="number"/></td>
	</tr>
	<tr>
		<th align="right"><format:message key="sessions.max_concurrent.date"/></th>
		<td><format:formatDate value="<%= webAppStats.getMaxConcurrentSessionsDate() %>" type="both" pattern="yyyy-MM-dd HH:mm:ss"/></td>
	</tr>
	<tr>
		<th><format:message key="sessions.total_created"/></th>
		<td><format:formatNumber value="<%= webAppStats.getTotalCreatedSessions() %>" type="number"/></td>
	</tr>
	<tr>
		<td colspan="2"><hr /></td>
	</tr>
	<tr>
		<th><format:message key="hits.total"/></th>
		<td><format:formatNumber value="<%= webAppStats.getHits() %>" type="number"/></td>
	</tr>
	<tr>
		<th><format:message key="request.size.total"/></th>
		<td><span id="request" class="infoballoonable"><format:formatNumber value="<%= webAppStats.getRequestTotalLength() %>" type="bytes"/></span>
			<div id="request-infoballoon" class="infoballoon">
			<table border="0">
				<caption></caption>
				<tr>
					<th><format:message key="max"/></th>
					<td class="number"><format:formatNumber value="<%= webAppStats.getRequestMaxLength() %>" type="bytes"/></td>
				</tr>
				<tr>
					<th><format:message key="mean"/></th>
					<td class="number"><format:formatNumber value="<%= webAppStats.getRequestMeanLength() %>" type="bytes"/></td>
				</tr>
				<tr>
					<th><format:message key="stdDev"/></th>
					<td class="number"><format:formatNumber value="<%= webAppStats.getRequestStdDevLength() %>" type="bytes"/></td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
<core:if test="<%=((ApplicationInfo) webAppStats).isMessAdminFullMode()%>">
	<tr>
		<th><format:message key="response.size.total"/></th>
		<td><span id="response" class="infoballoonable"><format:formatNumber value="<%= webAppStats.getResponseTotalLength() %>" type="bytes"/></span>
			<div id="response-infoballoon" class="infoballoon">
			<table border="0">
				<caption></caption>
				<tr>
					<th><format:message key="max"/></th>
					<td class="number"><format:formatNumber value="<%= webAppStats.getResponseMaxLength() %>" type="bytes"/></td>
				</tr>
				<tr>
					<th><format:message key="mean"/></th>
					<td class="number"><format:formatNumber value="<%= webAppStats.getResponseMeanLength() %>" type="bytes"/></td>
				</tr>
				<tr>
					<th><format:message key="stdDev"/></th>
					<td class="number"><format:formatNumber value="<%= webAppStats.getResponseStdDevLength() %>" type="bytes"/></td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
</core:if>
	<tr>
		<th><format:message key="serverTime"/></th>
		<td><span id="servertime" class="infoballoonable" title="<format:message key='serverTime.TT'><format:param value='<%= webAppStats.getUsedTimeTotal() % 1000 %>'/></format:message>"><format:formatTimeInterval value="<%= webAppStats.getUsedTimeTotal() %>"/></span>
			<div id="servertime-infoballoon" class="infoballoon">
			<table border="0">
				<caption></caption>
				<tr>
					<th><format:message key="max"/></th>
					<td class="number"><format:formatNumber value="<%= webAppStats.getUsedTimeMax() %>" type="number"/> ms</td>
				</tr>
				<tr>
					<th><format:message key="mean"/></th>
					<td class="number"><format:formatNumber value="<%= webAppStats.getUsedTimeMean() %>" type="number"/> ms</td>
				</tr>
				<tr>
					<th><format:message key="stdDev"/></th>
					<td class="number"><format:formatNumber value="<%= webAppStats.getUsedTimeStdDev() %>" type="number"/> ms</td>
				</tr>
			</table>
			</div>
		</td>
	</tr>
<core:if test="<%=((ApplicationInfo) webAppStats).isMessAdminFullMode()%>">
	<tr>
		<th><format:message key="responseStatus"/></th>
		<td>
			<jsp:include page="inc/responseStatus.jsp"><jsp:param name="statusInfo" value="<%= webAppStats.getResponseStatusInfo() %>" /></jsp:include>
		</td>
	</tr>
</core:if>
</table>

<p class="noprint" style="text-align: center;"><button type="button" onclick="window.location.reload()"><format:message key="refresh"/></button></p>

<div class="error"><core:out value='<%= request.getAttribute("error") %>'/></div>
<div class="message"><core:out value='<%= request.getAttribute("message") %>'/></div>

<fieldset>
<legend><span class="collapsible" id="applicationAttributes"><format:message key="servletContext.attributes.legend"/></span></legend>
<table id="applicationAttributes-target" class="strippable" style="text-align: left;" border="1" cellpadding="2" cellspacing="2">
	<caption style="font-variant: small-caps;"><format:message key="servletContext.attributes.caption"><format:param><format:formatNumber value="<%=webAppStats.getAttributes().size()%>" type="number"/></format:param></format:message></caption>
	<thead>
		<tr>
			<th><format:message key="servletContext.attributes.remove"/></th>
			<th><format:message key="servletContext.attributes.size"/></th>
			<th><format:message key="servletContext.attributes.name"/></th>
			<th><format:message key="servletContext.attributes.value"/></th>
		</tr>
	</thead>
	<tbody>
<core:forEach items="<%= webAppStats.getAttributes() %>" var="attribute" varStatus="status">
<%	java.util.Map.Entry attribute = (java.util.Map.Entry) pageContext.getAttribute("attribute"); %>
		<tr><%-- class="${status.count%2==0?'even':'odd'}"--%>
			<td align="center"><form action="<%= submitUrl %>"><div><input type="hidden" name="action" value="removeServletContextAttribute" /><input type="hidden" name="context" value="<core:out value='<%= context %>'/>" /><input type="hidden" name="attributeName" value="<core:out value='<%= attribute.getKey() %>'/>" /><input type="submit" value="<format:message key='remove'/>" /></div></form></td>
			<td><format:formatNumber type="bytes"><core:sizeof object="<%=attribute.getValue()%>"/></format:formatNumber></td>
			<td><core:out value="<%=attribute.getKey()%>"/></td>
			<td><core:outWithClass value="<%=attribute.getValue()%>"/></td>
		</tr>
</core:forEach>
	</tbody>
</table>
</fieldset>

<div id="extraApplicationAttributes">
<% int index = 0; %>
<core:forEach items="<%= webAppStats.getApplicationSpecificData() %>" var="applicationSpecificData" varStatus="status">
<%	DisplayDataHolder applicationSpecificData = (DisplayDataHolder) pageContext.getAttribute("applicationSpecificData");
	String dataTitle = applicationSpecificData.getTitle();
	String dataXHTML = applicationSpecificData.getXHTMLData();
	if (dataTitle != null && dataXHTML != null) {
		++index; %>
	<fieldset>
		<legend><span class="collapsible" id="extraApplicationAttributes-<%=index%>"><%= dataTitle %></span></legend>
		<div id="extraApplicationAttributes-<%=index%>-target"><div id="<%=applicationSpecificData.getHTMLId()%>"><%= dataXHTML %></div></div>
	</fieldset>
<% } %>
</core:forEach>
</div>

<jsp:include page="inc/footer.jsp"/>

<%@ include file="inc/js.inc" %>
</body>
</html>
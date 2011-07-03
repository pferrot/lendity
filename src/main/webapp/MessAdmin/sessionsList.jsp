<?xml version="1.0" encoding="UTF-8"?>
<%@page session="false" contentType="text/html; charset=UTF-8" %>
<%@page import="java.util.Collection" %>
<%@page import="clime.messadmin.taglib.core.Util" %>
<%@page import="clime.messadmin.model.IApplicationInfo" %>
<%@page import="clime.messadmin.model.ISessionInfo" %>
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
<format:setBundle basename="clime.messadmin.admin.i18n.sessionsList"/>
<% IApplicationInfo webAppStats = (IApplicationInfo) request.getAttribute("webAppStats");
   String context = (String) request.getAttribute("context");
   Collection activeSessions = (Collection)request.getAttribute("activeSessions");
   Collection passiveSessionsIds = (Collection)request.getAttribute("passiveSessionsIds"); %>
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
			document.getElementById('sessionsFormAction').value='sessionsList';
			document.getElementById('sessionsForm').method='GET';
			document.getElementById('refreshButton').click();
		}
		function checkSessions(theElementCB, name) {
			if (hasCheckedCB(theElementCB, name)) {
				return true;
			} else {
				alert('<format:message key="js.err.select_one"/>');
				return false;
			}
		}
		function invalidateSessions(theElement) {
			if (checkSessions(theElement, 'sessionIds') && window.confirm('<format:message key="js.invalidate.confirm"/>')) {
				document.getElementById('sessionsFormAction').value='invalidateSessions';
				return true;
			} else {
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
<a href="<%=submitUrl%>?action=webAppsList"><format:message key="menu.webAppsList"/></a>
|
<a href="<%=submitUrl%>?action=webAppStats&amp;context=<%=context%>"><format:message key="menu.webAppStats"><format:param><core:out value="<%=webAppStats.getContextPath()%>"/></format:param></format:message></a>
|
<format:message key="menu.sessionsList"><format:param><core:out value="<%=webAppStats.getContextPath()%>"/></format:param></format:message>
]
</div>

<h1><format:message key="page.title2"><format:param><core:out value="<%= webAppStats.getContextPath() %>"/></format:param></format:message></h1>

<core:if test="<%=((ApplicationInfo) webAppStats).isMessAdminFullMode()%>">
<form action="<%= submitUrl %>" method="post" id="applicationForm">
	<input type="hidden" name="context" value="<%= context %>" />
	<input type="hidden" id="applicationFormAction" name="action" value="injectApplication" />
		<table border="0">
			<tr>
				<td>
					<fieldset><legend><format:message key="msg.app.title"/></legend>
						<label><format:message key="msg.app.label"/><br /><textarea rows="4" cols="70" id="applicationMessage" name="message"></textarea></label><br />
						<input type="submit" value="<format:message key='msg.app.send'/>" />
						<core:if test="<%= webAppStats.getAttribute(Constants.GLOBAL_MESSAGE_KEY) != null %>"><br /><format:message key="msg.app.is_set"/></core:if>
					</fieldset>
				</td>
				<td>
					<format:message key="tips.title"/>
					<ul>
						<li><format:message key="tips.1"/></li>
						<li><format:message key="tips.2"/></li>
					</ul>
				</td>
			</tr>
		</table>
</form>
</core:if>

<div class="error"><core:out value='<%= request.getAttribute("error") %>'/></div>
<div class="message"><core:out value='<%= request.getAttribute("message") %>'/></div>

<form action="<%= submitUrl %>" method="post" id="sessionsForm">
	<input type="hidden" name="context" value="<%= context %>" />
	<input type="hidden" name="action" id="sessionsFormAction" value="injectSessions" />
	<input type="hidden" name="sort" id="sessionsFormSort" value="<core:out value='<%=request.getAttribute("sort")%>'/>" />
	<input type="hidden" name="order" id="sessionsFormSortOrder" value="<core:out value='<%=request.getAttribute("order")%>' default='ASC'/>" />
	<div class="noprint" style="text-align: center;">
		<input type="number" name="autorefresh" id="autorefresh" min="5" title="<format:message key='autorefresh.TT'/>" value="<core:out value='<%=request.getAttribute("autorefresh")%>'/>" size="3" maxlength="3" onchange="setAutorefresh(this); return false;" />
		<input type="submit" name="refresh" id="refreshButton" value="<format:message key='refresh'/>" title="<format:message key='refresh.TT'/>" onclick="document.getElementById('sessionsFormAction').value='sessionsList'; document.getElementById('sessionsForm').method='GET'; return true;" />
	</div>
<core:if test="<%=((ApplicationInfo) webAppStats).isMessAdminFullMode()%>">
	<fieldset><legend><format:message key="list.legend"/></legend>
</core:if>
		<format:message key="list.summary">
			<format:param><format:formatNumber value="<%= activeSessions.size() %>" type="number"/></format:param>
			<format:param><format:formatNumber value="<%= passiveSessionsIds.size() %>" type="number"/></format:param>
		</format:message>
		(<a href="<%= submitUrl %>?action=webAppStats&amp;context=<%=context%>"><format:message key="list.summary.more"/></a>)<br />
		<table id="sessionsListTable" class="strippable" border="1" cellpadding="2" cellspacing="2" width="100%">
			<thead>
				<tr>
					<th><a onclick="document.getElementById('sessionsFormSort').value='id'; document.getElementById('refreshButton').click(); return true;"><format:message key="list.session_id"/></a></th>
<core:if test="<%=((ApplicationInfo) webAppStats).isMessAdminFullMode()%>">
					<th><format:message key="list.message_pending"/></th>
</core:if>
					<th><format:message key="list.remoteHost"/></th>
					<th><a onclick="document.getElementById('sessionsFormSort').value='locale'; document.getElementById('refreshButton').click(); return true;"><format:message key="list.locale"/></a></th>
					<th><a onclick="document.getElementById('sessionsFormSort').value='user'; document.getElementById('refreshButton').click(); return true;"><format:message key="list.username"/></a></th>
					<th><a onclick="document.getElementById('sessionsFormSort').value='CreationTime'; document.getElementById('refreshButton').click(); return true;"><format:message key="list.creation_time"/></a></th>
					<th><a onclick="document.getElementById('sessionsFormSort').value='LastAccessedTime'; document.getElementById('refreshButton').click(); return true;"><format:message key="list.access_time"/></a></th>
					<th><a onclick="document.getElementById('sessionsFormSort').value='UsedTime'; document.getElementById('refreshButton').click(); return true;"><format:message key="list.server_time"/></a></th>
					<th><a onclick="document.getElementById('sessionsFormSort').value='IdleTime'; document.getElementById('refreshButton').click(); return true;"><format:message key="list.idle_time"/></a></th>
					<th><a onclick="document.getElementById('sessionsFormSort').value='TTL'; document.getElementById('refreshButton').click(); return true;"><span title="<format:message key='list.ttl.TT'/>"><format:message key="list.ttl"/></span></a></th>
				</tr>
			</thead>
			<core:if test='<%= activeSessions.size() > 10 %>'>
			<tfoot><%-- <tfoot> is the same as <thead> --%>
				<tr>
					<th><a onclick="document.getElementById('sessionsFormSort').value='id'; document.getElementById('refreshButton').click(); return true;"><format:message key="list.session_id"/></a></th>
<core:if test="<%=((ApplicationInfo) webAppStats).isMessAdminFullMode()%>">
					<th><format:message key="list.message_pending"/></th>
</core:if>
					<th><format:message key="list.remoteHost"/></th>
					<th><a onclick="document.getElementById('sessionsFormSort').value='locale'; document.getElementById('refreshButton').click(); return true;"><format:message key="list.locale"/></a></th>
					<th><a onclick="document.getElementById('sessionsFormSort').value='user'; document.getElementById('refreshButton').click(); return true;"><format:message key="list.username"/></a></th>
					<th><a onclick="document.getElementById('sessionsFormSort').value='CreationTime'; document.getElementById('refreshButton').click(); return true;"><format:message key="list.creation_time"/></a></th>
					<th><a onclick="document.getElementById('sessionsFormSort').value='LastAccessedTime'; document.getElementById('refreshButton').click(); return true;"><format:message key="list.access_time"/></a></th>
					<th><a onclick="document.getElementById('sessionsFormSort').value='UsedTime'; document.getElementById('refreshButton').click(); return true;"><format:message key="list.server_time"/></a></th>
					<th><a onclick="document.getElementById('sessionsFormSort').value='IdleTime'; document.getElementById('refreshButton').click(); return true;"><format:message key="list.idle_time"/></a></th>
					<th><a onclick="document.getElementById('sessionsFormSort').value='TTL'; document.getElementById('refreshButton').click(); return true;"><span title="<format:message key='list.ttl.TT'/>"><format:message key="list.ttl"/></span></a></th>
				</tr>
			</tfoot>
			</core:if>
			<tbody>
<% ISessionInfo currentSession; %>
<core:forEach items="<%=activeSessions%>" var="currentSession" varStatus="status">
<% currentSession = (ISessionInfo) pageContext.getAttribute("currentSession"); %>
				<tr style="<core:if test='<%=currentSession.isSecure()%>'>background-color: #F5F6BE;</core:if>"><%-- class="${status.count%2==0?'even':'odd'}"--%>
					<td>
<input type="checkbox" name="sessionIds" value="<core:out value='<%= currentSession.getId() %>'/>" /><a href="<%=submitUrl%>?action=sessionDetail&amp;context=<%=context%>&amp;sessionId=<%=Util.URLEncode(currentSession.getId(), response.getCharacterEncoding())%>"><core:out value="<%= currentSession.getId() %>"/></a>
					</td>
<core:if test="<%=((ApplicationInfo) webAppStats).isMessAdminFullMode()%>">
					<td style="text-align: center;">
<core:if test="<%= currentSession.getAttribute(Constants.SESSION_MESSAGE_KEY) != null %>">M</core:if>
					</td>
</core:if>
					<td style="text-align: center;" title="<core:out value='<%= currentSession.getUserAgent() %>'/>"><%= currentSession.getRemoteAddr() %></td>
					<td style="text-align: center;"><core:out value="<%= currentSession.getGuessedLocale() %>"/></td>
					<td style="text-align: center;" title="<core:out value='<%= currentSession.getLastRequestURL() %>'/>"><core:out value="<%= currentSession.getGuessedUser() %>"/></td>
					<td style="text-align: center;"><format:formatDate value="<%= currentSession.getCreationTime() %>" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td style="text-align: center;"><format:formatDate value="<%= currentSession.getLastAccessedTime() %>" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td style="text-align: center;"><format:formatTimeInterval value="<%=currentSession.getTotalUsedTime() %>"/></td>
					<td style="text-align: center;"><format:formatTimeInterval value="<%= currentSession.getIdleTime() %>"/></td>
					<td style="text-align: center;"><format:formatTimeInterval value="<%= currentSession.getTTL() %>"/></td>
				</tr>
</core:forEach>
			</tbody>
		</table>
		<label><input type="checkbox" onclick="javascript:checkUncheckAllCB(this, 'sessionIds');" /><format:message key="select.all"/></label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="submit" name="invalidate" value="<format:message key='invalidate'/>" title="<format:message key='invalidate.TT'/>" onclick="return invalidateSessions(this);" />
<core:if test="<%=((ApplicationInfo) webAppStats).isMessAdminFullMode()%>">
		<div style="text-align: center;">
			<format:message key="msg.session.title"/><br />
			<textarea rows="4" cols="70" id="sessionsMessage" name="message"></textarea><br />
			<input type="submit" name="submit" value="<format:message key='msg.session.send'/>" title="<format:message key='msg.session.send.TT'/>" onclick="document.getElementById('sessionsFormAction').value='injectSessions'; return checkSessions(this, 'sessionIds');" />
		</div>
	</fieldset>
</core:if>
</form>

<jsp:include page="inc/footer.jsp"/>

<%@ include file="inc/js.inc" %>
	<script type="text/javascript" defer="defer">//<![CDATA[
		addWindowOnLoadHandler(function() {
			setAutorefresh(document.getElementById('autorefresh'));
		});
	//]]>
	</script>
</body>
</html>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<meta http-equiv="pragma" content="no-cache" /><!-- HTTP 1.0 -->
	<meta http-equiv="cache-control" content="no-cache,must-revalidate" /><!-- HTTP 1.1 -->
	<meta http-equiv="expires" content="0" /><!-- 0 is an invalid value and should be treated as 'now' -->
	<meta http-equiv="content-language" content="<%= response.getLocale() %>" />
	<meta name="author" content="Cedrik LIME" />
	<meta name="copyright" content="copyright 2005--2011 Cedrik LIME" />
	<meta name="robots" content="noindex,nofollow,noarchive" />
	<meta name="format-detection" content="telephone=no"/>
<%-- if (request.getServletPath().toLowerCase().endsWith(".jsp")) { --%>
	<script type="text/javascript">//<![CDATA[
		if (/^.+\.jsp$/gi.test('<%=request.getServletPath()%>')) {
			alert('Please use the MessAdmin administration servlet as defined in web.xml.\nDo not call the JSPs directly!');
		}
	//]]>
	</script>
	<%-- response.flushBuffer(); %>
	<% if(true) throw new JspException("Please use the MessAdmin administration servlet as defined in web.xml. Do not call the JSPs directly!"); %>
<% } --%>
<%
	Exception e = (Exception)request.getAttribute("exception");
%>

<html>
<head>
<title>Initial data</title>
</head>
<body>
<%
if (e == null) {
%>

	Done without any exception.

<%
}
else {
%>
	An exception occurred:<br/><br/>
	<%=e.getStackTrace().toString()%>
<%
}
%>
</body>
</html>
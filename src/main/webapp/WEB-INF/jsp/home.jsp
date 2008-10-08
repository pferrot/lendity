<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<title>Spring MVC Tutorial (Home Page)</title>
</head>
<body>
<h1>Good <c:out value="${greeting}"/>! Welcome to Spring MVC Tutorial</h1>

	The time on the server is <c:out value="${time}"/>

	<b>Here are ten random integers:</b>
	<c:forEach items="${randList}" var="num">
		<c:out value="${num}"/><br/>
	</c:forEach>

<br/><br/>

<table class="footer">
    <tr>
      <td><a href="<c:url value="/welcome.do"/>">Home</a></td>
      <td style="text-align:right;color:silver">PetClinic :: a Spring Framework demonstration</td>
      <td align="right"><img src="<c:url value="/images/springsource-logo.png"/>"/></td>
      <td align="right"><a href="<c:url value="/j_spring_security_logout"/>">Logout</a></td>
    </tr>
</table>

</body>
</html>

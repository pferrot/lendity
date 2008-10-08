<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<table class="footer">
    <tr>
      <td><a href="<c:url value="/welcome.do"/>">Home</a></td>
      <td style="text-align:right;color:silver">PetClinic :: a Spring Framework demonstration</td>
      <td align="right"><img src="<c:url value="/images/springsource-logo.png"/>"/></td>
      <td align="right"><a href="<c:url value="/j_spring_security_logout"/>">Logout</a></td>
    </tr>
</table>

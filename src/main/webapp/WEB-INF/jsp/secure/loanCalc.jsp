<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<html>
<head>
	<title>Calculate Loan</title>
</head>
<body>
<center>
	<!-- one way to display error messages – globally -->
	<spring:hasBindErrors name="loanInfo">
<h3>You have errors in your input!</h3>
<font color="red">
			<c:forEach items="${errors.allErrors}" var="error">
				<spring:message code="${error.code}" text="${error.defaultMessage}"/>
			</c:forEach>
		</font>
	</spring:hasBindErrors>

		<!-- note second way of displaying error messages – by field -->
		<form:form commandName="loanInfo" method="POST" action="loancalc.htm">
			Principal: <form:input path="principal" /><form:errors path="principal" /><br/>
			APR: <form:input path="apr" /><form:errors path="apr" /><br/>
			Number of Years: <form:input path="years" /><form:errors path="years" /><br/>
			Periods Per Year: <form:input path="periodPerYear" /><form:errors path="periodPerYear" /><br/>
			<input type="submit" title="Calculate" />
		</form:form>

</center>

<sec:authorize ifAllGranted="ROLE_SUPERVISOR">
  YES...YOU ARE SUPERVISOR<br/>
</sec:authorize>

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

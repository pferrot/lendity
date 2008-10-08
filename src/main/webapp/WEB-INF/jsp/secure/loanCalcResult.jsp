<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html>
<head>
	<title>Calculate Loan Result</title>
</head>
<body>
<center>

		The installment size is: <fmt:formatNumber value="${loanInfo.payment}" type="currency"/>

<table border="1">
<tr>
<td width="10%">Payment No.</td>
<td align="right" width="30%">Principal</td>
<td align="right" width="30%">Interest</td>
<td align="right" width="30%">Outstanding Principal</td>
</tr>
<c:forEach items="${loanInfo.schedule}" var="entry">
<tr>
<td>${entry.paymentNo}</td>
<td align="right"><fmt:formatNumber value="${entry.principal}" type="currency"/></td>
<td align="right"><fmt:formatNumber value="${entry.interest}" type="currency"/></td>
<td align="right"><fmt:formatNumber value="${entry.outstanding}" type="currency"/></td>
</tr>
</c:forEach></table>
</center>
</body>
</html>

<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<f:view>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD><TITLE>New Account Registration</TITLE>
</HEAD>
<BODY>
<CENTER>
<TABLE BORDER=5>
  <TR><TH CLASS="TITLE">New Account Registration</TH></TR>
</TABLE>
<P>
<h:form>
  Email address: <h:inputText value="#{registrationBean.email}" /><BR>
  Password: <h:inputSecret value="#{registrationBean.password}"/><BR>
  <h:commandButton value="Sign Me Up!" action="#{registrationBean.validate}"/>
</h:form>
</CENTER></BODY></HTML>
</f:view>
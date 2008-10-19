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
<h:message showSummary="true" showDetail="false" style="color: red;" for="username"></h:message>
<h:message showSummary="true" showDetail="false" style="color: red;" for="password"></h:message>
<h:message showSummary="true" showDetail="false" style="color: red;" for="passwordRepeat"></h:message>
<h:message showSummary="true" showDetail="false" style="color: red;" for="firstName"></h:message>
<h:message showSummary="true" showDetail="false" style="color: red;" for="lastName"></h:message>
<h:message showSummary="true" showDetail="false" style="color: red;" for="email"></h:message>

Username: <h:inputText id="username" value="#{registrationBean.username}" required="true" validator="#{registrationBean.validateUsername}"/><br/>
Password: <h:inputSecret id="password" value="#{registrationBean.password}" required="true" /><br/>
Password repeat: <h:inputSecret id="passwordRepeat" value="#{registrationBean.passwordRepeat}" required="true">
</h:inputSecret>
<br/>
First name: <h:inputText id="firstName" value="#{registrationBean.firstName}" required="true"/><br/>
Last name: <h:inputText id="lastName" value="#{registrationBean.lastName}" required="true"/><br/>
Email: <h:inputText id="email" value="#{registrationBean.email}" required="true" >
</h:inputText>
<br/>
  <h:commandButton value="Sign Me Up!" action="#{registrationBean.submit}"/><br/>
</h:form>
</CENTER></BODY></HTML>
</f:view>
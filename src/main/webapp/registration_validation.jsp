<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD><TITLE>Validate?</TITLE>
</HEAD>
<BODY>
<CENTER>
<TABLE BORDER=5>
  <TR><TH CLASS="TITLE">Validate?</TH></TR>
</TABLE>

<f:view>
<h:form>
<h:outputText value="#{registrationBean.username}"/><br/>
<h:commandButton value="Validate !" action="validate"/><br/>
<h:commandButton value="Back !" action="back"/>

</h:form>
</f:view>
</CENTER>
</BODY></HTML>
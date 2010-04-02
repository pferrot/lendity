<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@ page import="org.springframework.security.ui.AbstractProcessingFilter" %>
<%@ page import="org.springframework.security.ui.webapp.AuthenticationProcessingFilter" %>
<%@ page import="org.springframework.security.AuthenticationException" %>

<html>
	<head>
		<title>Login</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta http-equiv="Expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
		<meta http-equiv="Pragma" content="no-cache" />
		
	<link rel="stylesheet" href="<%=request.getContextPath()%>/public/css/reset-fonts.css" type="text/css" media="screen, projection" />
	<link rel="stylesheet" href="<%=request.getContextPath()%>/public/css/gt-styles.css" type="text/css" media="screen, projection" />
	<link rel="stylesheet" href="<%=request.getContextPath()%>/public/css/gt-fixed.css" type="text/css" media="screen, projection" />
	<link rel="stylesheet" href="<%=request.getContextPath()%>/public/css/override.css" type="text/css" media="screen, projection" />
	<link rel="stylesheet" href="<%=request.getContextPath()%>/public/css/custom.css" type="text/css" media="screen, projection" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/public/javascript/jquery/jquery-1.3.2.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/public/javascript/jquery/jquery-ui-1.7.custom.min.js"></script>
	
	<script type="text/javascript">
	  var $j = jQuery.noConflict();
	</script>  
	
	</head>
	<body>
		
		<!-- login form head -->
		<div class="gt-login-hd">
			Login
		</div>
		
		<!-- login box -->
		<div class="gt-login-box">
    
			<!-- login form -->
		  <form name="f" class="gt-form" action="<c:url value='http://localhost:8080/shared_calendar/j_spring_security_check'/>" method="POST">
				<!-- form row -->
				<div class="gt-form-row">
					<label class="gt-login-label">Email</label>
					<%
					if ("true".equals(request.getParameter("loginFailed"))) {
					%>
					<span class="error">Login failed</span>
					<script type="text/javascript">
					  $j(document).ready(function() {
						  $j("#password").css('background-color', '#f8b6a1');
						  $j("#username").css('background-color', '#f8b6a1');        
					  });
					</script>   
					<%
					}
					%>
					<input id="username" type='text' class="gt-form-text" name='j_username' value='<c:if test="${not empty param.loginFailed}"><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></c:if>'/>
				</div><!-- /form row -->
				
				<!-- form row -->
				<div class="gt-form-row">
					<label class="gt-login-label">Password</label>
					<input id="password" type='password' class="gt-form-text" name='j_password'/>
				</div><!-- /form row -->
				
				<!-- form row -->
				<div class="gt-form-row">
					<label class="gt-remember-me"><input type="checkbox" class="gt-form-checkbox" name="_spring_security_remember_me"> Remember me</label>
					
					<input type="submit" value="Login" class="gt-btn-green-medium gt-btn-right" />
				</div>
				<!-- /form row -->
				
				<p class="gt-forgot-password"><a href="<%=request.getContextPath()%>/public/lostpassword/lostpassword.faces">Forgot your password?</a></p>
				<p class="gt-forgot-password"><a href="<%=request.getContextPath()%>/public/registration/registration.faces">Not a member yet?</a></p>
				
			</form><!-- /login form -->
		</div><!-- /login box -->
		
		
	</body>
</html>

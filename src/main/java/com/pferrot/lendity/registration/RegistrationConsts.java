package com.pferrot.lendity.registration;

public interface RegistrationConsts {
	String REGISTRATION_EMAIL_PARAM_NAME = "email";
	String REGISTRATION_CODE_PARAM_NAME = "code";	
	String ACTIVATION_CODE_PARAMETER_NAME = "actiCode";
	String USERNAME_PARAMETER_NAME = "username";
	String PASSWORD_REGEXP = "[\\p{Graph}]{4,50}";
}

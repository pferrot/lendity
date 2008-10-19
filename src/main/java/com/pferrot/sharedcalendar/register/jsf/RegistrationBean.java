package com.pferrot.sharedcalendar.register.jsf;

public class RegistrationBean {
	
	private String email;
	private String password;
	
	
	
	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public String validate() {
		if ("patrice".equals(password)) {
			return "success";		
		}
		else {
			return "ko";
		}
	}
	

}

package com.pferrot.sharedcalendar.lostpassword.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.lostpassword.LostPasswordService;

public class LostPasswordController {
	
	private final static Log log = LogFactory.getLog(LostPasswordController.class);
	
	private String email;	
	private LostPasswordService lostPasswordService;

	public void setLostPasswordService(final LostPasswordService lostPasswordService) {
		this.lostPasswordService = lostPasswordService;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public void sendPassword() {		
		lostPasswordService.sendPassword(getEmail());		
	}	
}

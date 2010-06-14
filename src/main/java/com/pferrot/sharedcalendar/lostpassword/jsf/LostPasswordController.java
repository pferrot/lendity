package com.pferrot.sharedcalendar.lostpassword.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.lostpassword.LostPasswordService;

public class LostPasswordController {
	
	private final static Log log = LogFactory.getLog(LostPasswordController.class);
	
	private final static String CAPTCHA_SESSION_KEY_NAME = "lostPasswordCaptchaSessionKeyName";
	
	private String email;
	private String captcha;
	
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

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public String getCaptchaSessionKeyName() {
        return CAPTCHA_SESSION_KEY_NAME;
    }
	
	public void sendPassword() {
		lostPasswordService.sendPassword(getEmail());		
	}	
}

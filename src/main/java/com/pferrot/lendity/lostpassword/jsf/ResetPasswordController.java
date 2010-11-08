package com.pferrot.lendity.lostpassword.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.lostpassword.LostPasswordConsts;
import com.pferrot.lendity.lostpassword.LostPasswordService;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/public/lostpassword/resetPassword.jspx"})
public class ResetPasswordController {
	
	private final static Log log = LogFactory.getLog(ResetPasswordController.class);
	
	private LostPasswordService lostPasswordService;
	private String username;
	private String resetPasswordCode;
	private boolean resetPasswordSuccessful = false; 	

	@InitView
	public void initView() {
		try {
			username = JsfUtils.getRequestParameter(LostPasswordConsts.USERNAME_PARAMETER_NAME);
			resetPasswordCode = JsfUtils.getRequestParameter(LostPasswordConsts.RESET_PASSWORD_CODE_PARAMETER_NAME);
			getLostPasswordService().updateResetPassword(username, resetPasswordCode);
			resetPasswordSuccessful = true;
		} catch (Exception e) {
			resetPasswordSuccessful = false;
		}
	}

	

	public LostPasswordService getLostPasswordService() {
		return lostPasswordService;
	}

	public void setLostPasswordService(LostPasswordService lostPasswordService) {
		this.lostPasswordService = lostPasswordService;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getResetPasswordCode() {
		return resetPasswordCode;
	}

	public void setResetPasswordCode(String resetPasswordCode) {
		this.resetPasswordCode = resetPasswordCode;
	}

	public boolean isResetPasswordSuccessful() {
		return resetPasswordSuccessful;
	}

	public void setResetPasswordSuccessful(boolean resetPasswordSuccessful) {
		this.resetPasswordSuccessful = resetPasswordSuccessful;
	}
	
}

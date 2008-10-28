package com.pferrot.sharedcalendar.registration.jsf;

import org.apache.myfaces.orchestra.conversation.ConversationUtils;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

@ViewController(viewIds={"/public/registration/registration.jspx", "/public/registration/registration_2.jspx", "/public/registration/registration_3.jspx"})
public class RegistrationViewController
	//implements org.apache.myfaces.orchestra.viewController.ViewController
{

	@InitView
	public void initView() {
		ConversationUtils.ensureConversationRedirect("registrationController", "/public/registration/registration.iface");
	}

//	@PreProcess
//	public void preProcess() {		
//	}
//
//	@PreRenderView
//	public void preRenderView() {		
//	}
}

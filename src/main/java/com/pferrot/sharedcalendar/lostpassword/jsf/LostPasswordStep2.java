package com.pferrot.sharedcalendar.lostpassword.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.ConversationUtils;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

@ViewController(viewIds={"/public/lostpassword/lostpassword_2.jspx"})
public class LostPasswordStep2 {
	
	private final static Log log = LogFactory.getLog(LostPasswordStep2.class);
	
	public LostPasswordStep2() {
		super();
	}

	@InitView
	public void initView() {
		ConversationUtils.ensureConversationRedirect("lostpassword", "/public/lostpassword/lostpassword.faces");
	}	
}

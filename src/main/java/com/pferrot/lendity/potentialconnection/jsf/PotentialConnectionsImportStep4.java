package com.pferrot.lendity.potentialconnection.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.ConversationUtils;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

@ViewController(viewIds={"/auth/potentialconnection/potentialConnectionsImport_4.jspx"})
public class PotentialConnectionsImportStep4 extends AbstractPotentialConnectionsImportStep {
	
	private final static Log log = LogFactory.getLog(PotentialConnectionsImportStep3.class);

	@InitView
	public void initView() {
		ConversationUtils.ensureConversationRedirect("potentialConnectionsImport", "/auth/potentialconnection/potentialConnectionsImport.faces");
	}
}

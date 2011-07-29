package com.pferrot.lendity.potentialconnection.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.ConversationUtils;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

@ViewController(viewIds={"/auth/potentialconnection/potentialConnectionsImport_2.jspx"})
public class PotentialConnectionsImportStep2 extends AbstractPotentialConnectionsImportStep {
	
	private final static Log log = LogFactory.getLog(PotentialConnectionsImportStep2.class);

	@InitView
	public void initView() {
		ConversationUtils.ensureConversationRedirect("potentialConnectionsImport", "/auth/potentialconnection/potentialConnectionsImport.faces");
	}
	
	public String confirm() {
		getPotentialConnectionsImportController().requestConnections();
		return "confirm";
	}
	
	public String skip() {
		getPotentialConnectionsImportController().skipRequestConnections();
		return "confirm";
	}
	
	public String back() {
		return "back";
	}

}

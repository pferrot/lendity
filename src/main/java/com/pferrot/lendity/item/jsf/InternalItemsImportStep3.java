package com.pferrot.lendity.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.ConversationUtils;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

@ViewController(viewIds={"/auth/item/internalItemsImport_3.jspx"})
public class InternalItemsImportStep3 extends AbstractInternalItemsImportStep {
	
	private final static Log log = LogFactory.getLog(InternalItemsImportStep3.class);

	@InitView
	public void initView() {
		ConversationUtils.ensureConversationRedirect("internalItemsImport", "/auth/item/internalItemsImport.faces");
	}
}
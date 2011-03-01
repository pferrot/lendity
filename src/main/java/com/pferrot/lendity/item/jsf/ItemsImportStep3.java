package com.pferrot.lendity.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.ConversationUtils;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

@ViewController(viewIds={"/auth/item/itemsImport_3.jspx"})
public class ItemsImportStep3 extends AbstractItemsImportStep {
	
	private final static Log log = LogFactory.getLog(ItemsImportStep3.class);

	@InitView
	public void initView() {
		ConversationUtils.ensureConversationRedirect("itemsImport", "/auth/item/itemsImport.faces");
	}
}

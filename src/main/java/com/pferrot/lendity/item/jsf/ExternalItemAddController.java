package com.pferrot.lendity.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.model.ExternalItem;

@ViewController(viewIds={"/auth/item/externalItemAdd.jspx"})
public class ExternalItemAddController extends AbstractExternalItemAddEditController {
	
	private final static Log log = LogFactory.getLog(ExternalItemAddController.class);

	@InitView
	public void initView() {
	}

	public Long createItem() {
		ExternalItem externalItem = new ExternalItem();
		
		externalItem.setTitle(getTitle());
		externalItem.setDescription(getDescription());
		externalItem.setOwnerName(getOwnerName());
		externalItem.setBorrowDate(getBorrowDate());
				
		return getItemService().createExternalItemWithCategory(externalItem, getCategoryId());		
	}
	
	@Override
	public Long processItem() {
		return createItem();
	}
}

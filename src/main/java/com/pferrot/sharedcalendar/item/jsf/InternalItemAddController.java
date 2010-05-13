package com.pferrot.sharedcalendar.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.sharedcalendar.model.InternalItem;

@ViewController(viewIds={"/auth/item/internalItemAdd.jspx"})
public class InternalItemAddController extends AbstractInternalItemAddEditController {
	
	private final static Log log = LogFactory.getLog(InternalItemAddController.class);

	@InitView
	public void initView() {
		setVisible(Boolean.TRUE);
	}

	public Long createItem() {
		InternalItem internalItem = new InternalItem();
		
		internalItem.setTitle(getTitle());
		internalItem.setDescription(getDescription());
		internalItem.setVisible(getVisible());
		internalItem.setOwner(getItemService().getCurrentPerson());
				
		return getItemService().createItemWithCategory(internalItem, getCategoryId());		
	}
	
	@Override
	public Long processItem() {
		return createItem();
	}
}

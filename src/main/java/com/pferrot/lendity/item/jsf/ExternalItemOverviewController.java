package com.pferrot.lendity.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.item.ItemUtils;
import com.pferrot.lendity.model.ExternalItem;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/item/externalItemOverview.jspx"})
public class ExternalItemOverviewController extends AbstractItemOverviewController
{
	private final static Log log = LogFactory.getLog(ExternalItemOverviewController.class);
	
	@InitView
	public void initView() {		
		final String itemIdString = JsfUtils.getRequestParameter(PagesURL.EXTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID);
		ExternalItem item = null;
		if (itemIdString != null) {
			setItemId(Long.parseLong(itemIdString));
			item = getItemService().findExternalItem(getItemId());
			// Access control check.
			if (!getItemService().isCurrentUserAuthorizedToView(item)) {
				JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
				if (log.isWarnEnabled()) {
					log.warn("Access denied (external item view): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), item = " + getItemId());
				}
				return;
			}
			setItem(item);
		}	
	}
	
	@Override
	public String getItemEditHref() {		
		return ItemUtils.getExternalItemEditPageUrl(((ExternalItem)getItem()).getId().toString());
	}
}

package com.pferrot.lendity.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.utils.JsfUtils;

public abstract class AbstractInternalItemAddEditController extends AbstractItemAddEditController {
	
	private final static Log log = LogFactory.getLog(AbstractInternalItemAddEditController.class);
	
	private Boolean visible;
		
	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public String submit() {
		Long itemId = processItem();
		
		JsfUtils.redirect(PagesURL.INTERNAL_ITEM_OVERVIEW, PagesURL.INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID, itemId.toString());
	
		// As a redirect is used, this is actually useless.
		return null;
	}
}
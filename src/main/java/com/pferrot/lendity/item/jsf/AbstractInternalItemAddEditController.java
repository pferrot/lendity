package com.pferrot.lendity.item.jsf;

import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

public abstract class AbstractInternalItemAddEditController extends AbstractItemAddEditController {
	
	private final static Log log = LogFactory.getLog(AbstractInternalItemAddEditController.class);
	
	private String infoPublic;
	private String infoConnections;
	private List<SelectItem> visibilitySelectItems;
	private Long visibilityId;

	public String getInfoConnections() {
		return infoConnections;
	}

	public void setInfoConnections(String infoConnections) {
		this.infoConnections = StringUtils.getNullIfEmpty(infoConnections);
	}

	public String getInfoPublic() {
		return infoPublic;
	}

	public void setInfoPublic(String infoPublic) {
		this.infoPublic = StringUtils.getNullIfEmpty(infoPublic);
	}
		
	public List<SelectItem> getVisibilitySelectItems() {
		if (visibilitySelectItems == null) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			visibilitySelectItems = UiUtils.getSelectItemsForOrderedListValue(getItemService().getVisibilities(), locale);
			visibilitySelectItems.add(0, UiUtils.getPleaseSelectSelectItem(locale));
		}		
		return visibilitySelectItems;	
	}

	public Long getVisibilityId() {
		return visibilityId;
	}

	public void setVisibilityId(final Long pVisibilityId) {
		this.visibilityId = UiUtils.getPositiveLongOrNull(pVisibilityId);
	}

	public String submit() {
		Long itemId = processItem();
		
		JsfUtils.redirect(PagesURL.INTERNAL_ITEM_OVERVIEW, PagesURL.INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID, itemId.toString());
	
		// As a redirect is used, this is actually useless.
		return null;
	}
}
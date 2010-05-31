package com.pferrot.sharedcalendar.item.jsf;

import java.util.List;
import java.util.Locale;

import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.i18n.I18nUtils;
import com.pferrot.sharedcalendar.utils.UiUtils;

public abstract class AbstractItemsWithOwnerListController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(AbstractItemsWithOwnerListController.class);
	
	private List<SelectItem> ownerSelectItems;
	private Long ownerId;
	
	public List<SelectItem> getOwnerSelectItems() {
		// TODO reload everytime as connections might be added / removed. What about performance?
//		if (ownerSelectItems == null) {
			ownerSelectItems = UiUtils.getSelectItemsForPerson(getItemService().getCurrentPersonEnabledConnections());
			// Add all owners first.
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			ownerSelectItems.add(0, getAllOwnerSelectItem(locale));
//		}		
		return ownerSelectItems;	
	}

	private SelectItem getAllOwnerSelectItem(final Locale pLocale) {
		final String label = I18nUtils.getMessageResourceString("item_ownerAll", pLocale);
		final SelectItem si = new SelectItem(null, label);
		return si;
	}

    public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = UiUtils.getPositiveLongOrNull(ownerId);
	}

	public String clearOwner() {
		setOwnerId(null);
		return "clearOwner";
	}

	public void owner(final ValueChangeEvent pEevent) {
    	final Long ownerId = (Long) ((HtmlSelectOneMenu) pEevent.getComponent()).getValue();
    	setOwnerId(ownerId);
        // loadDataList() is not called by getList() when the page is submitted with the onchange
        // event on the h:selectOneMenu. Not sure why!?
    	reloadList();
    }

	@Override
	public boolean isFilteredList() {
		final boolean tempResult = getOwnerId() != null;
		return tempResult || super.isFilteredList();
	}
}
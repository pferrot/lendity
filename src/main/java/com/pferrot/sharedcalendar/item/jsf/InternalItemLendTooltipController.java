package com.pferrot.sharedcalendar.item.jsf;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.sharedcalendar.PagesURL;
import com.pferrot.sharedcalendar.item.ItemService;
import com.pferrot.sharedcalendar.utils.JsfUtils;
import com.pferrot.sharedcalendar.utils.UiUtils;

@ViewController(viewIds={"/auth/item/internalItemLendTooltip.jspx"})
public class InternalItemLendTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(InternalItemLendTooltipController.class);
	
	private ItemService itemService;
	
	private List<SelectItem> borrowerSelectItems;
	private Long borrowerId;
	private String borrowerName;
	private Long itemId;
	
	// Default to today.
	private Date borrowDate = new Date();

	@InitView
	public void initView() {
		try {
			final String itemIdString = JsfUtils.getRequestParameter(PagesURL.INTERNAL_ITEM_LEND_TOOLTIP_PARAM_ITEM_ID);
			if (itemIdString != null) {
				setItemId(Long.parseLong(itemIdString));
			}
		}
		catch (Exception e) {
			//TODO display standard error page instead.
			JsfUtils.redirect(PagesURL.ITEMS_LIST);
		}		
	}

	public void setItemService(final ItemService pItemService) {
		this.itemService = pItemService;
	}
	
	public ItemService getItemService() {
		return itemService;
	}

	public List<SelectItem> getBorrowerSelectItems() {
		borrowerSelectItems = UiUtils.getSelectItemsForPerson(getItemService().getCurrentPersonEnabledConnections());
		final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		borrowerSelectItems.add(0, UiUtils.getPleaseSelectSelectItem(locale));	
		return borrowerSelectItems;	
	}

    public Long getBorrowerId() {
		return borrowerId;
	}

	public void setBorrowerId(Long borrowerId) {
		this.borrowerId = UiUtils.getPositiveLongOrNull(borrowerId);
	}

	public String getBorrowerName() {
		return borrowerName;
	}

	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}

	public Date getBorrowDate() {
		return borrowDate;
	}

	public void setBorrowDate(Date borrowDate) {
		this.borrowDate = borrowDate;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String submit() {
		lentItem();
		
		JsfUtils.redirect(PagesURL.MY_ITEMS_LIST);
	
		// As a redirect is used, this is actually useless.
		return null;
	}

	private void lentItem() {
		getItemService().updateLendInternalItem(getItemId(), getBorrowerId(), getBorrowDate());		
	}	
}

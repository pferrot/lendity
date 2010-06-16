package com.pferrot.lendity.item.jsf;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

public class InternalItemLendTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(InternalItemLendTooltipController.class);
	
	private final static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	
	private ItemService itemService;
	
	private List<SelectItem> borrowerSelectItems;
	private Long borrowerId;
	private String borrowerName;
	private Long itemId;
	
	// 1 == my items page
	// 2 == item overview page
	private Long redirectId;
	
	// Default to today.
	private Date borrowDate = new Date();

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
	
	public void setBorrowDateAsString(String borrowDateAsString) {
		try {
			setBorrowDate(DATE_FORMAT.parse(borrowDateAsString));
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getBorrowDateAsString() {
		return DATE_FORMAT.format(getBorrowDate());
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Long getRedirectId() {
		return redirectId;
	}

	public void setRedirectId(Long redirectId) {
		this.redirectId = redirectId;
	}

	public String submit() {
		lendItem();
		
		if (getRedirectId().longValue() == 1) {
			JsfUtils.redirect(PagesURL.MY_ITEMS_LIST);
		}
		else if (getRedirectId().longValue() == 2) {
			JsfUtils.redirect(PagesURL.INTERNAL_ITEM_OVERVIEW, PagesURL.INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID, getItemId().toString());
		}
	
		// As a redirect is used, this is actually useless.
		return null;
	}

	private void lendItem() {		
		if (getBorrowerId() != null && getBorrowerId().longValue() > 0) {
			getItemService().updateLendInternalItem(getItemId(), getBorrowerId(), getBorrowDate());
		}
		else if (!StringUtils.isNullOrEmpty(getBorrowerName())) {
			getItemService().updateLendInternalItem(getItemId(), getBorrowerName().trim(), getBorrowDate());
		}
		else {
			throw new RuntimeException("Neither borrower ID not borrower name is specified");
		}
	}	
}

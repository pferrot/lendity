package com.pferrot.lendity.item.jsf;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.i18n.I18nConsts;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.item.exception.ItemException;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

public class ItemLendTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(ItemLendTooltipController.class);
	
	
	
	private ItemService itemService;
	
	private List<SelectItem> borrowerSelectItems;
	private Long borrowerId;
	private String borrowerName;
	private Long itemId;
	
	// 1 == my items page
	// 2 == item overview page
	private Long redirectId;
	
	private Date borrowDate;
	private Date endDate;

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
	
	public void setBorrowDateAsString(final String pBorrowDateAsString) {
		try {
			if (StringUtils.isNullOrEmpty(pBorrowDateAsString)) {
				setBorrowDate(null);
			}
			else {
				setBorrowDate(I18nConsts.DATE_FORMAT.parse(pBorrowDateAsString));
			}
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getBorrowDateAsString() {
		if (getBorrowDate() == null) {
			return "";
		}
		else {
			return I18nConsts.DATE_FORMAT.format(getBorrowDate());
		}
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public void setEndDateAsString(final String pEndDateAsString) {
		try {
			if (StringUtils.isNullOrEmpty(pEndDateAsString)) {
				setEndDate(null);
			}
			else {
				setEndDate(I18nConsts.DATE_FORMAT.parse(pEndDateAsString));
			}
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getEndDateAsString() {
		if (getEndDate() == null) {
			return "";
		}
		else {
			return I18nConsts.DATE_FORMAT.format(getEndDate());
		}
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
		final Long lendTransactionId = lendItem();
		
//		if (getRedirectId().longValue() == 1) {
//			JsfUtils.redirect(PagesURL.MY_ITEMS_LIST);
//		}
//		else if (getRedirectId().longValue() == 2) {
//			JsfUtils.redirect(PagesURL.ITEM_OVERVIEW, PagesURL.ITEM_OVERVIEW_PARAM_ITEM_ID, getItemId().toString());
//		}
		
		JsfUtils.redirect(
				PagesURL.LEND_TRANSACTION_OVERVIEW,
				PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID,
				lendTransactionId.toString());
	
		// As a redirect is used, this is actually useless.
		return null;
	}

	private Long lendItem() {
		try {
			if (getBorrowerId() != null && getBorrowerId().longValue() > 0) {
				return getItemService().updateLendItem(getItemId(), getBorrowerId(), getBorrowDate(), getEndDate());
			}
			else if (!StringUtils.isNullOrEmpty(getBorrowerName())) {
				return getItemService().updateLendItem(getItemId(), getBorrowerName().trim(), getBorrowDate(), getEndDate());
			}
			else {
				throw new RuntimeException("Neither borrower ID not borrower name is specified");
			}
		}
		catch (ItemException e) {
			throw new RuntimeException(e);
		}
	}	
}

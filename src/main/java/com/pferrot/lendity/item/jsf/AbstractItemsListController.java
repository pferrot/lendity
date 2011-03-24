package com.pferrot.lendity.item.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemConsts;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.item.ItemUtils;
import com.pferrot.lendity.item.ObjektService;
import com.pferrot.lendity.lendrequest.LendRequestConsts;
import com.pferrot.lendity.lendrequest.LendRequestService;
import com.pferrot.lendity.lendtransaction.LendTransactionConsts;
import com.pferrot.lendity.lendtransaction.LendTransactionService;
import com.pferrot.lendity.lendtransaction.exception.LendTransactionException;
import com.pferrot.lendity.lendtransaction.jsf.AbstractLendTransactionsListController;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;
import com.pferrot.security.SecurityUtils;

public abstract class AbstractItemsListController extends AbstractObjektsListController {
	
	private final static Log log = LogFactory.getLog(AbstractItemsListController.class);
	
	private final static String REQUEST_LEND_AVAILABLE_ATTRIUTE_PREFIX = "REQUEST_LEND_AVAILABLE_";
	
	private ItemService itemService;
	private LendRequestService lendRequestService;
	private LendTransactionService lendTransactionService;
	
	private List<SelectItem> borrowStatusSelectItems;
	// Cannot use Boolean because selecting the SelectItem with value null actually
	// sets the value False...
	// 1 = True = borrowed by someone
	// 2 = False = not borrowed
	// null = all items
	private Long borrowStatus;

	// null == all (default)
	// 2 == to lend
	// 3 == to rent
	// 4 == to sell
	// 5 == to give for free
	private Long lendType;
	
	public AbstractItemsListController() {
		super();
	}
	
	public ItemService getItemService() {
		return itemService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	@Override
	protected ObjektService getObjektService() {
		return getItemService();
	}

	@Override
	protected void resetFilters() {
		super.resetFilters();
		setBorrowStatus(null);
		setLendType(null);
		setOrderBy(null);
	}

	public LendRequestService getLendRequestService() {
		return lendRequestService;
	}

	public void setLendRequestService(LendRequestService lendRequestService) {
		this.lendRequestService = lendRequestService;
	}

	public LendTransactionService getLendTransactionService() {
		return lendTransactionService;
	}

	public void setLendTransactionService(
			LendTransactionService lendTransactionService) {
		this.lendTransactionService = lendTransactionService;
	}

	public List<SelectItem> getBorrowStatusSelectItems() {
		if (getBorrowStatusSelectItemsInternal() == null) {
			final List<SelectItem> result = new ArrayList<SelectItem>();
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			
			result.add(new SelectItem(UiUtils.getLongFromBoolean(null), I18nUtils.getMessageResourceString("item_borrowStatusAll", locale)));
			result.add(new SelectItem(UiUtils.getLongFromBoolean(Boolean.TRUE), I18nUtils.getMessageResourceString("item_borrowStatusBorrowed", locale)));
			result.add(new SelectItem(UiUtils.getLongFromBoolean(Boolean.FALSE), I18nUtils.getMessageResourceString("item_borrowStatusNotBorrowed", locale)));
			
			setBorrowStatusSelectItemsInternal(result);
		}		
		return getBorrowStatusSelectItemsInternal();	
	}
	
	protected List<SelectItem> getBorrowStatusSelectItemsInternal() {
		return borrowStatusSelectItems;
	}

	protected void setBorrowStatusSelectItemsInternal(final List<SelectItem> pBorrowStatusSelectItems) {
		this.borrowStatusSelectItems = pBorrowStatusSelectItems;
	}

	public Long getBorrowStatus() {
		return borrowStatus;
	}
	
	public Boolean getBorrowStatusBoolean() {
		return UiUtils.getBooleanFromLong(borrowStatus);
	}

	public void setBorrowStatus(final Long pBorrowStatus) {
		this.borrowStatus = UiUtils.getPositiveLongOrNull(pBorrowStatus);
	}

    public void borrowStatus(final ValueChangeEvent event) {
    	final Long borrowStatus = (Long) ((HtmlSelectOneMenu) event.getComponent()).getValue();
        setBorrowStatus(borrowStatus);
        // loadDataList() is not called by getList() when the page is submitted with the onchange
        // event on the h:selectOneMenu. Not sure why!?
        reloadList();
    }
	
    
	public String clearBorrowStatus() {
		setBorrowStatus(null);
		return "clearBorrowStatus";
	}

	public List<SelectItem> getLendTypeSelectItems() {
		final List<SelectItem> result = new ArrayList<SelectItem>();
		final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		
		result.add(new SelectItem(null, I18nUtils.getMessageResourceString("item_lendTypeAll", locale)));
		result.add(new SelectItem(Long.valueOf(ItemConsts.LEND_TYPE_LEND), I18nUtils.getMessageResourceString("item_lendTypeLend", locale)));
		result.add(new SelectItem(Long.valueOf(ItemConsts.LEND_TYPE_RENT), I18nUtils.getMessageResourceString("item_lendTypeRent", locale)));
		result.add(new SelectItem(Long.valueOf(ItemConsts.LEND_TYPE_GIVE_FOR_FREE), I18nUtils.getMessageResourceString("item_lendTypeGiveForFree", locale)));
		result.add(new SelectItem(Long.valueOf(ItemConsts.LEND_TYPE_SELL), I18nUtils.getMessageResourceString("item_lendTypeSell", locale)));
		
		return result;
	}

	public Long getLendType() {
		return lendType;
	}

	public void setLendType(final Long pLendType) {
		this.lendType = UiUtils.getPositiveLongOrNull(pLendType);
	}

    public void lendType(final ValueChangeEvent event) {
    	final Long lendType = (Long) ((HtmlSelectOneMenu) event.getComponent()).getValue();
        setLendType(lendType);
        // loadDataList() is not called by getList() when the page is submitted with the onchange
        // event on the h:selectOneMenu. Not sure why!?
        reloadList();
    }
	
    
	public String clearLendType() {
		setLendType(null);
		return "clearLendType";
	}

	public String getAvailableLabel() {
		final Item item = (Item)getTable().getRowData();
		if (item.isAvailable()) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			return I18nUtils.getMessageResourceString("item_availableYes", locale);
		}
		else {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			return I18nUtils.getMessageResourceString("item_availableNo", locale);
		}
	}
	
//	public boolean isEditAvailable() {
//		final Item item = (Item)getTable().getRowData();
//		return getItemService().isCurrentUserAuthorizedToEdit(item);
//	}

	public boolean isLendAvailable() {
		final Item item = (Item)getTable().getRowData();
		return getItemService().isCurrentUserAuthorizedToEdit(item);		
	}

	public boolean isLendBackAvailable() {
		final Item item = (Item)getTable().getRowData();
		return getItemService().isCurrentUserAuthorizedToEdit(item) &&
			item.isBorrowed();
	}

//	public boolean isAddAvailable() {
//		return getItemService().isCurrentUserAuthorizedToAdd();
//	}

//	public boolean isDeleteAvailable() {
//		final Item item = (Item)getTable().getRowData();
//		return getItemService().isCurrentUserAuthorizedToDelete(item);
//	}

	public boolean isBorrowed() {
		final Item item = (Item)getTable().getRowData();
		return item != null && item.isBorrowed();
	}

	public String getInProgressLendTransactionUrl() {
		final Item item = (Item)getTable().getRowData();
		try {
			return lendTransactionService.getItemInProgressLendTransactionUrl(item);
		}
		catch (LendTransactionException e) {
			throw new RuntimeException(e);
		}
	}

	public String getBorrowerLabel() {
		final Item item = (Item)getTable().getRowData();
		if (item != null && item.isBorrowed()) {
			if (item.getBorrower() != null) {
				return item.getBorrower().getDisplayName();
			}
			else if (! StringUtils.isNullOrEmpty(item.getBorrowerName())) {
				return item.getBorrowerName();
			}
		}
		return "";
	}

	public String getBorrowDateLabel() {
		final Item item = (Item)getTable().getRowData();
		if (item != null && item.isBorrowed()) {
			return UiUtils.getDateAsString(item.getBorrowDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
		}
		return "";
	}

	public boolean isAvailable() {
		final Item item = (Item)getTable().getRowData();
		if (item instanceof Item) {
			return ((Item) item).isAvailable();
		}
		else {
			return false;
		}
	}
	
	@Override
	public String getCreationDateLabel() {
		final Item item = (Item)getTable().getRowData();
		return UiUtils.getDateAsString(item.getCreationDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}

	public String getBorrowerHref() {
		final Item item = (Item)getTable().getRowData();
		if (item.getBorrower() != null) {
			return PersonUtils.getPersonOverviewPageUrl(item.getBorrower().getId().toString());
		}
		return null;		
	}
	
	public boolean isBorrowerHrefAvailable() {
		final Item item = (Item)getTable().getRowData();
		return item.getBorrower() != null;
	}
	
	public String getItemOverviewHref() {
		final Item item = (Item)getTable().getRowData();
		return ItemUtils.getItemOverviewPageUrl(((Item)item).getId().toString());		
	}

	public boolean isRequestLendAvailable() {
		return getRequestLendAvailableCode() == LendRequestConsts.REQUEST_LEND_ALLOWED;
	}

	public boolean isRequestLendNotAvailableOwnItem() {
		return getRequestLendAvailableCode() == LendRequestConsts.REQUEST_LEND_NOT_ALLOWED_OWN_ITEM;
	}

	public boolean isRequestLendNotAvailableBannedPerson() {
		return getRequestLendAvailableCode() == LendRequestConsts.REQUEST_LEND_NOT_ALLOWED_BANNED_PERSON;
	}

	public boolean isRequestLendNotAvailableNotLoggedIn() {
		return getRequestLendAvailableCode() == LendRequestConsts.REQUEST_LEND_NOT_ALLOWED_NOT_LOGGED_IN;
	}
	
	public boolean isRequestLendNotAvailableUncompletedTransaction() {
		return getRequestLendAvailableCode() == LendRequestConsts.REQUEST_LEND_NOT_ALLOWED_TRANSACTION_UNCOMPLETED;
	}

	public String getRequestLendNotAvailableUncompletedTransactionUrl() {		
		final Item item = (Item)getTable().getRowData();
		final ListWithRowCount lwrc = lendTransactionService.findUncompletedLendTransactionForItemAndBorrower(item.getId(), PersonUtils.getCurrentPersonId(), 0, 0);
		if (lwrc.getRowCount() == 0) {
			return null;
		}
		else if (lwrc.getRowCount() > 1) {
			if (log.isWarnEnabled()) {
				log.warn("Found " + lwrc.getRowCount() + " uncomplted transactions for borrower " + PersonUtils.getCurrentPersonId() + " on " +
						"item " + item.getId() + ". Should have 1 maximum.");
			}
		}
		LendTransaction lt = (LendTransaction)lwrc.getList().get(0);
		return JsfUtils.getFullUrl(
				PagesURL.LEND_TRANSACTION_OVERVIEW,
				PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID,
				lt.getId().toString());
	}

	protected int getRequestLendAvailableCode() {
		final Item item = (Item)getTable().getRowData();
		// Not sure why this is called 3 times per item !? Avoid hitting DB.
		final HttpServletRequest request = JsfUtils.getRequest();
		final Integer requestResult = (Integer)request.getAttribute(REQUEST_LEND_AVAILABLE_ATTRIUTE_PREFIX + item.getId());
		if (requestResult != null) {
			return requestResult.intValue();
		}
		int result = lendRequestService.getLendRequestAllowedFromCurrentUser(item);
		request.setAttribute(REQUEST_LEND_AVAILABLE_ATTRIUTE_PREFIX + item.getId(), Integer.valueOf(result));
		return result;
	}
	
	public String getImage1Src() {
		final Item item = (Item)getTable().getRowData();
		return getItemService().getItemPicture1Src(item, true);
	}
	
	public String getThumbnail1Src() {
		final Item item = (Item)getTable().getRowData();
		return getItemService().getItemThumbnail1Src(item, true);
	}

	public String getMyLendTransactionsUrl() {
		final Item item = (Item)getTable().getRowData();
		return JsfUtils.getFullUrl(
				PagesURL.MY_LEND_TRANSACTIONS_FOR_ITEM_LIST,
				PagesURL.MY_LEND_TRANSACTIONS_FOR_ITEM_LIST_PARAM_ITEM_ID,
				item.getId().toString());
	}

	public long getNbLendTransactionsCurrentPerson() {
		if (! SecurityUtils.isLoggedIn()) {
			throw new RuntimeException("Not logged in");
		}
		final Item item = (Item)getTable().getRowData();
		return lendTransactionService.countLendTransactionsForItemAndPerson(item.getId(), PersonUtils.getCurrentPersonId(), null);
	}

	public String getMyUncompletedLendTransactionsUrl() {
		final Item item = (Item)getTable().getRowData();
		final String[] param1 = new String[]{PagesURL.MY_LEND_TRANSACTIONS_FOR_ITEM_LIST_PARAM_ITEM_ID, item.getId().toString()};
		final String[] param2 = new String[]{AbstractLendTransactionsListController.FORCE_VIEW_PARAM_NAME,
				AbstractLendTransactionsListController.FORCE_VIEW_UNCOMPLETED_LEND_TRANSACTIONS};
		final String[][] params = new String[][]{param1, param2};
		return JsfUtils.getFullUrl(
				PagesURL.MY_LEND_TRANSACTIONS_FOR_ITEM_LIST,
				params);
	}
	
	public long getNbUncompletedLendTransactionsCurrentPerson() {
		if (! SecurityUtils.isLoggedIn()) {
			throw new RuntimeException("Not logged in");
		}
		final Item item = (Item)getTable().getRowData();
		return lendTransactionService.countLendTransactionsForItemAndPerson(item.getId(),
				PersonUtils.getCurrentPersonId(),
				LendTransactionConsts.UNCOMPLETED_STATUS_SELECT_ITEM_VALUE);
	}
	
	public boolean isActiveTransactionsLinkAvailable() {
		final long nb = getNbUncompletedLendTransactionsCurrentPerson();
		if (isBorrowed()) {
			return nb > 1;
		}
		else {
			return nb > 0;
		}
	}

	public boolean isFilteredList() {
		return !StringUtils.isNullOrEmpty(getSearchString()) ||
			getMaxDistance() != null ||
			getCategoryId() != null ||
			getBorrowStatus() != null ||
			getOwnerType() != null ||
			getLendType() != null ||
			getVisibilityId() != null;
	}

	public boolean isShowAdvancedSearch() {
		return getCategoryId() != null || 
			getBorrowStatus() != null ||
			getLendType() != null ||
			getMaxDistance() != null ||
			getOwnerType() != null ||
			getVisibilityId() != null ||
			getOrderBy() != null;
	}
}

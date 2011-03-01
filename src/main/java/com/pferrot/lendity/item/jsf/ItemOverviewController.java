package com.pferrot.lendity.item.jsf;

import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.document.DocumentService;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.item.ItemUtils;
import com.pferrot.lendity.item.ObjektService;
import com.pferrot.lendity.item.exception.ItemException;
import com.pferrot.lendity.lendrequest.LendRequestConsts;
import com.pferrot.lendity.lendrequest.LendRequestService;
import com.pferrot.lendity.lendtransaction.LendTransactionConsts;
import com.pferrot.lendity.lendtransaction.LendTransactionService;
import com.pferrot.lendity.lendtransaction.jsf.AbstractLendTransactionsListController;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.Objekt;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.NavigationUtils;
import com.pferrot.lendity.utils.UiUtils;
import com.pferrot.security.SecurityUtils;

@ViewController(viewIds={"/public/item/itemOverview.jspx"})
public class ItemOverviewController extends AbstractObjektOverviewController {
	
	private final static Log log = LogFactory.getLog(ItemOverviewController.class);
	
	private final static String REQUEST_LEND_AVAILABLE_ATTRIUTE_PREFIX = "REQUEST_LEND_AVAILABLE_";

	private Item item;
	private ItemService itemService;
	private DocumentService documentService;
	private LendRequestService lendRequestService;
	private LendTransactionService lendTransactionService;
	private Long itemId;

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

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}
	
	public ItemService getItemService() {
		return itemService;
	}

	@Override
	protected Objekt getObjekt() {
		return getItem();
	}

	@Override
	protected ObjektService getObjektService() {
		return getItemService();
	}

	public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}
	
	@InitView
	public void initView() {		
		final String itemIdString = JsfUtils.getRequestParameter(PagesURL.ITEM_OVERVIEW_PARAM_ITEM_ID);
		Item item = null;
		if (itemIdString != null) {
			setItemId(Long.parseLong(itemIdString));
			item = getItemService().findItem(getItemId());
			// Access control check.
			if (!getItemService().isCurrentUserAuthorizedToView(item)) {
				if (SecurityUtils.isLoggedIn()) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (item view): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), item = " + getItemId());
					}
				}
				// If the user is not logged in, there is a chance he can access the page once logged in,
				// so we need to redirect him and not just show the access denied page.
				else {
					NavigationUtils.redirectToCurrentPageThroughLogin();
				}
				return;
			}
			setItem(item);
		}	
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
		if (item.getImage1() != null) {
			documentService.authorizeDownloadOneMinute(JsfUtils.getSession(), item.getImage1().getId());
		}
	}

	public String getImage1URL() {
		if (item.getImage1() == null) {
			return null;
		}
		return JsfUtils.getFullUrl(PagesURL.DOCUMENT_DOWNLOAD, PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_ID, item.getImage1().getId().toString());
	}	

	public boolean isBorrowed() {
		return item.isBorrowed();
	}

	public String getInProgressLendTransactionUrl() {
		try {
			return itemService.getItemInProgressLendTransactionUrl(item);
		}
		catch (ItemException e) {
			throw new RuntimeException(e);
		}
	}

	public String getBorrowerLabel() {
		if (item.isBorrowed()) {
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
		if (item.isBorrowed()) {
			return UiUtils.getDateAsString(item.getBorrowDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
		}
		return "";
	}

	public String getBorrowerHref() {
		if (item.getBorrower() != null) {
			return PersonUtils.getPersonOverviewPageUrl(item.getBorrower().getId().toString());
		}
		return null;		
	}
	
	public boolean isBorrowerHrefAvailable() {
		return item.getBorrower() != null;
	}
	
	public boolean isLendAvailable() {
		return isEditAvailable();
	}
	
	public boolean isLendBackAvailable() {
		return isEditAvailable() && getItem().isBorrowed();
	}
	
	public String getItemEditHref() {		
		return ItemUtils.getItemEditPageUrl(((Item)getItem()).getId().toString());
	}
	
	public String getItemEditPictureHref() {		
		return ItemUtils.getItemEditPicturePageUrl(((Item)getItem()).getId().toString());
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

	protected int getRequestLendAvailableCode() {
		// Not sure why this is called 3 times per item (in lister) !? Avoid hitting DB.
		final HttpServletRequest request = JsfUtils.getRequest();
		final Integer requestResult = (Integer)request.getAttribute(REQUEST_LEND_AVAILABLE_ATTRIUTE_PREFIX + item.getId());
		if (requestResult != null) {
			return requestResult.intValue();
		}
		int result = lendRequestService.getLendRequestAllowedFromCurrentUser(item);
		request.setAttribute(REQUEST_LEND_AVAILABLE_ATTRIUTE_PREFIX + item.getId(), Integer.valueOf(result));
		return result;
	}

	public String getImageButtonLabel() {
		final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if (getItem().getImage1() == null) {
			return I18nUtils.getMessageResourceString("image_addImage", locale);
		}
		else {
			return I18nUtils.getMessageResourceString("image_changeImage", locale);
		}
	}
	
	public String getMyLendTransactionsUrl() {
		return JsfUtils.getFullUrl(
				PagesURL.MY_LEND_TRANSACTIONS_FOR_ITEM_LIST,
				PagesURL.MY_LEND_TRANSACTIONS_FOR_ITEM_LIST_PARAM_ITEM_ID,
				item.getId().toString());
	}

	public long getNbLendTransactionsCurrentPerson() {
		if (! SecurityUtils.isLoggedIn()) {
			throw new RuntimeException("Not logged in");
		}
		return lendTransactionService.countLendTransactionsForItemAndPerson(item.getId(), PersonUtils.getCurrentPersonId(), null);
	}

	public String getMyUncompletedLendTransactionsUrl() {
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
		return lendTransactionService.countLendTransactionsForItemAndPerson(item.getId(),
				PersonUtils.getCurrentPersonId(),
				LendTransactionConsts.UNCOMPLETED_STATUS_SELECT_ITEM_VALUE);
	}

	public String getLendButtonLabel() {
		final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if (!item.isBorrowed()) {
			return I18nUtils.getMessageResourceString("item_lend", locale);
		}
		else {
			return I18nUtils.getMessageResourceString("item_lendAgain", locale);
		}
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
}

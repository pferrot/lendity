package com.pferrot.lendity.item;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.dao.ItemDao;
import com.pferrot.lendity.dao.LendRequestDao;
import com.pferrot.lendity.dao.LendTransactionDao;
import com.pferrot.lendity.dao.bean.ItemDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.item.exception.ItemException;
import com.pferrot.lendity.lendtransaction.LendTransactionService;
import com.pferrot.lendity.lendtransaction.exception.LendTransactionException;
import com.pferrot.lendity.model.Document;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.ItemCategory;
import com.pferrot.lendity.model.ItemVisibility;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.model.Objekt;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.ListValueUtils;
import com.pferrot.security.SecurityUtils;

public class ItemService extends ObjektService {

	private final static Log log = LogFactory.getLog(ItemService.class);
	
	private LendRequestDao lendRequestDao;
	private LendTransactionDao lendTransactionDao;
	private LendTransactionService lendTransactionService;
	private ItemDao itemDao;

	public void setLendRequestDao(LendRequestDao lendRequestDao) {
		this.lendRequestDao = lendRequestDao;
	}

	public void setLendTransactionDao(LendTransactionDao lendTransactionDao) {
		this.lendTransactionDao = lendTransactionDao;
	}
	
	public void setLendTransactionService(
			LendTransactionService lendTransactionService) {
		this.lendTransactionService = lendTransactionService;
	}

	public ItemDao getItemDao() {
		return itemDao;
	}

	public void setItemDao(ItemDao itemDao) {
		this.itemDao = itemDao;
	}

	public Item findItem(final Long itemId) {
		return itemDao.findItem(itemId);
	}
	
	public String findItemTitle(final Long itemId) {
		return itemDao.findItem(itemId).getTitle();
	}	
	
	public ListWithRowCount findMyItems(final String pTitle, final Long pCategoryId, final Long pVisibilityId,
			final Boolean pBorrowed, final Long pLendType, final String pOrderBy, final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {		
		return findItems(PersonUtils.getCurrentPersonId(), pTitle, pCategoryId, getVisibilityIds(pVisibilityId), pBorrowed, pLendType, pOrderBy, pOrderByAscending, pFirstResult, pMaxResults);
	}

	public ListWithRowCount findItems(final Long pPersonId, final String pTitle, final Long pCategoryId, final Long[] pVisibilityIds,
			final Boolean pBorrowed, final Long pLendType, final String pOrderBy, final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
		final Long currentPersonId = pPersonId;
		CoreUtils.assertNotNull(currentPersonId);
		final Long[] personIds = new Long[]{currentPersonId};
		
		final ItemDaoQueryBean itemQuery = new ItemDaoQueryBean();
		itemQuery.setOwnerIds(personIds);
		itemQuery.setOwnerEnabled(Boolean.TRUE);
		itemQuery.setTitle(pTitle);
		itemQuery.setCategoryIds(getCategoryIds(pCategoryId));
		itemQuery.setVisibilityIds(pVisibilityIds);
		itemQuery.setBorrowed(pBorrowed);
		itemQuery.setOrderBy(pOrderBy);
		itemQuery.setOrderByAscending(pOrderByAscending);
		itemQuery.setFirstResult(pFirstResult);
		itemQuery.setMaxResults(pMaxResults);
		if (pLendType != null) {
			if (pLendType.equals(ItemConsts.LEND_TYPE_LEND)) {
				itemQuery.setToLend();
			}
			else if (pLendType.equals(ItemConsts.LEND_TYPE_RENT)) {
				itemQuery.setToRent();			
			}
			else if (pLendType.equals(ItemConsts.LEND_TYPE_SELL)) {
				itemQuery.setToSell();
			}
			else if (pLendType.equals(ItemConsts.LEND_TYPE_GIVE_FOR_FREE)) {
				itemQuery.setToGiveForFree();
			}
		}
		
		
		return itemDao.findItems(itemQuery);
	}

	public ListWithRowCount findItems(final String pTitle, final Long pCategoryId, final Boolean pBorrowed, final Long pOwnerType,
			final Double pMaxDistanceInKm, final Long pLendType, final String pOrderBy, final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
				
		final ItemDaoQueryBean itemQuery = new ItemDaoQueryBean();
		
		itemQuery.setOwnerEnabled(Boolean.TRUE);
		itemQuery.setTitle(pTitle);
		itemQuery.setCategoryIds(getCategoryIds(pCategoryId));
		if (pLendType != null) {
			if (pLendType.equals(ItemConsts.LEND_TYPE_LEND)) {
				itemQuery.setToLend();
			}
			else if (pLendType.equals(ItemConsts.LEND_TYPE_RENT)) {
				itemQuery.setToRent();			
			}
			else if (pLendType.equals(ItemConsts.LEND_TYPE_SELL)) {
				itemQuery.setToSell();
			}
			else if (pLendType.equals(ItemConsts.LEND_TYPE_GIVE_FOR_FREE)) {
				itemQuery.setToGiveForFree();
			}
		}
		if (SecurityUtils.isLoggedIn()) {
			// All connections.
			final Long[] connectionsIds = getPersonService().getCurrentPersonConnectionIds(null);
			itemQuery.setOwnerIds(connectionsIds);
			itemQuery.setVisibilityIds(getConnectionsAndPublicVisibilityIds());
			if (!ItemConsts.OWNER_TYPE_CONNECTIONS.equals(pOwnerType)) {
				itemQuery.setVisibilityIdsToForce(ListValueUtils.getIdsArray(getPublicVisibilityId()));
				itemQuery.setOwnerIdsToExcludeForVisibilityIdsToForce(ListValueUtils.getIdsArray(PersonUtils.getCurrentPersonId()));
			}
		}
		// When not logged in - only show public items.
		else {
			itemQuery.setVisibilityIds(new Long[]{getPublicVisibilityId()});
		}
		itemQuery.setBorrowed(pBorrowed);		
		if (pMaxDistanceInKm != null) {
			final Double latitude = getCurrentPerson().getAddressHomeLatitude();
			final Double longitude = getCurrentPerson().getAddressHomeLongitude();			
			if (latitude == null || longitude == null) {
				throw new RuntimeException("Can only search by distance if geolocation is available.");
			}
			itemQuery.setMaxDistanceKm(pMaxDistanceInKm);
			itemQuery.setOriginLatitude(latitude);
			itemQuery.setOriginLongitude(longitude);
		}
		itemQuery.setOrderBy(pOrderBy);
		itemQuery.setOrderByAscending(pOrderByAscending);
		itemQuery.setFirstResult(pFirstResult);
		itemQuery.setMaxResults(pMaxResults);
		
		return itemDao.findItems(itemQuery);
	}

	public ListWithRowCount findPersonItems(final Long pOwnerId, final String pTitle, final Long pCategoryId, final Boolean pBorrowed, final String pOrderBy, final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
				
		CoreUtils.assertNotNull(pOwnerId);
		
		final ItemDaoQueryBean itemQuery = new ItemDaoQueryBean();
		
		itemQuery.setOwnerIds(getIds(pOwnerId));
		itemQuery.setOwnerEnabled(Boolean.TRUE);
		itemQuery.setTitle(pTitle);
		itemQuery.setCategoryIds(getCategoryIds(pCategoryId));
		
		if (SecurityUtils.isLoggedIn() &&
		    getPersonService().isConnection(pOwnerId, PersonUtils.getCurrentPersonId())) {		
			itemQuery.setVisibilityIds(getConnectionsAndPublicVisibilityIds());
		}
		// When not logged in - only show public items.
		else {
			itemQuery.setVisibilityIds(new Long[]{getPublicVisibilityId()});
		}
		itemQuery.setBorrowed(pBorrowed);		
		itemQuery.setOrderBy(pOrderBy);
		itemQuery.setOrderByAscending(pOrderByAscending);
		itemQuery.setFirstResult(pFirstResult);
		itemQuery.setMaxResults(pMaxResults);
		
		return itemDao.findItems(itemQuery);
	}
	
	public ListWithRowCount findMyLatestConnectionsItems() {
		Long[] connectionsIds = getPersonService().getCurrentPersonConnectionIds(null);
		
		final ItemDaoQueryBean itemQuery = new ItemDaoQueryBean();
		itemQuery.setOwnerIds(connectionsIds);
		itemQuery.setOwnerEnabled(Boolean.TRUE);
		itemQuery.setVisibilityIds(getConnectionsAndPublicVisibilityIds());
		itemQuery.setOrderBy("creationDate");
		itemQuery.setOrderByAscending(Boolean.FALSE);
		itemQuery.setFirstResult(0);
		itemQuery.setMaxResults(5);
		
		return itemDao.findItems(itemQuery);
	}
	
	public ListWithRowCount findPersonLatestConnectionsItemsSince(final Person pPerson, final Date pDate) {
		Long[] connectionsIds = getPersonService().getPersonConnectionIds(pPerson, null);
		
		final ItemDaoQueryBean itemQuery = new ItemDaoQueryBean();
		itemQuery.setOwnerIds(connectionsIds);
		itemQuery.setOwnerEnabled(Boolean.TRUE);
		itemQuery.setVisibilityIds(getConnectionsAndPublicVisibilityIds());
		itemQuery.setCreationDateMin(pDate);
		itemQuery.setOrderBy("creationDate");
		itemQuery.setOrderByAscending(Boolean.FALSE);
		itemQuery.setFirstResult(0);
		itemQuery.setMaxResults(5);
		
		return itemDao.findItems(itemQuery);
	}

	/**
	 * When an item is no more lent.
	 *
	 * @param pItemId
	 */
	public void updateLendBackItem(final Long pItemId) {
		final Item item = findItem(pItemId);
		assertCurrentUserAuthorizedToEdit(item);
		final Person borrower = item.getBorrower();
		item.setLendBack();
		updateItem(item);
		// Send email only if was lent to a user of the system.
		if (borrower != null) {
			// Send email (will actually create a JMS message, i.e. it is async).
			Map<String, String> objects = new HashMap<String, String>();
			objects.put("borrowerFirstName", borrower.getFirstName());
			objects.put("lenderFirstName", PersonUtils.getCurrentPersonFirstName());
			objects.put("lenderLastName", PersonUtils.getCurrentPersonLastName());
			objects.put("lenderDisplayName", PersonUtils.getCurrentPersonDisplayName());
			objects.put("itemTitle", item.getTitle());
			objects.put("signature", Configuration.getSiteName());
			objects.put("siteName", Configuration.getSiteName());
			objects.put("siteUrl", Configuration.getRootURL());
			
			// TODO: localization
			final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/lend/lendback/fr";
			
			Map<String, String> to = new HashMap<String, String>();
			to.put(borrower.getEmail(), borrower.getEmail());
			
			Map<String, String> inlineResources = new HashMap<String, String>();
			inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.gif");
			
			getMailManager().send(Configuration.getNoReplySenderName(), 
					         Configuration.getNoReplyEmailAddress(),
					         to,
					         null, 
					         null,
					         Configuration.getSiteName() + ": objet rendu",
					         objects, 
					         velocityTemplateLocation,
					         inlineResources);
			
		}
	}

	/**
	 * Lend to a user of the system.
	 * 
	 * @param pItemId
	 * @param pBorrowerId
	 * @param pBorrowDate
	 * @throws ItemException 
	 */
	public Long updateLendItem(final Long pItemId, final Long pBorrowerId, final Date pBorrowDate, final Date pEndDate) throws ItemException {
		try{
			if (log.isInfoEnabled()) {
				log.info("Lending item to borrower ID: " + pBorrowerId);
			}
			final Item item = findItem(pItemId);
			assertCurrentUserAuthorizedToEdit(item);
			
			final Person borrower = getPersonService().findPerson(pBorrowerId);
			
		    final boolean itemAlreadyBorrowed = item.isBorrowed();
			
			Long lendTransactionId = lendTransactionService.createLendTransaction(borrower, null, item, null,
					pBorrowDate, pEndDate, Boolean.valueOf(itemAlreadyBorrowed));
			
			final Date now = new Date();
			
			// Only mark as borrowed and send email dates actually indicate it is
			// currently lent.
			if (!itemAlreadyBorrowed &&
				(pBorrowDate == null || pBorrowDate.before(now)) &&
				(pEndDate == null || pEndDate.after(now))) {
				// TODO: check if enabled and allowed to borrow !?
				item.setBorrowed(borrower, pBorrowDate);
				updateItem(item);
				
				// Send email (will actually create a JMS message, i.e. it is async).
				Map<String, String> objects = new HashMap<String, String>();
				objects.put("borrowerFirstName", borrower.getFirstName());
				objects.put("lenderFirstName", PersonUtils.getCurrentPersonFirstName());
				objects.put("lenderLastName", PersonUtils.getCurrentPersonLastName());
				objects.put("lenderDisplayName", PersonUtils.getCurrentPersonDisplayName());
				objects.put("itemTitle", item.getTitle());
				objects.put("lendTransactionUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(),
						PagesURL.LEND_TRANSACTION_OVERVIEW,
						PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID,
						lendTransactionId.toString()));
				objects.put("signature", Configuration.getSiteName());
				objects.put("siteName", Configuration.getSiteName());
				objects.put("siteUrl", Configuration.getRootURL());
				
				// TODO: localization
				final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/lend/lend/fr";
				
				Map<String, String> to = new HashMap<String, String>();
				to.put(borrower.getEmail(), borrower.getEmail());
				
				Map<String, String> inlineResources = new HashMap<String, String>();
				inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.gif");
				
				getMailManager().send(Configuration.getNoReplySenderName(), 
						         Configuration.getNoReplyEmailAddress(),
						         to,
						         null, 
						         null,
						         Configuration.getSiteName() + ": objet emprunté",
						         objects, 
						         velocityTemplateLocation,
						         inlineResources);
			}
			return lendTransactionId;
		}
		catch (LendTransactionException e) {
			throw new ItemException(e);
		}
	}

	/**
	 * Lend to someone not using the system.
	 * 
	 * @param pItemId
	 * @param pBorrowerId
	 * @param pBorrowDate
	 * @param pEndDate
	 * @throws ItemException 
	 */
	public Long updateLendItem(final Long pItemId, final String pBorrowerName, final Date pBorrowDate, final Date pEndDate)
			throws ItemException {
		try {
			if (log.isInfoEnabled()) {
				log.info("Lending item to borrower name: " + pBorrowerName);
			}
			final Item item = findItem(pItemId);
			final boolean itemAlreadyBorrowed = item.isBorrowed();
			assertCurrentUserAuthorizedToEdit(item);
			item.setBorrowerName(pBorrowerName);
			item.setBorrower(null);
			item.setBorrowDate(pBorrowDate);
			updateItem(item);
			return lendTransactionService.createLendTransaction(null, pBorrowerName, item, null,
					pBorrowDate, pEndDate, Boolean.valueOf(itemAlreadyBorrowed));
			// No email to send out since the borrower is not a user of the system.
		}
		catch (LendTransactionException e) {
			throw new ItemException(e);
		}
	}

	public void updateSendReminderItem(final Long pItemId) {
		if (log.isInfoEnabled()) {
			log.info("Sending reminder for item: " + pItemId);
		}
		final Item item = findItem(pItemId);
		assertCurrentUserAuthorizedToEdit(item);
		
		if (!item.isBorrowed()) {
			return;			
		}
		
		final Person borrower = item.getBorrower();
		if (borrower == null) {
			return;
		}
		
		item.reminderSentNow();		
		updateItem(item);
		
		// Send email (will actually create a JMS message, i.e. it is async).
		Map<String, String> objects = new HashMap<String, String>();
		objects.put("borrowerFirstName", borrower.getFirstName());
		objects.put("lenderFirstName", PersonUtils.getCurrentPersonFirstName());
		objects.put("lenderLastName", PersonUtils.getCurrentPersonLastName());
		objects.put("lenderDisplayName", PersonUtils.getCurrentPersonDisplayName());
		objects.put("itemTitle", item.getTitle());
		objects.put("signature", Configuration.getSiteName());
		objects.put("siteName", Configuration.getSiteName());
		objects.put("siteUrl", Configuration.getRootURL());
		
		// TODO: localization
		final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/lend/reminder/fr";
		
		Map<String, String> to = new HashMap<String, String>();
		to.put(borrower.getEmail(), borrower.getEmail());
		
		Map<String, String> inlineResources = new HashMap<String, String>();
		inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.gif");
		
		getMailManager().send(Configuration.getNoReplySenderName(), 
				         Configuration.getNoReplyEmailAddress(),
				         to,
				         null, 
				         null,
				         Configuration.getSiteName() + ": rappel pour objet emprunté",
				         objects, 
				         velocityTemplateLocation,
				         inlineResources);
	}
	
	public Long createItem(final Item pItem, final Need pNeed) {
		try {
			pItem.setCreationDate(new Date());
			if (pNeed != null && 
				pItem instanceof Item) {
				((Item)pItem).addRelatedNeed(pNeed);
			}
			final Long result = itemDao.createItem(pItem);
			// Send the notification after the dao is called !
			if (pNeed != null && 
				pItem instanceof Item) {
				final Item item = (Item)pItem;
				if (item.isPublicVisibility() || 
					(item.isConnectionsVisibility() && pItem.getOwner().getConnections().contains(pNeed.getOwner()))) {
					sendNotificationForNeed(pNeed, item);
				}
			}
			return result;
		}
		catch (ItemException e) {
			throw new RuntimeException(e);
		}
	}
 	
	public void deleteItem(final Item pItem)  {
		assertCurrentUserAuthorizedToDelete(pItem);
		// Delete lend requests and lend transactions.
		lendRequestDao.deleteLendRequestsForItem(pItem.getId());
		lendTransactionDao.deleteLendTransactionsForItem(pItem.getId());
		itemDao.deleteItem(pItem);
	}
	
	public void deleteItem(final Long pItemId) {
		deleteItem(itemDao.findItem(pItemId));
	}
	
	public Long createItem(final Item pItem, final Long pCategoryId, final Long pVisibilityId) {
		return createItem(pItem, pCategoryId, pVisibilityId, null);
	}

	public Long createItem(final Item pItem, final Long pCategoryId, final Long pVisibilityId, final Need pNeed) {
		pItem.setCategory((ItemCategory) ListValueUtils.getListValueFromId(pCategoryId, getListValueDao()));
		pItem.setVisibility((ItemVisibility) ListValueUtils.getListValueFromId(pVisibilityId, getListValueDao()));
		return createItem(pItem, pNeed);
	}

	public void updateItem(final Item item) {
		itemDao.updateItem(item);
	}

	public void updateItem(final Item pItem, final Long pCategoryId, final Long pVisibilityId) {
		assertCurrentUserAuthorizedToEdit(pItem);
		pItem.setVisibility((ItemVisibility) ListValueUtils.getListValueFromId(pVisibilityId, getListValueDao()));
		updateItem(pItem, pCategoryId);
	}

	public void updateItem(final Item pItem, final Long pCategoryId) {
		assertCurrentUserAuthorizedToEdit(pItem);
		pItem.setCategory((ItemCategory) ListValueUtils.getListValueFromId(pCategoryId, getListValueDao()));
		updateItem(pItem);
	}

	public void updateItemPicture1(final Item pItem, final Document pPicture, final Document pThumbnail) {
		assertCurrentUserAuthorizedToEdit(pItem);
		final Document oldPic = pItem.getImage1();
		final Document oldThumbnail = pItem.getThumbnail1();		
		if (pPicture != null) {
			getDocumentDao().createDocument(pPicture);
		}
		pItem.setImage1(pPicture);
		if (pThumbnail != null) {
			getDocumentDao().createDocument(pThumbnail);
		}
		pItem.setThumbnail1(pThumbnail);
		
		if (oldPic != null) {
			getDocumentDao().deleteDocument(oldPic);
		}
		if (oldThumbnail != null) {
			getDocumentDao().deleteDocument(oldThumbnail);
		}
		
		itemDao.updateItem(pItem);
 	}
	
	/**
	 * Returns the URL for image1 or null if no image1.
	 * 
	 * @param pItem
	 * @param pAuthorizeDocumentAccess
	 * @return
	 */
	public String getItemPicture1Src(final Item pItem, final boolean pAuthorizeDocumentAccess) {
		final Document picture = pItem.getImage1();
		if (picture == null ) {
			return null;
		}
		else {
			if (pAuthorizeDocumentAccess) {
				getDocumentService().authorizeDownloadOneMinute(JsfUtils.getSession(), picture.getId());
			}
			return JsfUtils.getFullUrl(
					PagesURL.DOCUMENT_DOWNLOAD, 
					PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_ID, 
					picture.getId().toString());
		}			
	}
	
	/**
	 * Returns the URL for thumbnail1 or null if no thumbnail1.
	 * 
	 * @param pItem
	 * @param pAuthorizeDocumentAccess
	 * @return
	 */
	public String getItemThumbnail1Src(final Item pItem, final boolean pAuthorizeDocumentAccess) {
		final Document thumbnail = pItem.getThumbnail1();
		if (thumbnail == null ) {
			return null;
		}
		else {
			if (pAuthorizeDocumentAccess) {
				getDocumentService().authorizeDownloadOneMinute(JsfUtils.getSession(), thumbnail.getId());
			}
			return JsfUtils.getFullUrl(
					PagesURL.DOCUMENT_DOWNLOAD, 
					PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_ID, 
					thumbnail.getId().toString());
		}		
	}
	
	public void sendNotificationForNeed(final Need pNeed, final Item pItem) throws ItemException {
		CoreUtils.assertNotNull(pNeed);
		CoreUtils.assertNotNull(pItem);
		if (pNeed.getOwner() == null || !pNeed.getOwner().isEnabled()) {
			return;
		}
		try {	
			// Send email (will actually create a JMS message, i.e. it is async).
			Map<String, String> objects = new HashMap<String, String>();
			objects.put("connectionFirstName", pItem.getOwner().getFirstName());
			objects.put("connectionLastName", pItem.getOwner().getLastName());
			objects.put("connectionDisplayName", pItem.getOwner().getDisplayName());
			objects.put("requesterFirstName", pNeed.getOwner().getFirstName());
			objects.put("requesterLastName", pNeed.getOwner().getLastName());
			objects.put("requesterDisplayName", pNeed.getOwner().getDisplayName());
			objects.put("needTitle", pNeed.getTitle());
			objects.put("needUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(),
					PagesURL.NEED_OVERVIEW,
					PagesURL.NEED_OVERVIEW_PARAM_NEED_ID,
					pNeed.getId().toString()));
			objects.put("itemTitle", pItem.getTitle());
			objects.put("itemUrl",  JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(),
					PagesURL.ITEM_OVERVIEW,
					PagesURL.ITEM_OVERVIEW_PARAM_ITEM_ID,
					pItem.getId().toString()));
			objects.put("signature", Configuration.getSiteName());
			objects.put("siteName", Configuration.getSiteName());
			objects.put("siteUrl", Configuration.getRootURL());
			
			// TODO: localization
			final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/need/objectadded/fr";
			
			Map<String, String> to = new HashMap<String, String>();
			to.put(pNeed.getOwner().getEmail(), pNeed.getOwner().getEmail());
			
			Map<String, String> inlineResources = new HashMap<String, String>();
			inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.gif");
			
			getMailManager().send(Configuration.getNoReplySenderName(), 
					         Configuration.getNoReplyEmailAddress(),
					         to,
					         null, 
					         null,
					         Configuration.getSiteName() + ": object répondant à une recherche",
					         objects, 
					         velocityTemplateLocation,
					         inlineResources);		
		} 
		catch (Exception e) {
			throw new ItemException(e);
		}		
	}

	public String getItemInProgressLendTransactionUrl(final Item pItem) throws ItemException {
		try {
			final LendTransaction lt = lendTransactionService.findInProgressLendTransactionForItem(pItem);
			if (lt != null) {
				return JsfUtils.getFullUrl(
						PagesURL.LEND_TRANSACTION_OVERVIEW,
						PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID,
						lt.getId().toString());
			}
			else {
				if (log.isWarnEnabled()) {
					log.warn("No in progress lend transaction for item: " + pItem.getId());
				}
				return null;
			}			
		}
		catch (LendTransactionException e) {
			throw new ItemException(e);
		}
	}

	/////////////////////////////////////////////////////////
	// Access control
	
	public boolean isUserAuthorizedToView(final Person pPerson, final Objekt pObjekt) {
		CoreUtils.assertNotNull(pObjekt);
		if (!SecurityUtils.isLoggedIn()) {
			if (pObjekt.isPublicVisibility()) {
				return true;
			}
			return false;
		}
		else {
			Item item = (Item)pObjekt;
			if (isUserAuthorizedToEdit(pPerson, pObjekt)) {
				return true;
			}
			// Connections can view.
			else if (// Public visibility.
				pObjekt.isPublicVisibility() ||
				// Connection visibility.
			    (pObjekt.isConnectionsVisibility() && 
	    		 pObjekt.getOwner() != null &&
	    		 pObjekt.getOwner().getConnections() != null &&
	    		 pObjekt.getOwner().getConnections().contains(pPerson)) ||
				 // Person is the current borrower - then he should always see it. 
	    		 item.getBorrower() != null && item.getBorrower().equals(pPerson)) {
				return true;
			}
			return false;
		}
	}

	// Access control
	/////////////////////////////////////////////////////////
	
}

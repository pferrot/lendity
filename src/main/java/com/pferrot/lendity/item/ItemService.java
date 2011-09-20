package com.pferrot.lendity.item;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.dao.ItemDao;
import com.pferrot.lendity.dao.LendRequestDao;
import com.pferrot.lendity.dao.LendTransactionDao;
import com.pferrot.lendity.dao.bean.ItemDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.exception.ItemException;
import com.pferrot.lendity.model.Document;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.ItemCategory;
import com.pferrot.lendity.model.ItemVisibility;
import com.pferrot.lendity.model.ListValue;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.model.Objekt;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.HtmlUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.ListValueUtils;
import com.pferrot.security.SecurityUtils;

public class ItemService extends ObjektService {

	private final static Log log = LogFactory.getLog(ItemService.class);
	
	private LendRequestDao lendRequestDao;
	private LendTransactionDao lendTransactionDao;
	private ItemDao itemDao;

	public void setLendRequestDao(LendRequestDao lendRequestDao) {
		this.lendRequestDao = lendRequestDao;
	}

	public void setLendTransactionDao(LendTransactionDao lendTransactionDao) {
		this.lendTransactionDao = lendTransactionDao;
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

	public long countAllMyItems() {		
		return countItems(PersonUtils.getCurrentPersonId(), null, null, null, null, null, null, null);
	}
	
	public List<Item> findAllItemsForPerson(final Long pPersonId) {
		CoreUtils.assertNotNull(pPersonId);
		
		final ItemDaoQueryBean queryBean = new ItemDaoQueryBean();
		queryBean.setOwnerIds(new Long[]{pPersonId});
		queryBean.setFirstResult(0);
		queryBean.setMaxResults(0);
		
		return itemDao.findItemsList(queryBean);		
	}
	
	/**
	 * Returns 5 random public items in the area of pOriginLatitude/pOriginLongitude.
	 * 
	 * @return
	 */
	public List<Item> findRandomItemsHomepage() {
		final ItemDaoQueryBean itemQuery = new ItemDaoQueryBean();
		itemQuery.setOwnerEnabled(Boolean.TRUE);
		itemQuery.setOrderBy("random");
		itemQuery.setMaxResults(5);
		if (SecurityUtils.isLoggedIn()) {
			// All connections.
			final Long[] connectionsIds = getPersonService().getCurrentPersonConnectionIds(null);
			itemQuery.setOwnerIds(connectionsIds);
			// Exclude myself.
			itemQuery.setOwnerIdsToExcludeForVisibilityIdsToForce(ListValueUtils.getIdsArray(PersonUtils.getCurrentPersonId()));
			itemQuery.setVisibilityIds(getConnectionsAndPublicVisibilityIds());
			itemQuery.setVisibilityIdsToForce(ListValueUtils.getIdsArray(getPublicVisibilityId()));
			itemQuery.setGroupIds(getGroupService().getCurrentPersonGroupIds());
		}
		// When not logged in - only show public items.
		else {
			itemQuery.setVisibilityIds(new Long[]{getPublicVisibilityId()});
		}
		return itemDao.findItemsList(itemQuery);
	}

	/**
	 * Returns 5 random public items in the area of pOriginLatitude/pOriginLongitude.
	 * First we look in the a distance of 2 kilometers to search for really close items.
	 * If there is less that 5 items, then we look up to 20 km, then up to 100 km and finally distance.
	 * 
	 * @param pOriginLatitude
	 * @param pOriginLongitude
	 * @return
	 */
	public List<Item> findRandomItemsHomepage(final Double pOriginLatitude, final Double pOriginLongitude) {
		CoreUtils.assertNotNull(pOriginLatitude);
		CoreUtils.assertNotNull(pOriginLongitude);
		
		final int maxResults = 5;
		
		final ItemDaoQueryBean itemQuery = new ItemDaoQueryBean();
		itemQuery.setOwnerEnabled(Boolean.TRUE);
		itemQuery.setOrderBy("random");
		itemQuery.setMaxResults(maxResults);
		if (SecurityUtils.isLoggedIn()) {
			// All connections.
			final Long[] connectionsIds = getPersonService().getCurrentPersonConnectionIds(null);
			itemQuery.setOwnerIds(connectionsIds);
			// Exclude myself.
			itemQuery.setOwnerIdsToExcludeForVisibilityIdsToForce(ListValueUtils.getIdsArray(PersonUtils.getCurrentPersonId()));
			itemQuery.setVisibilityIds(getConnectionsAndPublicVisibilityIds());
			itemQuery.setVisibilityIdsToForce(ListValueUtils.getIdsArray(getPublicVisibilityId()));
			itemQuery.setGroupIds(getGroupService().getCurrentPersonGroupIds());
		}
		// When not logged in - only show public items.
		else {
			itemQuery.setVisibilityIds(new Long[]{getPublicVisibilityId()});
		}
		
		// Try 2km, then 20, then 100, then unlimited.
		itemQuery.setMaxDistanceKm(new Double(2));
		itemQuery.setOriginLatitude(pOriginLatitude);
		itemQuery.setOriginLongitude(pOriginLongitude);
		ListWithRowCount lwrc = itemDao.findItems(itemQuery);
		
		// Try 20km.
		if (lwrc.getRowCount() < maxResults) {
			itemQuery.setMaxDistanceKm(new Double(20));
			lwrc = itemDao.findItems(itemQuery);
		}
		else {
			return lwrc.getList();
		}
		
		// Try 100km.
		if (lwrc.getRowCount() < maxResults) {
			itemQuery.setMaxDistanceKm(new Double(100));
			lwrc = itemDao.findItems(itemQuery);
		}
		else {
			return lwrc.getList();
		}
		
		// Unlimited distance.
		if (lwrc.getRowCount() < maxResults) {
			itemQuery.setMaxDistanceKm(null);
			lwrc = itemDao.findItems(itemQuery);
		}
		else {
			return lwrc.getList();
		}
		
		return lwrc.getList();
	}

	protected ItemDaoQueryBean getItemsQueryBean(final Long pPersonId, final String pTitle, final Long pCategoryId, final Long[] pVisibilityIds,
			final Boolean pBorrowed, final Long pLendType, final String pOrderBy, final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
		final Long[] personIds = new Long[]{pPersonId};
		
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
		final Double latitude = PersonUtils.getCurrentPersonAddressHomeLatitude();
		final Double longitude = PersonUtils.getCurrentPersonAddressHomeLongitude();	
		itemQuery.setOriginLatitude(latitude);
		itemQuery.setOriginLongitude(longitude);
		return itemQuery;
	}
	
	public ListWithRowCount findItems(final Long pPersonId, final String pTitle, final Long pCategoryId, final Long[] pVisibilityIds,
			final Boolean pBorrowed, final Long pLendType, final String pOrderBy, final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
		
		final ItemDaoQueryBean itemQuery = getItemsQueryBean(pPersonId, pTitle, pCategoryId, pVisibilityIds, pBorrowed, pLendType, pOrderBy, pOrderByAscending, pFirstResult, pMaxResults);
		
		return itemDao.findItems(itemQuery);
	}

	public long countItems(final Long pPersonId, final String pTitle, final Long pCategoryId, final Long[] pVisibilityIds,
			final Boolean pBorrowed, final Long pLendType, final String pOrderBy, final Boolean pOrderByAscending) {
		final ItemDaoQueryBean itemQuery = getItemsQueryBean(pPersonId, pTitle, pCategoryId, pVisibilityIds, pBorrowed, pLendType, null, null, 0, 0);
		
		return itemDao.countItems(itemQuery);
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
			// Exclude myself.
			itemQuery.setOwnerIdsToExcludeForVisibilityIdsToForce(ListValueUtils.getIdsArray(PersonUtils.getCurrentPersonId()));
			itemQuery.setVisibilityIds(getConnectionsAndPublicVisibilityIds());
			if (!ItemConsts.OWNER_TYPE_CONNECTIONS.equals(pOwnerType)) {
				itemQuery.setVisibilityIdsToForce(ListValueUtils.getIdsArray(getPublicVisibilityId()));
				itemQuery.setGroupIds(getGroupService().getCurrentPersonGroupIds());
			}
		}
		// When not logged in - only show public items.
		else {
			itemQuery.setVisibilityIds(new Long[]{getPublicVisibilityId()});
		}
		itemQuery.setBorrowed(pBorrowed);
		final Double latitude = PersonUtils.getCurrentPersonAddressHomeLatitude();
		final Double longitude = PersonUtils.getCurrentPersonAddressHomeLongitude();	
		itemQuery.setOriginLatitude(latitude);
		itemQuery.setOriginLongitude(longitude);
		itemQuery.setMaxDistanceKm(pMaxDistanceInKm);
		if (pMaxDistanceInKm != null &&
				(latitude == null || longitude == null)) {
			throw new RuntimeException("Can only search by distance if geolocation is available.");			
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
	
	public ListWithRowCount findGroupItems(final Long pGroupId, final String pTitle, final Long pCategoryId, final Boolean pBorrowed, final String pOrderBy, final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
		
		CoreUtils.assertNotNull(pGroupId);
		
		final ItemDaoQueryBean itemQuery = new ItemDaoQueryBean();
		
		itemQuery.setGroupIds(getIds(pGroupId));
		itemQuery.setOwnerEnabled(Boolean.TRUE);
		itemQuery.setTitle(pTitle);
		itemQuery.setCategoryIds(getCategoryIds(pCategoryId));
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
	
	/**
	 * Returns new items in the area (20km) of the user.
	 * 
	 * @param pPerson
	 * @param pDate
	 * @return
	 */
	public ListWithRowCount findPersonItemsUpdateSince(final Person pPerson, final Date pDate) {
		Long[] connectionsIds = getPersonService().getPersonConnectionIds(pPerson, null);
		Long[] groupIds = getGroupService().getPersonGroupsIds(pPerson, null);
		
		final ItemDaoQueryBean itemQuery = new ItemDaoQueryBean();
		itemQuery.setOwnerIds(connectionsIds);
		itemQuery.setOwnerEnabled(Boolean.TRUE);

		// Exclude myself.
		itemQuery.setOwnerIdsToExcludeForVisibilityIdsToForce(ListValueUtils.getIdsArray(pPerson.getId()));
		itemQuery.setVisibilityIds(getConnectionsAndPublicVisibilityIds());
		itemQuery.setVisibilityIdsToForce(ListValueUtils.getIdsArray(getPublicVisibilityId()));
		itemQuery.setGroupIds(groupIds);
		
		if (pPerson.isAddressHomeDefined()) {
			itemQuery.setMaxDistanceKm(Double.valueOf(20.0));
			itemQuery.setOriginLatitude(pPerson.getAddressHomeLatitude());
			itemQuery.setOriginLongitude(pPerson.getAddressHomeLongitude());
		}
		
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
//	public void updateLendBackItem(final Long pItemId) {
//		final Item item = findItem(pItemId);
//		assertCurrentUserAuthorizedToEdit(item);
//		final Person borrower = item.getBorrower();
//		item.setLendBack();
//		updateItem(item);
//		// Send email only if was lent to a user of the system.
//		if (borrower != null) {
//			// Send email (will actually create a JMS message, i.e. it is async).
//			Map<String, String> objects = new HashMap<String, String>();
//			objects.put("borrowerFirstName", borrower.getFirstName());
//			objects.put("lenderFirstName", PersonUtils.getCurrentPersonFirstName());
//			objects.put("lenderLastName", PersonUtils.getCurrentPersonLastName());
//			objects.put("lenderDisplayName", PersonUtils.getCurrentPersonDisplayName());
//			objects.put("itemTitle", item.getTitle());
//			objects.put("signature", Configuration.getSiteName());
//			objects.put("siteName", Configuration.getSiteName());
//			objects.put("siteUrl", Configuration.getRootURL());
//			
//			// TODO: localization
//			final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/lend/lendback/fr";
//			
//			Map<String, String> to = new HashMap<String, String>();
//			to.put(borrower.getEmail(), borrower.getEmail());
//			
//			Map<String, String> inlineResources = new HashMap<String, String>();
//			inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.png");
//			
//			getMailManager().send(Configuration.getNoReplySenderName(), 
//					         Configuration.getNoReplyEmailAddress(),
//					         to,
//					         null, 
//					         null,
//					         Configuration.getSiteName() + ": objet rendu",
//					         objects, 
//					         velocityTemplateLocation,
//					         inlineResources);
//			
//		}
//	}

//	public void updateSendReminderItem(final Long pItemId) {
//		if (log.isInfoEnabled()) {
//			log.info("Sending reminder for item: " + pItemId);
//		}
//		final Item item = findItem(pItemId);
//		assertCurrentUserAuthorizedToEdit(item);
//		
//		if (!item.isBorrowed()) {
//			return;			
//		}
//		
//		final Person borrower = item.getBorrower();
//		if (borrower == null) {
//			return;
//		}
//		
//		item.reminderSentNow();		
//		updateItem(item);
//		
//		// Send email (will actually create a JMS message, i.e. it is async).
//		Map<String, String> objects = new HashMap<String, String>();
//		objects.put("borrowerFirstName", borrower.getFirstName());
//		objects.put("lenderFirstName", PersonUtils.getCurrentPersonFirstName());
//		objects.put("lenderLastName", PersonUtils.getCurrentPersonLastName());
//		objects.put("lenderDisplayName", PersonUtils.getCurrentPersonDisplayName());
//		objects.put("itemTitle", item.getTitle());
//		objects.put("signature", Configuration.getSiteName());
//		objects.put("siteName", Configuration.getSiteName());
//		objects.put("siteUrl", Configuration.getRootURL());
//		
//		// TODO: localization
//		final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/lend/reminder/fr";
//		
//		Map<String, String> to = new HashMap<String, String>();
//		to.put(borrower.getEmail(), borrower.getEmail());
//		
//		Map<String, String> inlineResources = new HashMap<String, String>();
//		inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.png");
//		
//		getMailManager().send(Configuration.getNoReplySenderName(), 
//				         Configuration.getNoReplyEmailAddress(),
//				         to,
//				         null, 
//				         null,
//				         Configuration.getSiteName() + ": rappel pour objet emprunté",
//				         objects, 
//				         velocityTemplateLocation,
//				         inlineResources);
//	}
	
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
		// TODO: update and set item to null?
		lendRequestDao.updateLendRequestsSetNullItem(pItem.getId());
		lendTransactionDao.updateLendTransactionsSetNullItem(pItem.getId());
		itemDao.deleteItem(pItem);
	}
	
	public void deleteItem(final Long pItemId) {
		deleteItem(itemDao.findItem(pItemId));
	}
	
	public Long createItem(final Item pItem, final List<Long> pCategoriesIds, final Long pVisibilityId) {
		return createItem(pItem, pCategoriesIds, pVisibilityId, null);
	}

	public Long createItem(final Item pItem, final List<Long> pCategoriesIds, final Long pVisibilityId, final Need pNeed) {
		Set<ItemCategory> categories = new HashSet<ItemCategory>();
		if (pCategoriesIds != null) {			
			for (Long categoryId: pCategoriesIds) {
				final ItemCategory catgory = (ItemCategory) ListValueUtils.getListValueFromId(categoryId, getListValueDao());
				categories.add(catgory);
			}					
		}
		pItem.setCategories(categories);	
		pItem.setVisibility((ItemVisibility) ListValueUtils.getListValueFromId(pVisibilityId, getListValueDao()));
		return createItem(pItem, pNeed);
	}
	
	public Long createItem(final Item pItem, final List<Long> pCategorieIds, final Long pVisibilityId, final Need pNeed, final List<Long> pAuthorizedGroupsIds) {
		if (pAuthorizedGroupsIds != null) {
			Set<Group> groups = new HashSet<Group>();
			for (Long groupId: pAuthorizedGroupsIds) {
				final Group group = getGroupService().findGroup(groupId);
				// AC check.
				getGroupService().assertCurrentUserOwnerOrAdministratorOrMemberOfGroup(group);
				groups.add(group);
			}
			pItem.setGroupsAuthorized(groups);
		}
		return createItem(pItem, pCategorieIds, pVisibilityId, pNeed);
	}

	public void updateItem(final Item pItem) {
		updateItemPicture1Visibility(pItem);
		itemDao.updateItem(pItem);
	}

	public void updateItem(final Item pItem, final List<Long> pCategoriesIds, final Long pVisibilityId) {
		assertCurrentUserAuthorizedToEdit(pItem);
		pItem.setVisibility((ItemVisibility) ListValueUtils.getListValueFromId(pVisibilityId, getListValueDao()));
		updateItem(pItem, pCategoriesIds);
	}
	
	public void updateItem(final Item pItem, final List<Long> pCategoriesIds, final Long pVisibilityId, final List<Long> pAuthorizedGroupsIds) {
		assertCurrentUserAuthorizedToEdit(pItem);
		if (pAuthorizedGroupsIds != null) {
			Set<Group> groups = new HashSet<Group>();
			for (Long groupId: pAuthorizedGroupsIds) {
				final Group group = getGroupService().findGroup(groupId);
				// AC check.
				getGroupService().assertCurrentUserOwnerOrAdministratorOrMemberOfGroup(group);
				groups.add(group);
			}
			pItem.setGroupsAuthorized(groups);
		}
		updateItem(pItem, pCategoriesIds, pVisibilityId);
	}

	public void updateItem(final Item pItem, final List<Long> pCategoriesIds) {
		assertCurrentUserAuthorizedToEdit(pItem);
		Set<ItemCategory> categories = new HashSet<ItemCategory>();
		if (pCategoriesIds != null) {			
			for (Long categoryId: pCategoriesIds) {
				final ItemCategory catgory = (ItemCategory) ListValueUtils.getListValueFromId(categoryId, getListValueDao());
				categories.add(catgory);
			}					
		}
		pItem.setCategories(categories);	
		updateItem(pItem);
	}

	public void updateItemPicture1(final Item pItem, final Document pPicture, final Document pThumbnail) {
		assertCurrentUserAuthorizedToEdit(pItem);
		if (pItem.isPublicVisibility()) {
			if (pPicture != null) {
				pPicture.setPublik(Boolean.TRUE);
			}
			if (pThumbnail != null) {
				pThumbnail.setPublik(Boolean.TRUE);
			}
		}
		else {
			if (pPicture != null) {
				pPicture.setPublik(Boolean.FALSE);
			}
			if (pThumbnail != null) {
				pThumbnail.setPublik(Boolean.FALSE);
			}
		}
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
	
	private void updateItemPicture1Visibility(final Item pItem) {		
		if (pItem.getImage1() != null) {
			pItem.getImage1().setPublik(pItem.isPublicVisibility());
		}
		if (pItem.getThumbnail1() != null) {
			pItem.getThumbnail1().setPublik(pItem.isPublicVisibility());
		}
 	}
	
	public void updateItemsRemoveGroupAuthorized(final Long pItemOwnerId, final Long pGroupId) {
		CoreUtils.assertNotNull(pItemOwnerId);
		CoreUtils.assertNotNull(pGroupId);
		
		final Group group = getGroupService().findGroup(pGroupId);
		final List<Item> items = findAllItemsForPerson(pItemOwnerId);
		
		for (Item item: items) {
			if (item.getGroupsAuthorized().contains(group)) {
				assertCurrentUserAuthorizedToEdit(item);
				item.removeGroupAuthorized(group);
				updateItem(item);
			}
		}		
	}
	
	public void updateItemsRemoveGroupAuthorized(final Long pGroupId) {
		CoreUtils.assertNotNull(pGroupId);
		
		final Group group = getGroupService().findGroup(pGroupId);
		
		getGroupService().assertCurrentUserAuthorizedToDelete(group);
		
		final Set<Item> items = group.getItems();
		
		if (items != null) {
			for (Item item: items) {
				item.removeGroupAuthorized(group);
				updateItem(item);
			}
		}
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
	
	@Override
	public String getThumbnail1Src(final Objekt pObjekt,
			 final boolean pAuthorizeDocumentAccess, final HttpSession pSession, final String pUrlPrefix, final Long pPreferredCategoryId) {
		final Item item = (Item)pObjekt;
		final Document thumbnail1 = item.getThumbnail1();
		if (thumbnail1 == null ) {
			return super.getThumbnail1Src(pObjekt, pAuthorizeDocumentAccess, pSession, pUrlPrefix, pPreferredCategoryId);
		}
		else {
			if (pAuthorizeDocumentAccess) {
				getDocumentService().authorizeDownloadOneMinute(JsfUtils.getSession(), thumbnail1.getId());
			}
			return JsfUtils.getFullUrl(
					PagesURL.DOCUMENT_DOWNLOAD, 
					PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_ID, 
					thumbnail1.getId().toString());
		}
	}	
	

	@Override
	public String getFacebookLikeImageSrc(final Objekt pObjekt,
			 final boolean pAuthorizeDocumentAccess, final HttpSession pSession, final String pUrlPrefix) {
		final Item item = (Item)pObjekt;
		final Document image1 = item.getImage1();
		if (image1 == null ) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, ItemConsts.FACEBOOK_LIKE_DEFAULT_IMAGE_URL);
		}
		else {
			if (pAuthorizeDocumentAccess) {
				getDocumentService().authorizeDownloadOneMinute(JsfUtils.getSession(), image1.getId());
			}
			return JsfUtils.getFullUrlWithPrefix(
					Configuration.getRootURL(),
					PagesURL.DOCUMENT_DOWNLOAD, 
					PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_ID, 
					image1.getId().toString());
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
			inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.png");
			
			getMailManager().send(Configuration.getNoReplySenderName(), 
					         Configuration.getNoReplyEmailAddress(),
					         to,
					         null, 
					         null,
					         Configuration.getSiteName() + ": object répondant à un souhait",
					         objects, 
					         velocityTemplateLocation,
					         inlineResources);		
		} 
		catch (Exception e) {
			throw new ItemException(e);
		}		
	}

	public boolean isRentalAllowed(final Long pCategoryId) {
		CoreUtils.assertNotNull(pCategoryId);
		final ListValue category = getListValueDao().findListValue(pCategoryId);
		
		if (Configuration.getCategoriesNotAllowedToRent().contains(category.getLabelCode())) {
			return false;
		}		
		return true;
	}
	
	/**
	 * Replaces all occurrences of strings like {i123} with an href link to the
	 * corresponding object, e.g.:
	 * 
	 * <a href="http://www.lendity.ch/item/itemOverview.faces?itemID=123" target="_blank">The item title</a>
	 * 
	 * If pPerson is not authorized to view the item, a standard error text is used instead.
	 * 
	 * @param pText
	 * @param pPerson
	 * @return
	 */
	public String processItemHref(final String pText, final Person pPerson) {
		return processItemHrefInternal(pText, pPerson, true);
	}
	
	public String processItemNoHref(final String pText, final Person pPerson) {
		return processItemHrefInternal(pText, pPerson, false);
	}
	
	private String processItemHrefInternal(final String pText, final Person pPerson, final boolean pWithHref) {
		if (StringUtils.isNullOrEmpty(pText)) {
			return pText;
		}
		
		final String regex = "\\{i[0-9]+\\}";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(pText);

		final StringBuffer result = new StringBuffer();
		while (m.find()) {
			try {
				final String text = m.group();
				final Long itemId = Long.parseLong(text.substring(2, text.length() - 1));
				final Item item = findItem(itemId);
				assertUserAuthorizedToView(pPerson, item);
				if (pWithHref) {
					m.appendReplacement(result, getHrefLinkToItem(item, true));
				}
				else {
					m.appendReplacement(result, item.getTitle());
				}
			}
			catch (Exception e) {
				final Locale locale = I18nUtils.getDefaultLocale();
				final String s = I18nUtils.getMessageResourceString("comment_replacementError", locale);
				m.appendReplacement(result, s);	
			}
			
			
		}
		m.appendTail(result);	
		return result.toString();
	}
	
	private String getHrefLinkToItem(final Item pItem, final boolean pOpenInNewWindow) {
		return "<a href=\"" + 
			JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.ITEM_OVERVIEW, PagesURL.ITEM_OVERVIEW_PARAM_ITEM_ID, pItem.getId().toString()) +
			"\"" +
			(pOpenInNewWindow?" target=\"_blank\"":"") +
			">" + 
			HtmlUtils.escapeHtmlAndReplaceCr(pItem.getTitle()) + 
			"</a>";
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
	    		(item.getBorrower() != null && item.getBorrower().equals(pPerson))) {
				return true;
			}
			final Set<Group> groupsAuthorized = item.getGroupsAuthorized();
			if (groupsAuthorized.isEmpty()) {
				return false;
			}
			final List<Group> groupsPerson = getGroupService().findPersonGroupsWhereOwnerOrAdministratorOrMemberList(pPerson.getId(), null, 0, 0);
			if (groupsPerson.isEmpty()) {
				return false;
			}
			// We should certainly improve this when we have time...
			for (Group groupAuthorized: groupsAuthorized) {
				for(Group groupPerson: groupsPerson) {
					if (groupAuthorized.equals(groupPerson)) {
						return true;
					}
				}
			}
			return false;
		}
	}

	// Access control
	/////////////////////////////////////////////////////////
	
}

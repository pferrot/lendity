package com.pferrot.lendity.item;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ObjectNotFoundException;

import com.pferrot.core.CoreUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.dao.ItemDao;
import com.pferrot.lendity.dao.LendRequestDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.ExternalItem;
import com.pferrot.lendity.model.InternalItem;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.ItemCategory;
import com.pferrot.lendity.model.Language;
import com.pferrot.lendity.model.ListValue;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.ListValueUtils;

public class ItemService {

	private final static Log log = LogFactory.getLog(ItemService.class);
	
	private ItemDao itemDao;
	private ListValueDao listValueDao;
	private LendRequestDao lendRequestDao;
	private PersonDao personDao;
	private MailManager mailManager;
	
	public void setMailManager(final MailManager pMailManager) {
		this.mailManager = pMailManager;
	}

	public void setItemDao(ItemDao itemDao) {
		this.itemDao = itemDao;
	}

	public void setListValueDao(ListValueDao listValueDao) {
		this.listValueDao = listValueDao;
	}

	public void setLendRequestDao(LendRequestDao lendRequestDao) {
		this.lendRequestDao = lendRequestDao;
	}

	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}

	public List<ListValue> getCategories() {
		return listValueDao.findListValue(ItemCategory.class);
	}
	
	public List<Person> getCurrentPersonEnabledConnections() {
		final ListWithRowCount listWithRowCount = personDao.findPersons(PersonUtils.getCurrentPersonId(), PersonDao.CONNECTIONS_LINK, null, Boolean.TRUE, true, 0, 0);
		return listWithRowCount.getList();
	}
	
	public Language findLanguage(final String languageLabelCode) {
		return (Language)listValueDao.findListValue(languageLabelCode);
	}

	public InternalItem findInternalItem(final Long itemId) {
		return itemDao.findInternalItem(itemId);
	}

	public ExternalItem findExternalItem(final Long itemId) {
		return itemDao.findExternalItem(itemId);
	}
	
//	public List<InternalItem> findAllInternalItems() {
//		return itemDao.findAllInternalItems();
//	}
//	
//	public List<InternalItem> findItems(final int pFirstResult, final int pMaxResults) {
//		return itemDao.findInternalItems(pFirstResult, pMaxResults);
//	}
//	
//	public List<InternalItem> findItemsByTitle(final String pTitle, final int pFirstResult, final int pMaxResults) {
//		return itemDao.findInternalItemsByTitle(pTitle, pFirstResult, pMaxResults);
//	}
	
//	public ListWithRowCount findItemsOwnedByPersonId(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
//		return itemDao.findItemsOwnedByPerson(pPersonId, pFirstResult, pMaxResults);
//	}
//
//	public ListWithRowCount findItemsByTitleOwnedByPersonId(final String pTitle, final Long pPersonId, final int pFirstResult, final int pMaxResults) {
//		return itemDao.findItemsByTitleOwnedByPerson(pTitle, pPersonId, pFirstResult, pMaxResults);
//	}

	public ListWithRowCount findMyBorrowedItems(final Long pOwnerId, final String pTitle, final Long pCategoryId, final int pFirstResult, final int pMaxResults) {
		final Long currentPersonId = PersonUtils.getCurrentPersonId();
		CoreUtils.assertNotNull(currentPersonId);
		Long[] ownerIds = null;
		if (pOwnerId != null) {
			ownerIds = new Long[]{pOwnerId};
		}
		Long[] borrowersIds = new Long[]{PersonUtils.getCurrentPersonId()};
		
		return itemDao.findInternalAndExternalItems(ownerIds, Boolean.TRUE, borrowersIds, null, pTitle, getCategoryIds(pCategoryId), null, "title", Boolean.TRUE, pFirstResult, pMaxResults);
	}
	
	public ListWithRowCount findMyItems(final String pTitle, final Long pCategoryId, final Boolean pVisible,
			final Boolean pBorrowed, final int pFirstResult, final int pMaxResults) {
		final Long currentPersonId = PersonUtils.getCurrentPersonId();
		CoreUtils.assertNotNull(currentPersonId);
		final Long[] personIds = new Long[]{currentPersonId};
		
		return itemDao.findInternalItems(personIds, Boolean.TRUE, null, null, pTitle, getCategoryIds(pCategoryId), pVisible, pBorrowed, "title", Boolean.TRUE, pFirstResult, pMaxResults);
	}

	public ListWithRowCount findMyConnectionsItems(final Long pConnectionId, final String pTitle, final Long pCategoryId,
			final Boolean pBorrowed, final int pFirstResult, final int pMaxResults) {
		Long[] connectionsIds = getConnectionIds(pConnectionId);
		if (connectionsIds == null || connectionsIds.length == 0) {
			return ListWithRowCount.emptyListWithRowCount();
		}
		return itemDao.findInternalItems(connectionsIds, Boolean.TRUE, null, null, pTitle, getCategoryIds(pCategoryId), Boolean.TRUE, pBorrowed, "title", Boolean.TRUE, pFirstResult, pMaxResults);
	}
	
	public ListWithRowCount findMyLatestAvailableConnectionsItems() {
		Long[] connectionsIds = getConnectionIds(null);
		if (connectionsIds == null || connectionsIds.length == 0) {
			return ListWithRowCount.emptyListWithRowCount();
		}
		return itemDao.findInternalItems(connectionsIds, Boolean.TRUE, null, null, null, null, Boolean.TRUE, Boolean.FALSE, "creationDate", Boolean.FALSE, 0, 5);
	}
	
	private Long[] getConnectionIds(final Long pConnectionId) {
		Long[] connectionsIds = null;
		// All connections
		if (pConnectionId == null) {
			final Person person = getCurrentPerson();
			final Set<Person> connections = person.getConnections();
			if (connections == null || connections.isEmpty()) {
				return null;
			}
			connectionsIds = new Long[connections.size()];
			int counter = 0;
			for(Person connection: connections) {			
				connectionsIds[counter] = connection.getId();
				counter++;
			}
		}
		// Only one connection - make sure that it is a connection of the user. If not, it is someone trying to hack...
		else {
			final Person person = getCurrentPerson();
			final Set<Person> connections = person.getConnections();
			boolean connectionFound = false;
			if (connections != null) {
				for(Person connection: connections) {			
					if (pConnectionId.equals(connection.getId())) {
						connectionFound = true;
						break;
					}
				}
			}
			if (!connectionFound) {
				throw new SecurityException("Person with ID '" + PersonUtils.getCurrentPersonId() + "' tried to display details about person with " +
						"ID '" + pConnectionId.toString() + "' but is not a connection.");
			}
			connectionsIds = new Long[]{pConnectionId};
		}
		return connectionsIds;
	}
	
	private Long[] getCategoryIds(final Long pCategoryId) {
		Long[] categoryIds = null;
		if (pCategoryId != null) {
			categoryIds = new Long[1];
			categoryIds[0] = pCategoryId;
		}
		return categoryIds;		
	}

//	public List<Item> findItemsBorrowedByPersonId(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
//		return itemDao.findItemsBorrowedByPerson(pPersonId, pFirstResult, pMaxResults);
//	}
//
//	public List<InternalItem> findItemsLentByPersonId(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
//		return itemDao.findItemsLentByPerson(pPersonId, pFirstResult, pMaxResults);
//	}
//	
//	public List<InternalItem> findVisibleItemsOwnedByCurrentPersonConnections(final int pFirstResult, final int pMaxResults) {
//		return itemDao.findVisibleItemsOwnedByConnections(getCurrentPerson(), pFirstResult, pMaxResults);
//	}

	
	/**
	 * When an internal item is no more lent.
	 *
	 * @param pItemId
	 */
	public void updateLendBackInternalItem(final Long pItemId) {
		final InternalItem internalItem = findInternalItem(pItemId);
		assertCurrentUserAuthorizedToEdit(internalItem);
		final Person borrower = internalItem.getBorrower();
		internalItem.setLendBack();
		updateItem(internalItem);
		// Send email only if was lent to a user of the system.
		if (borrower != null) {
			// Send email (will actually create a JMS message, i.e. it is async).
			Map<String, String> objects = new HashMap<String, String>();
			objects.put("borrowerFirstName", borrower.getFirstName());
			objects.put("lenderFirstName", PersonUtils.getCurrentPersonFirstName());
			objects.put("lenderLastName", PersonUtils.getCurrentPersonLastName());
			objects.put("itemTitle", internalItem.getTitle());
			objects.put("signature", Configuration.getSiteName());
			objects.put("siteName", Configuration.getSiteName());
			objects.put("siteUrl", Configuration.getRootURL());
			
			// TODO: localization
			final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/lend/lendback/fr";
			
			Map<String, String> to = new HashMap<String, String>();
			to.put(borrower.getEmail(), borrower.getEmail());
			
			mailManager.send(Configuration.getNoReplySenderName(), 
					         Configuration.getNoReplyEmailAddress(),
					         to,
					         null, 
					         null,
					         Configuration.getSiteName() + ": objet rendu",
					         objects, 
					         velocityTemplateLocation);
			
		}
	}

	/**
	 * Lend to a user of the system.
	 * 
	 * @param pItemId
	 * @param pBorrowerId
	 * @param pBorrowDate
	 */
	public void updateLendInternalItem(final Long pItemId, final Long pBorrowerId, final Date pBorrowDate) {
		if (log.isInfoEnabled()) {
			log.info("Lending item to borrower ID: " + pBorrowerId);
		}
		final InternalItem internalItem = findInternalItem(pItemId);
		assertCurrentUserAuthorizedToEdit(internalItem);
		// TODO: check if enabled and allowed to borrow !?
		final Person borrower = personDao.findPerson(pBorrowerId);
		internalItem.setBorrowed(borrower, pBorrowDate);
		
		updateItem(internalItem);
		
		// Send email (will actually create a JMS message, i.e. it is async).
		Map<String, String> objects = new HashMap<String, String>();
		objects.put("borrowerFirstName", borrower.getFirstName());
		objects.put("lenderFirstName", PersonUtils.getCurrentPersonFirstName());
		objects.put("lenderLastName", PersonUtils.getCurrentPersonLastName());
		objects.put("itemTitle", internalItem.getTitle());
		objects.put("signature", Configuration.getSiteName());
		objects.put("siteName", Configuration.getSiteName());
		objects.put("siteUrl", Configuration.getRootURL());
		
		// TODO: localization
		final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/lend/lend/fr";
		
		Map<String, String> to = new HashMap<String, String>();
		to.put(borrower.getEmail(), borrower.getEmail());
		
		mailManager.send(Configuration.getNoReplySenderName(), 
				         Configuration.getNoReplyEmailAddress(),
				         to,
				         null, 
				         null,
				         Configuration.getSiteName() + ": objet emprunté",
				         objects, 
				         velocityTemplateLocation);
	}

	/**
	 * Lend to someone not using the system.
	 * 
	 * @param pItemId
	 * @param pBorrowerId
	 * @param pBorrowDate
	 */
	public void updateLendInternalItem(final Long pItemId, final String pBorrowerName, final Date pBorrowDate) {
		if (log.isInfoEnabled()) {
			log.info("Lending item to borrower name: " + pBorrowerName);
		}
		final InternalItem internalItem = findInternalItem(pItemId);
		assertCurrentUserAuthorizedToEdit(internalItem);
		internalItem.setBorrowerName(pBorrowerName);
		internalItem.setBorrower(null);
		internalItem.setBorrowDate(pBorrowDate);
		updateItem(internalItem);
		// No email to send out since the borrower is not a user of the system.
	}

	public void updateSendReminderInternalItem(final Long pItemId) {
		if (log.isInfoEnabled()) {
			log.info("Sending reminder for item: " + pItemId);
		}
		final InternalItem internalItem = findInternalItem(pItemId);
		assertCurrentUserAuthorizedToEdit(internalItem);
		
		if (!internalItem.isBorrowed()) {
			return;			
		}
		
		final Person borrower = internalItem.getBorrower();
		if (borrower == null) {
			return;
		}
		
		internalItem.reminderSentNow();		
		updateItem(internalItem);
		
		// Send email (will actually create a JMS message, i.e. it is async).
		Map<String, String> objects = new HashMap<String, String>();
		objects.put("borrowerFirstName", borrower.getFirstName());
		objects.put("lenderFirstName", PersonUtils.getCurrentPersonFirstName());
		objects.put("lenderLastName", PersonUtils.getCurrentPersonLastName());
		objects.put("itemTitle", internalItem.getTitle());
		objects.put("signature", Configuration.getSiteName());
		objects.put("siteName", Configuration.getSiteName());
		objects.put("siteUrl", Configuration.getRootURL());
		
		// TODO: localization
		final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/lend/reminder/fr";
		
		Map<String, String> to = new HashMap<String, String>();
		to.put(borrower.getEmail(), borrower.getEmail());
		
		mailManager.send(Configuration.getNoReplySenderName(), 
				         Configuration.getNoReplyEmailAddress(),
				         to,
				         null, 
				         null,
				         Configuration.getSiteName() + ": rappel pour objet emprunté",
				         objects, 
				         velocityTemplateLocation);		
		
	}
	
	public Long createItem(final Item pItem) {
		pItem.setCreationDate(new Date());
		return itemDao.createItem(pItem);
	}
	
	public void deleteInternalItem(final InternalItem pInternalItem)  {
		assertCurrentUserAuthorizedToDelete(pInternalItem);
		// Delete lend requests.
		lendRequestDao.deleteLendRequestsForItem(pInternalItem.getId());
		itemDao.deleteItem(pInternalItem);
	}
	
	public void deleteExternalItem(final Long pExternalItemId) {
		deleteExternalItem(itemDao.findExternalItem(pExternalItemId));
	}

	public void deleteExternalItem(final ExternalItem pExternalItem)  {
		assertCurrentUserAuthorizedToDelete(pExternalItem);
		itemDao.deleteItem(pExternalItem);
	}
	
	public void deleteInternalItem(final Long pInternalItemId) {
		deleteInternalItem(itemDao.findInternalItem(pInternalItemId));
	}

	public Long createItemWithCategory(final Item pItem, final Long pCategoryId) {
		pItem.setCategory((ItemCategory) ListValueUtils.getListValueFromId(pCategoryId, listValueDao));
		return createItem(pItem);
	}
	
	public Long createExternalItemWithCategory(final ExternalItem pItem, final Long pCategoryId) {
		pItem.setBorrower(getCurrentPerson());
		pItem.setCategory((ItemCategory) ListValueUtils.getListValueFromId(pCategoryId, listValueDao));
		return createItem(pItem);
	}

	public void updateItem(final Item item) {
		itemDao.updateItem(item);
	}

	public void updateItemWithCategory(final Item pItem, final Long pCategoryId) {
		assertCurrentUserAuthorizedToEdit(pItem);
		pItem.setCategory((ItemCategory) ListValueUtils.getListValueFromId(pCategoryId, listValueDao));
		updateItem(pItem);
	}

	public boolean isCurrentUserAuthorizedToView(final Item pItem) {
		CoreUtils.assertNotNull(pItem);
		if (isCurrentUserAuthorizedToEdit(pItem)) {
			return true;
		}
		// Connections can view.
		if (pItem instanceof InternalItem) {
			final InternalItem internalItem = (InternalItem) pItem;
			if (internalItem.isVisible() && 
				internalItem.getOwner() != null &&
				internalItem.getOwner().getConnections() != null &&
				internalItem.getOwner().getConnections().contains(getCurrentPerson())) {
				return true;
			}
		}
		return false;
	}
	
	public void assertCurrentUserAuthorizedToView(final Item pItem) {
		if (!isCurrentUserAuthorizedToView(pItem)) {
			throw new SecurityException("Current user is not authorized to view item");
		}
	}

	public boolean isCurrentUserAuthorizedToEdit(final Item pItem) {
		CoreUtils.assertNotNull(pItem);
		final Person currentPerson = getCurrentPerson();
		if (currentPerson == null) {
			return false;
		}
		if (currentPerson.getUser() != null &&
		    currentPerson.getUser().isAdmin()) {
			return true;
		}
		if (pItem instanceof InternalItem) {
			final InternalItem internalItem = (InternalItem) pItem;
			if (currentPerson.equals(internalItem.getOwner())) {
				return true;
			}			
		}
		else if (pItem instanceof ExternalItem) {
			final ExternalItem externalItem = (ExternalItem) pItem;
			if (currentPerson.equals(externalItem.getBorrower())) {
				return true;
			}
		}
		return false;
	}

	public void assertCurrentUserAuthorizedToEdit(final Item pItem) {
		if (!isCurrentUserAuthorizedToEdit(pItem)) {
			throw new SecurityException("Current user is not authorized to edit item");
		}
	}

	public boolean isCurrentUserAuthorizedToAdd() {
		final Person currentPerson = getCurrentPerson();
		if (currentPerson != null && currentPerson.getUser() != null) {
			return true;
		}
		return false;
	}

	public void assertCurrentUserAuthorizedToAdd(final Item pItem) {
		if (!isCurrentUserAuthorizedToAdd()) {
			throw new SecurityException("Current user is not authorized to add item");
		}
	}

	public boolean isCurrentUserAuthorizedToDelete(final Item pItem) {
		return isCurrentUserAuthorizedToEdit(pItem);
	}

	public void assertCurrentUserAuthorizedToDelete(final Item pItem) {
		if (!isCurrentUserAuthorizedToDelete(pItem)) {
			throw new SecurityException("Current user is not authorized to delete item");
		}
	}
	
	public boolean isCurrentUserOwner(final Item pItem) {
		if (! (pItem instanceof InternalItem)) {
			return false;
		}
		final InternalItem internalItem = (InternalItem)pItem;
		final Person currentPerson = getCurrentPerson();
		if (currentPerson == null) {
			return false;
		}
		return currentPerson.equals(internalItem.getOwner());
	}

	public Person getCurrentPerson() {
		return personDao.findPerson(PersonUtils.getCurrentPersonId());
	}
	
	public ListValue getListValue(final Long pListValueId) {
		CoreUtils.assertNotNull(pListValueId);
		final ListValue listValue = listValueDao.findListValue(pListValueId);
		if (listValue == null) {
			throw new ObjectNotFoundException(pListValueId, ListValue.class.getName());
		}
		return listValue;		
	}
	
}

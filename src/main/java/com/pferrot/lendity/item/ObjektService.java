package com.pferrot.lendity.item;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ObjectNotFoundException;

import com.pferrot.core.CoreUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.dao.DocumentDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.document.DocumentService;
import com.pferrot.lendity.model.ItemCategory;
import com.pferrot.lendity.model.ItemVisibility;
import com.pferrot.lendity.model.ListValue;
import com.pferrot.lendity.model.Objekt;
import com.pferrot.lendity.model.OrderedListValue;
import com.pferrot.lendity.model.Ownable;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.security.SecurityUtils;

public abstract class ObjektService {

	private final static Log log = LogFactory.getLog(ObjektService.class);
	
	private ListValueDao listValueDao;
	private PersonService personService;
	private MailManager mailManager;
	private DocumentDao documentDao;
	private DocumentService documentService;
	
	
	public ListValueDao getListValueDao() {
		return listValueDao;
	}

	public void setListValueDao(ListValueDao listValueDao) {
		this.listValueDao = listValueDao;
	}

	public DocumentDao getDocumentDao() {
		return documentDao;
	}

	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}

	public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public MailManager getMailManager() {
		return mailManager;
	}

	public void setMailManager(MailManager mailManager) {
		this.mailManager = mailManager;
	}

	public List<ListValue> getCategories() {
		return listValueDao.findListValue(ItemCategory.class);
	}
	
	public List<OrderedListValue> getVisibilities() {
		return listValueDao.findOrderedListValue(ItemVisibility.class);
	}
		
	protected Long[] getCategoryIds(final Long pCategoryId) {
		return getIds(pCategoryId);
	}
	
	protected Long[] getVisibilityIds(final Long pVisibilityId) {
		return getIds(pVisibilityId);		
	}

	public Long[] getConnectionsAndPublicVisibilityIds() {
		return new Long[]{getConnectionsVisibilityId(), getPublicVisibilityId()};
	}
	
	public Long getConnectionsVisibilityId() {
		return getListValueDao().findListValue(ItemVisibility.CONNECTIONS).getId();
	}
	
	public Long getPublicVisibilityId() {
		return getListValueDao().findListValue(ItemVisibility.PUBLIC).getId();
	}
	
	protected Long[] getIds(final Long pId) {
		Long[] ids = null;
		if (pId != null) {
			ids = new Long[1];
			ids[0] = pId;
		}
		return ids;			
	}
	
	public Person getCurrentPerson() {
		return getPersonService().getCurrentPerson();
	}
	
	public ListValue getListValue(final Long pListValueId) {
		CoreUtils.assertNotNull(pListValueId);
		final ListValue listValue = listValueDao.findListValue(pListValueId);
		if (listValue == null) {
			throw new ObjectNotFoundException(pListValueId, ListValue.class.getName());
		}
		return listValue;		
	}

	public boolean isCurrentUserOwner(final Ownable pOwnable) {
		final Person currentPerson = getCurrentPerson();
		if (currentPerson == null) {
			return false;
		}
		return currentPerson.equals(pOwnable.getOwner());
	}

	/////////////////////////////////////////////////////////
	// Access control
	
	public boolean isCurrentUserAuthorizedToView(final Objekt pObjekt) {
		return isUserAuthorizedToView(getCurrentPerson(), pObjekt);
	}

	public boolean isUserAuthorizedToView(final Person pPerson, final Objekt pObjekt) {
		CoreUtils.assertNotNull(pObjekt);
		if (!SecurityUtils.isLoggedIn()) {
			if (pObjekt.isPublicVisibility()) {
				return true;
			}
			return false;
		}
		else {
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
	    		 pObjekt.getOwner().getConnections().contains(pPerson))) {
				return true;
			}
			return false;
		}
	}
	
	public void assertCurrentUserAuthorizedToView(final Objekt pObjekt) {
		if (!isCurrentUserAuthorizedToView(pObjekt)) {
			throw new SecurityException("Current user is not authorized to view objekt");
		}
	}
	
	public void assertUserAuthorizedToView(final Person pPerson, final Objekt pObjekt) {
		if (!isUserAuthorizedToView(pPerson, pObjekt)) {
			throw new SecurityException("User is not authorized to view objekt");
		}
	}
	
	public boolean isCurrentUserAuthorizedToEdit(final Objekt pObjekt) {
		return isUserAuthorizedToEdit(getCurrentPerson(), pObjekt);
	}
	
	public boolean isUserAuthorizedToEdit(final Person pPerson, final Objekt pObjekt) {
		CoreUtils.assertNotNull(pObjekt);
		if (!SecurityUtils.isLoggedIn()) {
			return false;
		}
		else if (pPerson == null || pPerson.getUser() == null) {
			return false;
		}
		else if (pPerson.getUser() != null &&
			pPerson.getUser().isAdmin()) {
			return true;
		}
		else if (pPerson.equals(pObjekt.getOwner())) {
			return true;
		}			
		return false;
	}

	public void assertCurrentUserAuthorizedToEdit(final Objekt pObjekt) {
		if (!isCurrentUserAuthorizedToEdit(pObjekt)) {
			throw new SecurityException("Current user is not authorized to edit objekt");
		}
	}

	public boolean isCurrentUserAuthorizedToAdd() {
		final Person currentPerson = getCurrentPerson();
		if (currentPerson != null && currentPerson.getUser() != null) {
			return true;
		}
		return false;
	}

	public void assertCurrentUserAuthorizedToAdd(final Objekt pObjekt) {
		if (!isCurrentUserAuthorizedToAdd()) {
			throw new SecurityException("Current user is not authorized to add objekt");
		}
	}

	public boolean isCurrentUserAuthorizedToDelete(final Objekt pObjekt) {
		return isCurrentUserAuthorizedToEdit(pObjekt);
	}

	public void assertCurrentUserAuthorizedToDelete(final Objekt pObjekt) {
		if (!isCurrentUserAuthorizedToDelete(pObjekt)) {
			throw new SecurityException("Current user is not authorized to delete objekt");
		}
	}

	// Access control
	/////////////////////////////////////////////////////////
	
}

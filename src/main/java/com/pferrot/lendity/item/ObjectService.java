package com.pferrot.lendity.item;

import java.util.List;
import java.util.Set;

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
import com.pferrot.lendity.model.OrderedListValue;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;

public class ObjectService {

	private final static Log log = LogFactory.getLog(ObjectService.class);
	
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
	
	public List<Person> getCurrentPersonEnabledConnections() {
		return getPersonService().findConnectionsList(PersonUtils.getCurrentPersonId(), null, 0, 0);
	}
		
	protected Long[] getCategoryIds(final Long pCategoryId) {
		return getIds(pCategoryId);
	}
	
	protected Long[] getVisibilityIds(final Long pVisibilityId) {
		return getIds(pVisibilityId);		
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
	
}

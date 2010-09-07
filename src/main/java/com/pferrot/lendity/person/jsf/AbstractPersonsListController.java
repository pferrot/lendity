package com.pferrot.lendity.person.jsf;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.jsf.list.AbstractListController;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonConsts;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;

public abstract class AbstractPersonsListController extends AbstractListController {
	private final static Log log = LogFactory.getLog(AbstractPersonsListController.class);
	
	private PersonService personService;
	private ConnectionRequestService connectionRequestService;

	public AbstractPersonsListController() {
		super();
		setRowsPerPage(PersonConsts.NB_PERSONS_PER_PAGE);
	}

	public void setPersonService(final PersonService pPersonService) {
		this.personService = pPersonService;
	}
	
	public PersonService getPersonService() {
		return personService;
	}

	public ConnectionRequestService getConnectionRequestService() {
		return connectionRequestService;
	}

	public void setConnectionRequestService(final ConnectionRequestService pConnectionRequestService) {
		this.connectionRequestService = pConnectionRequestService;
	}

	public String getPersonOverviewHref() {
		final Person person = (Person)getTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(person.getId().toString());
	}

	public String getProfilePictureSrc() {
		final Person person = (Person)getTable().getRowData();
		if (FacesContext.getCurrentInstance().getRenderResponse()) {
			return personService.getProfilePictureSrc(person, true);
		}
		return null;
	}
	
	public String getProfileThumbnailSrc() {
		final Person person = (Person)getTable().getRowData();
		if (FacesContext.getCurrentInstance().getRenderResponse()) {
			return personService.getProfileThumbnailSrc(person, true);
		}
		return null;
	}
}

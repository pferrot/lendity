package com.pferrot.lendity.person.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.springframework.security.AccessDeniedException;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/person/personEdit.jspx"})
public class PersonEditController extends AbstractPersonAddEditController {
	
	private final static Log log = LogFactory.getLog(PersonEditController.class);
	
	private Person person;

	@InitView
	public void initView() {
		// Read the person ID from the request parameter and load the correct person.
		try {
			final String personIdString = JsfUtils.getRequestParameter(PagesURL.PERSON_EDIT_PARAM_PERSON_ID);
			if (personIdString != null) {
				final Long personId = Long.parseLong(personIdString);
				person = getPersonService().findPerson(personId);				
				// Access control check.
				if (!getPersonService().isCurrentUserAuthorizedToEdit(person)) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (person edit): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), person = " + personIdString);
					}
					return;
				}
				setPerson(person);
			}
			// Person not found or no person ID specified.
			if (getPerson() == null) {
				JsfUtils.redirect(PagesURL.PERSONS_LIST);
			}
		}
		catch (AccessDeniedException ade) {
			throw ade;
		}
		catch (Exception e) {
			//TODO display standard error page instead.
			JsfUtils.redirect(PagesURL.PERSONS_LIST);
		}		
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person pPerson) {
		person = pPerson;
		
		setFirstName(pPerson.getFirstName());
		setLastName(pPerson.getLastName());
		setDisplayName(pPerson.getDisplayName());
		setEmailSubscriber(pPerson.getEmailSubscriber());
	}

	public Long updatePerson() {

		getPerson().setFirstName(getFirstName());
		getPerson().setLastName(getLastName());
		getPerson().setEmailSubscriber(getEmailSubscriber());
		
		getPersonService().updatePerson(getPerson());
		
		return getPerson().getId();
	}

	public String getPersonOverviewHref() {		
		return PersonUtils.getPersonOverviewPageUrl(person.getId().toString());
	}	

	@Override
	public Long processPerson() {
		return updatePerson();
	}	
}

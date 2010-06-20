package com.pferrot.lendity.person.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.springframework.security.AccessDeniedException;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/person/personOverview.jspx"})
public class PersonOverviewController
{
	private final static Log log = LogFactory.getLog(PersonOverviewController.class);
	
	private PersonService personService;
	private Long personId;
	private Person person;
	
	@InitView
	public void initView() {
		// Read the item ID from the request parameter and load the correct item.
		try {
			final String personIdString = JsfUtils.getRequestParameter(PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID);
			Person person = null;
			if (personIdString != null) {
				personId = Long.parseLong(personIdString);
				person = personService.findPerson(personId);
				// Access control check.
				if (!personService.isCurrentUserAuthorizedToView(person)) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (person view): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), person = " + personIdString);
					}
					return;
				}
				setPerson(person);
			}
			// Item not found or not item ID specified.
			if (person == null) {
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

	public void setPersonService(final PersonService pPersonService) {
		this.personService = pPersonService;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(final Person pPerson) {
		this.person = pPerson;
	}

	public String getPersonEditHref() {		
		return PersonUtils.getPersonEditPageUrl(person.getId().toString());
	}

	public String getChangePasswordHref() {		
		return JsfUtils.getFullUrl(PagesURL.CHANGE_PASSWORD);
	}

	public boolean isEditAvailable() {
		return personService.isCurrentUserAuthorizedToEdit(person);
	}
}

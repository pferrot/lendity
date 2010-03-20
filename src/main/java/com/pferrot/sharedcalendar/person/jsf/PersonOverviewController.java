package com.pferrot.sharedcalendar.person.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.sharedcalendar.PagesURL;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.person.PersonService;
import com.pferrot.sharedcalendar.person.PersonUtils;
import com.pferrot.sharedcalendar.utils.JsfUtils;

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
				setPerson(person);
			}
			// Item not found or not item ID specified.
			if (person == null) {
				JsfUtils.redirect(PagesURL.PERSONS_LIST);
			}
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
}

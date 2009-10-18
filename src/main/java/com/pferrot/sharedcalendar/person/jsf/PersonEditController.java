package com.pferrot.sharedcalendar.person.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.sharedcalendar.PagesURL;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.person.PersonUtils;
import com.pferrot.sharedcalendar.utils.JsfUtils;

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
				setPerson(getPersonService().findPerson(Long.parseLong(personIdString)));
			}
			// Person not found or no person ID specified.
			if (getPerson() == null) {
				JsfUtils.redirect(PagesURL.PERSONS_LIST);
			}
		}
		catch (Exception e) {
			//TODO display standard error page instead.
			JsfUtils.redirect(PagesURL.PERSONS_LIST);
		}		
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Long updatePerson() {
		// TODO what to update?
		
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

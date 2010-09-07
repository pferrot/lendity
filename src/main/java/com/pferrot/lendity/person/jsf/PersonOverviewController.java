package com.pferrot.lendity.person.jsf;

import java.util.Locale;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.springframework.security.AccessDeniedException;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.connectionrequest.exception.ConnectionRequestException;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.InternalItem;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

@ViewController(viewIds={"/auth/person/personOverview.jspx"})
public class PersonOverviewController
{
	private final static Log log = LogFactory.getLog(PersonOverviewController.class);
	
	private PersonService personService;
	private ConnectionRequestService connectionRequestService;
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

	public void setConnectionRequestService(
			ConnectionRequestService connectionRequestService) {
		this.connectionRequestService = connectionRequestService;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(final Person pPerson) {
		this.person = pPerson;
	}
	
	public String getMemberSinceLabel() {
		return UiUtils.getDateAsString(person.getUser().getCreationDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}

	public String getPersonEditHref() {		
		return PersonUtils.getPersonEditPageUrl(person.getId().toString());
	}
	
	public String getPersonEditPictureHref() {		
		return PersonUtils.getPersonEditPicturePageUrl(person.getId().toString());
	}

	public String getChangePasswordHref() {		
		return JsfUtils.getFullUrl(PagesURL.CHANGE_PASSWORD);
	}

	public boolean isEditAvailable() {
		return personService.isCurrentUserAuthorizedToEdit(person);
	}
	
	public boolean isEmailAvailable() {
		return personService.isCurrentUserAuthorizedToViewEmail(person);
	}
	
	public boolean isRequestConnectionDisabled() {
		try {
			return !connectionRequestService.isConnectionRequestAllowedFromCurrentUser(person);
		}
		catch (ConnectionRequestException e) {
			throw new RuntimeException(e);
		}			
	}
	
	public String getPageTitle() {
		if (isEditAvailable()) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			return I18nUtils.getMessageResourceString("menu_profile", locale);
		}
		else {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			return I18nUtils.getMessageResourceString("person_personOverviewTitle", locale);
		}		
	}
	
	public String getProfilePictureSrc() {
		if (FacesContext.getCurrentInstance().getRenderResponse()) {
			return personService.getProfilePictureSrc(getPerson(), true);
		}
		return null;
	}
	
	public String getProfileThumbnailSrc() {
		if (FacesContext.getCurrentInstance().getRenderResponse()) {
			return personService.getProfileThumbnailSrc(getPerson(), true);
		}
		return null;
	}
	
//	public String getEmailSubscriberStatusLabel() {
//		if (Boolean.TRUE.equals(person.getEmailSubscriber())) {
//			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
//			return I18nUtils.getMessageResourceString("person_emailNotificationOn", locale);
//		}
//		else if (Boolean.FALSE.equals(person.getEmailSubscriber())) {
//			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
//			return I18nUtils.getMessageResourceString("person_emailNotificationOff", locale);
//		}
//		return "";
//	}
}

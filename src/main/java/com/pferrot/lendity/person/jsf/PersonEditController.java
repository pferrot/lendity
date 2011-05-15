package com.pferrot.lendity.person.jsf;

import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.springframework.security.AccessDeniedException;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.geolocation.bean.Coordinate;
import com.pferrot.lendity.geolocation.exception.GeolocalisationException;
import com.pferrot.lendity.geolocation.googlemaps.GoogleMapsUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonConsts;
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
			Person person = null;
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
				else {
					setPerson(person);
				}
			}
			// Person not found or no person ID specified.
			if (getPerson() == null) {
				JsfUtils.redirect(PagesURL.PERSONS_LIST);
				return;
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
		
		setWebsite(pPerson.getWebsite());
		
		setEmailSubscriber(pPerson.getEmailSubscriber());
		setReceiveNeedsNotifications(pPerson.getReceiveNeedsNotifications());
		setReceiveCommentsOnCommentedNotif(pPerson.getReceiveCommentsOnCommentedNotif());
		setReceiveCommentsOnOwnNotif(pPerson.getReceiveCommentsOnOwnNotif());
		setShowContactDetailsToAll(pPerson.getShowContactDetailsToAll());
		
		setPhoneHome(pPerson.getPhoneHome());
		setPhoneMobile(pPerson.getPhoneMobile());
		setPhoneProfessional(pPerson.getPhoneProfessional());
		
		setAddressHome(pPerson.getAddressHome());
		setAddressHomeLatitude(pPerson.getAddressHomeLatitude());
		setAddressHomeLongitude(pPerson.getAddressHomeLongitude());
	}

	public Long updatePerson() {

		getPerson().setFirstName(getFirstName());
		getPerson().setLastName(getLastName());
		getPerson().setDisplayName(getDisplayName());
		getPerson().setWebsite(getWebsite());
		getPerson().setEmailSubscriber(getEmailSubscriber());
		getPerson().setReceiveNeedsNotifications(getReceiveNeedsNotifications());
		getPerson().setReceiveCommentsOnCommentedNotif(getReceiveCommentsOnCommentedNotif());
		getPerson().setReceiveCommentsOnOwnNotif(getReceiveCommentsOnOwnNotif());
		getPerson().setShowContactDetailsToAll(getShowContactDetailsToAll());
		
		getPerson().setPhoneHome(getPhoneHome());
		getPerson().setPhoneMobile(getPhoneMobile());
		getPerson().setPhoneProfessional(getPhoneProfessional());
		
		getPerson().setAddressHome(getAddressHome());
		// Needed because validateAddressHome() is not called if empty. 
		if (StringUtils.isNullOrEmpty(getAddressHome())) {
			setAddressHomeLatitude(null);
			setAddressHomeLongitude(null);
		}
		getPerson().setAddressHomeLatitude(getAddressHomeLatitude());
		getPerson().setAddressHomeLongitude(getAddressHomeLongitude());

		getPersonService().updatePerson(getPerson());
		
		return getPerson().getId();
	}

	public String getMyProfileHref() {
		return JsfUtils.getFullUrl(PagesURL.MY_PROFILE);
	}

	@Override
	public Long processPerson() {
		return updatePerson();
	}

	public void validateAddressHome(FacesContext context, UIComponent toValidate, Object value) {
		
		String message = "";
		String address = (String) value;
		if (!StringUtils.isNullOrEmpty(address)) {
			if (address.length() > PersonConsts.MAX_ADDRESS_SIZE) {
				((UIInput)toValidate).setValid(false);
				final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				message = I18nUtils.getMessageResourceString("validation_maxSizeExceeded", new Object[]{String.valueOf(PersonConsts.MAX_ADDRESS_SIZE)}, locale);
				context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
			}
			
			else {
				try {
					final Coordinate c = GoogleMapsUtils.getCoordinate(address);
					setAddressHomeLatitude(c.getLatitude());
					setAddressHomeLongitude(c.getLongitude());
				}
				catch (GeolocalisationException e) {
					setAddressHomeLatitude(null);
					setAddressHomeLongitude(null);
					
					((UIInput)toValidate).setValid(false);
					final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
					message = I18nUtils.getMessageResourceString("validation_geolocationNotFound", locale);
					context.addMessage(toValidate.getClientId(context), new FacesMessage(message));					
				}
			}
		}
		else {
			setAddressHomeLatitude(null);
			setAddressHomeLongitude(null);
		}
	}

	public void validateDisplayName(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		String displayName = (String) value;
		if (!getPersonService().isDisplayNameAvailable(displayName, getPerson())) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			message = I18nUtils.getMessageResourceString("person_displayNameAlreadyExists", locale);
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
	}
	
//	protected boolean isModifiedAddressHome(final String pNewAddress) {
//		final String oldAddress = getPerson().getAddressHome();
//		if (pNewAddress == oldAddress) {
//			return false;
//		}
//		// The other is not null according to the first test, so they are now different.
//		else if (pNewAddress == null) {
//			return true;
//		}
//		else {
//			return !pNewAddress.equals(oldAddress);
//		}
//	}
}

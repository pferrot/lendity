package com.pferrot.lendity.person;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.pferrot.core.CoreUtils;
import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.login.jsf.HomePublicController;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.utils.CookieUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.security.SecurityUtils;

public class PersonUtils {

	/**
	 * Returns the ID of the person corresponding to the user being loged in.
	 *
	 * @return
	 */
	public static Long getCurrentPersonId() {
		return (Long)FacesContext.getCurrentInstance().getExternalContext().
					getSessionMap().get(PersonConsts.CURRENT_PERSON_ID_SESSION_ATTRIBUTE_NAME);
	}
	
	public static Long getCurrentPersonId(final HttpSession pSession) {
		return (Long)pSession.getAttribute(PersonConsts.CURRENT_PERSON_ID_SESSION_ATTRIBUTE_NAME);
	}
	
	public static String getCurrentPersonFirstName() {
		return (String)FacesContext.getCurrentInstance().getExternalContext().
					getSessionMap().get(PersonConsts.CURRENT_PERSON_FIRST_NAME_SESSION_ATTRIBUTE_NAME);
	}
	
	public static String getCurrentPersonLastName() {
		return (String)FacesContext.getCurrentInstance().getExternalContext().
					getSessionMap().get(PersonConsts.CURRENT_PERSON_LAST_NAME_SESSION_ATTRIBUTE_NAME);
	}
	
	public static String getCurrentPersonDisplayName(final HttpSession pSession) {
		return (String)pSession.getAttribute(PersonConsts.CURRENT_PERSON_DISPLAY_NAME_SESSION_ATTRIBUTE_NAME);
	}
	
	public static String getCurrentPersonDisplayName() {
		return (String)FacesContext.getCurrentInstance().getExternalContext().
					getSessionMap().get(PersonConsts.CURRENT_PERSON_DISPLAY_NAME_SESSION_ATTRIBUTE_NAME);
	}

	public static boolean isCurrentPersonIsAddressDefined() {
		if (SecurityUtils.isLoggedIn()) {
			return Boolean.TRUE.equals(FacesContext.getCurrentInstance().getExternalContext().
						getSessionMap().get(PersonConsts.CURRENT_PERSON_IS_ADDRESS_DEFINED_SESSION_ATTRIBUTE_NAME));
		}
		else {
			final String s = CookieUtils.getCookieValue(HomePublicController.COOKIE_LOCATION_LABEL);
			return !StringUtils.isNullOrEmpty(s);
		}
	}
	
	public static Double getCurrentPersonAddressHomeLatitude() {
		if (SecurityUtils.isLoggedIn()) {
			return (Double)FacesContext.getCurrentInstance().
				getExternalContext().getSessionMap().get(PersonConsts.CURRENT_PERSON_ADDRESS_HOME_LATITUDE_SESSION_ATTRIBUTE_NAME);
		}
		else {
			final String s = CookieUtils.getCookieValue(HomePublicController.COOKIE_LOCATION_LATITUDE);
			if (!StringUtils.isNullOrEmpty(s)) {
				return Double.valueOf(s);
			}
			else {
				return null;
			}		
		}
	}
	
	public static Double getCurrentPersonAddressHomeLongitude() {
		if (SecurityUtils.isLoggedIn()) {
			return (Double)FacesContext.getCurrentInstance().
				getExternalContext().getSessionMap().get(PersonConsts.CURRENT_PERSON_ADDRESS_HOME_LONGITUDE_SESSION_ATTRIBUTE_NAME);
		}
		else {
			final String s = CookieUtils.getCookieValue(HomePublicController.COOKIE_LOCATION_LONGITUDE);
			if (!StringUtils.isNullOrEmpty(s)) {
				return Double.valueOf(s);
			}
			else {
				return null;
			}
		}
	}
	
	
	
	/**
	 * Returns the HTML link to a person overview page.
	 * 
	 * @param pPersonId
	 * @return
	 */
	public static String getPersonOverviewPageUrl(final String pPersonId) {
		CoreUtils.assertNotNullOrEmptyString(pPersonId);
		
		return JsfUtils.getFullUrl(PagesURL.PERSON_OVERVIEW, PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID, pPersonId);
	}
	
	/**
	 * 
	 * @param pPersonId
	 * @return
	 */
	public static String getPersonEditPageUrl(final String pPersonId) {
		CoreUtils.assertNotNullOrEmptyString(pPersonId);
		
		return JsfUtils.getFullUrl(PagesURL.PERSON_EDIT, PagesURL.PERSON_EDIT_PARAM_PERSON_ID, pPersonId);
	}
	
	/**
	 * 
	 * @param pPersonId
	 * @return
	 */
	public static String getPersonEditPicturePageUrl(final String pPersonId) {
		CoreUtils.assertNotNullOrEmptyString(pPersonId);
		
		return JsfUtils.getFullUrl(PagesURL.PERSON_EDIT_PICTURE, PagesURL.PERSON_EDIT_PICTURE_PARAM_PERSON_ID, pPersonId);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getPersonsListUrl() {		
		return JsfUtils.getFullUrl(PagesURL.PERSONS_LIST);
	}

	/**
	 *
	 * @param pPerson
	 * @return
	 */
	public static boolean isActiveApplicationUser(final Person pPerson) {
		return isApplicationUser(pPerson) && 
			   pPerson.getUser().getEnabled();
	}

	/**
	 *
	 * @param pPerson
	 * @return
	 */
	public static boolean isApplicationUser(final Person pPerson) {
		return pPerson != null && 
			   pPerson.getUser() != null;
	}

	/**
	 * Sets some attributes in the session about the current person so that it is not
	 * always necessary to query the DB to get that information.
	 *
	 * @param pPerson
	 * @param pRequest
	 */
	public static void updatePersonInSession(final Person pPerson, final HttpServletRequest pRequest) {
		if (pPerson != null) {
			pRequest.getSession().setAttribute(PersonConsts.CURRENT_PERSON_ID_SESSION_ATTRIBUTE_NAME, pPerson.getId());
			pRequest.getSession().setAttribute(PersonConsts.CURRENT_PERSON_FIRST_NAME_SESSION_ATTRIBUTE_NAME, pPerson.getFirstName());
			pRequest.getSession().setAttribute(PersonConsts.CURRENT_PERSON_LAST_NAME_SESSION_ATTRIBUTE_NAME, pPerson.getLastName());
			pRequest.getSession().setAttribute(PersonConsts.CURRENT_PERSON_DISPLAY_NAME_SESSION_ATTRIBUTE_NAME, pPerson.getDisplayName());
			pRequest.getSession().setAttribute(PersonConsts.CURRENT_PERSON_IS_ADDRESS_DEFINED_SESSION_ATTRIBUTE_NAME, Boolean.valueOf(pPerson.isAddressHomeDefined()));
			pRequest.getSession().setAttribute(PersonConsts.CURRENT_PERSON_ADDRESS_HOME_LATITUDE_SESSION_ATTRIBUTE_NAME, pPerson.getAddressHomeLatitude());
			pRequest.getSession().setAttribute(PersonConsts.CURRENT_PERSON_ADDRESS_HOME_LONGITUDE_SESSION_ATTRIBUTE_NAME, pPerson.getAddressHomeLongitude());
		}		
	}
}

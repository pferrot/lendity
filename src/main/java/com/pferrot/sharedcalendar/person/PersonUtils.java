package com.pferrot.sharedcalendar.person;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.pferrot.core.CoreUtils;
import com.pferrot.sharedcalendar.PagesURL;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.utils.JsfUtils;

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
	
	public static String getCurrentPersonFirstName() {
		return (String)FacesContext.getCurrentInstance().getExternalContext().
					getSessionMap().get(PersonConsts.CURRENT_PERSON_FIRST_NAME_SESSION_ATTRIBUTE_NAME);
	}
	
	public static String getCurrentPersonLastName() {
		return (String)FacesContext.getCurrentInstance().getExternalContext().
					getSessionMap().get(PersonConsts.CURRENT_PERSON_LAST_NAME_SESSION_ATTRIBUTE_NAME);
	}
	
	public static String getCurrentPersonDisplayName() {
		return (String)FacesContext.getCurrentInstance().getExternalContext().
					getSessionMap().get(PersonConsts.CURRENT_PERSON_DISPLAY_NAME_SESSION_ATTRIBUTE_NAME);
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
		}		
	}
}

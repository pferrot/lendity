package com.pferrot.sharedcalendar.person;

import com.pferrot.core.CoreUtils;
import com.pferrot.sharedcalendar.PagesURL;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.utils.JsfUtils;

public class PersonUtils {

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
}

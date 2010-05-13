package com.pferrot.sharedcalendar.login.jsf;

import com.pferrot.security.SecurityUtils;
import com.pferrot.sharedcalendar.PagesURL;
import com.pferrot.sharedcalendar.login.AuthenticationConsts;
import com.pferrot.sharedcalendar.person.PersonUtils;
import com.pferrot.sharedcalendar.utils.JsfUtils;

public class AuthenticationController {
	
	public boolean isLoggedIn() {
		return SecurityUtils.isLoggedIn();
	}

	public String getCurrentUserFirstName() {
		return PersonUtils.getCurrentPersonFirstName();
	}

	public String getCurrentUserLastName() {
		return PersonUtils.getCurrentPersonLastName();
	}

	public String getCurrentUserDisplayName() {
		return PersonUtils.getCurrentPersonDisplayName();
	}

	public String getLoginURL() {
		return JsfUtils.getContextRoot() + PagesURL.LOGIN;
	}

	public String getLogoutURL() {
		return JsfUtils.getContextRoot() + AuthenticationConsts.LOGOUT_URL;
	}

	public String getRegistrationURL() {
		return JsfUtils.getContextRoot() + PagesURL.REGISTRATION;
	}

	public String getLostPasswordURL() {
		return JsfUtils.getContextRoot() + PagesURL.LOST_PASSWORD;
	}

	public String getHomeURL() {
		return JsfUtils.getContextRoot() + PagesURL.HOME;
	}
	
	public String getPersonsURL() {
		return JsfUtils.getContextRoot() + PagesURL.PERSONS_LIST;
	}
	
	public String getMyConnectionsURL() {
		return JsfUtils.getContextRoot() + PagesURL.MY_CONNECTIONS_LIST;
	}

	public String getMyItemsURL() {
		return JsfUtils.getContextRoot() + PagesURL.MY_ITEMS_LIST;
	}

	public String getMyConnectionsItemsURL() {
		return JsfUtils.getContextRoot() + PagesURL.MY_CONNECTIONS_ITEMS_LIST;
	}
	
	public String getMyProfileURL() {
		return JsfUtils.getContextRoot() + PagesURL.MY_PROFILE;
	}
}

package com.pferrot.lendity.login.jsf;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.item.ItemUtils;
import com.pferrot.lendity.login.AuthenticationConsts;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.security.SecurityUtils;

public class AuthenticationController {
	
	public boolean isLoggedIn() {
		return SecurityUtils.isLoggedIn();
	}
	
	public boolean isLoginPage() {
		return JsfUtils.isCurrentPage(PagesURL.LOGIN);
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
	
	public String getTermsAndConditionsURL() {
		return JsfUtils.getContextRoot() + PagesURL.TERMS_AND_CONDITIONS;
	}
	
	public String getContactURL() {
		return JsfUtils.getContextRoot() + PagesURL.CONTACT;
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
	
	public String getMyBannedPersonsURL() {
		return JsfUtils.getContextRoot() + PagesURL.MY_BANNED_PERSONS_LIST;
	}

	public String getMyPendingConnectionRequestsURL() {
		return JsfUtils.getContextRoot() + PagesURL.MY_PENDING_CONNECTION_REQUESTS_LIST;
	}

	public String getMyPendingConnectionRequestsOutURL() {
		return JsfUtils.getContextRoot() + PagesURL.MY_PENDING_CONNECTION_REQUESTS_OUT_LIST;
	}
	
	public String getMyConnectionsUpdatesURL() {
		return JsfUtils.getContextRoot() + PagesURL.MY_CONNECTIONS_UPDATES_LIST;
	}

	public String getMyPendingLendRequestsURL() {
		return JsfUtils.getContextRoot() + PagesURL.MY_PENDING_LEND_REQUESTS_LIST;
	}

	public String getMyPendingLendRequestsOutURL() {
		return JsfUtils.getContextRoot() + PagesURL.MY_PENDING_LEND_REQUESTS_OUT_LIST;
	}
	
	public String getImportInternalItemsURL() {
		return JsfUtils.getContextRoot() + PagesURL.INTERNAL_ITEMS_IMPORT;
	}

	public String getMyItemsURL() {
		return JsfUtils.getContextRoot() + PagesURL.MY_ITEMS_LIST;
	}

	public String getMyBorrowedItemsURL() {
		return JsfUtils.getContextRoot() + PagesURL.MY_BORROWED_ITEMS_LIST;
	}

	public String getMyConnectionsItemsURL() {
		return JsfUtils.getContextRoot() + PagesURL.MY_CONNECTIONS_ITEMS_LIST;
	}
	
	public String getInternalItemAddURL() {
		return ItemUtils.getInternalItemAddPageUrl();
	}

	public String getExternalItemAddURL() {
		return ItemUtils.getExternalItemAddPageUrl();
	}
	
	public String getMyProfileURL() {
//		return JsfUtils.getContextRoot() + PagesURL.MY_PROFILE;
		return JsfUtils.getFullUrl(PagesURL.PERSON_OVERVIEW, PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID, PersonUtils.getCurrentPersonId().toString());
	}
}

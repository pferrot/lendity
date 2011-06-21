package com.pferrot.lendity.login.jsf;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.item.ItemUtils;
import com.pferrot.lendity.item.jsf.SearchItemsListController;
import com.pferrot.lendity.login.AuthenticationConsts;
import com.pferrot.lendity.need.jsf.SearchNeedsListController;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.NavigationUtils;
import com.pferrot.security.SecurityUtils;

public class AuthenticationController {
	
	public String getCurrentPageThroughLoginURL() {
		return NavigationUtils.getCurrentPageThroughLoginURL();
	}
	
	public boolean isLoggedIn() {
		return SecurityUtils.isLoggedIn();
	}
	
	public boolean isLoginPage() {
		return JsfUtils.isCurrentPage(PagesURL.LOGIN);
	}
	
	public boolean isInviteFriendsPage() {
		return JsfUtils.isCurrentPage(PagesURL.INVITE_FRIENDS);
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
	
	public String getFaqURL() {
		return JsfUtils.getContextRoot() + PagesURL.FAQ;
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

	public String getInviteFriendsURL() {
		return JsfUtils.getContextRoot() + PagesURL.INVITE_FRIENDS;
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
	
	public String getImportItemsURL() {
		return JsfUtils.getContextRoot() + PagesURL.ITEMS_IMPORT;
	}

	public String getMyItemsURL() {
		return JsfUtils.getContextRoot() + PagesURL.MY_ITEMS_LIST;
	}

	public String getMyBorrowedItemsURL() {
		return JsfUtils.getContextRoot() + PagesURL.MY_IN_PROGRESS_LEND_TRANSACTIONS_OUT_LIST;
	}

	public String getItemsSearchURL() {
		return JsfUtils.getFullUrl(PagesURL.ITEMS_SEARCH);
	}

	public String getItemsSearchOrderByCreationDateURL() {
		return JsfUtils.getFullUrl(PagesURL.ITEMS_SEARCH,
				SearchItemsListController.FORCE_VIEW_PARAM_NAME,
				SearchItemsListController.FORCE_VIEW_ALL_BY_CREATION_DATE_VALUE);
	}

	public String getMyLentItemsURL() {
		return JsfUtils.getFullUrl(PagesURL.MY_IN_PROGRESS_LEND_TRANSACTIONS_LIST);
	}
	
	public String getItemAddURL() {
		return ItemUtils.getItemAddPageUrl();
	}
	
	public String getMyNeedsURL() {
		return JsfUtils.getContextRoot() + PagesURL.MY_NEEDS_LIST;
	}

	public String getNeedsSearchURL() {
		return JsfUtils.getContextRoot() + PagesURL.NEEDS_SEARCH;
	}

	public String getNeedsSearchOrderByCreationDateURL() {
		return JsfUtils.getFullUrl(PagesURL.NEEDS_SEARCH,
				SearchNeedsListController.FORCE_VIEW_PARAM_NAME,
				SearchNeedsListController.FORCE_VIEW_ALL_BY_CREATION_DATE_VALUE);
	}
	
	public String getNeedAddURL() {
		return JsfUtils.getFullUrl(PagesURL.NEED_ADD);
	}
	
	public String getMyProfileURL() {
		return JsfUtils.getFullUrl(PagesURL.MY_PROFILE);
	}
	
	public String getMyLendTransactionsURL() {
		return JsfUtils.getFullUrl(PagesURL.MY_LEND_TRANSACTIONS_LIST);
	}
	
	public String getMyLendTransactionsWaitingForInputURL() {
		return JsfUtils.getFullUrl(PagesURL.MY_LEND_TRANSACTIONS_WAITING_FOR_INPUT_LIST);
	}

	public String getMyEvaluationsURL() {
		return JsfUtils.getFullUrl(PagesURL.MY_EVALUATIONS_LIST);
	}

	public String getMyEvaluationsMadeURL() {
		return JsfUtils.getFullUrl(PagesURL.MY_EVALUATIONS_MADE_LIST);
	}

	public String getGroupAddURL() {
		return JsfUtils.getFullUrl(PagesURL.GROUP_ADD);
	}

	public String getGroupsURL() {
		return JsfUtils.getFullUrl(PagesURL.GROUPS_LIST);
	}

	public String getMyGroupsURL() {
		return JsfUtils.getFullUrl(PagesURL.MY_GROUPS_LIST);
	}
	
	public String getMyGroupsWhereOwnerOrAdministratorURL() {
		return JsfUtils.getFullUrl(PagesURL.MY_GROUPS_WHERE_OWNER_OR_ADMINISTRATOR_LIST);
	}
	
	public String getMyPendingGroupJoinRequestsListURL() {
		return JsfUtils.getFullUrl(PagesURL.MY_PENDING_GROUP_JOIN_REQUESTS_LIST);
	}
	
	public String getMyPendingGroupJoinRequestsOutListURL() {
		return JsfUtils.getFullUrl(PagesURL.MY_PENDING_GROUP_JOIN_REQUESTS_OUT_LIST);
	}
	
	public String getEditProfileURL() {		
		return PersonUtils.getPersonEditPageUrl(PersonUtils.getCurrentPersonId().toString());
	}
	
}

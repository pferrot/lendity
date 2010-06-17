package com.pferrot.lendity;

public interface PagesURL {

	/////////////////////////////////////////////////////////////////////////
	// ERROR - start
	
	String ERROR = "/public/error/error.faces";
	
	// ERROR - end
	/////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////
	// LOGIN - start
	
	String LOGIN = "/login.faces";
	
	// LOGIN - end
	/////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////
	// REGISTRATION - start
	
	String REGISTRATION = "/public/registration/registration.faces";
	String REGISTRATION_VALIDATION = "/public/registration/registrationValidation.faces";
	
	// REGISTRATION - end
	/////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////
	// LOST PASSWORD - start
	
	String LOST_PASSWORD = "/public/lostpassword/lostpassword.faces";
	
	// LOST PASSWORD - end
	/////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////
	// CHANGE PASSWORD - start
	
	String CHANGE_PASSWORD = "/auth/changepassword/changepassword.faces";
	
	// CHANGE PASSWORD - end
	/////////////////////////////////////////////////////////////////////////	

	/////////////////////////////////////////////////////////////////////////
	// HOME - start
	
	String HOME = "/auth/home.faces";
	
	// HOME - end
	/////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////
	// ITEM - start
	
	String INTERNAL_ITEM_OVERVIEW = "/auth/item/internalItemOverview.faces";
	String INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID = "itemID";
	
	String INTERNAL_ITEM_ADD = "/auth/item/internalItemAdd.faces";
	
	String INTERNAL_ITEM_EDIT = "/auth/item/internalItemEdit.faces";
	String INTERNAL_ITEM_EDIT_PARAM_ITEM_ID = INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID;
	
	String INTERNAL_ITEM_LEND_TOOLTIP = "/auth/item/internalItemLendTooltip.faces";
	String INTERNAL_ITEM_LEND_TOOLTIP_PARAM_ITEM_ID = INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID;
	
	String EXTERNAL_ITEM_OVERVIEW = "/auth/item/externalItemOverview.faces";
	String EXTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID = "itemID";
	
	String EXTERNAL_ITEM_ADD = "/auth/item/externalAdd.faces";
	
	String EXTERNAL_ITEM_EDIT = "/auth/item/externalEdit.faces";
	String EXTERNAL_ITEM_EDIT_PARAM_ITEM_ID = EXTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID;	
	
	String ITEMS_LIST = "/auth/item/itemsList.faces";
	String MY_ITEMS_LIST = "/auth/item/myItemsList.faces";
	String MY_BORROWED_ITEMS_LIST = "/auth/item/myBorrowedItemsList.faces";
	String MY_CONNECTIONS_ITEMS_LIST = "/auth/item/myConnectionsItemsList.faces";
	
	// ITEM - end
	/////////////////////////////////////////////////////////////////////////

	
	/////////////////////////////////////////////////////////////////////////
	// CONNECTION REQUEST - start	
	
	// Connection requests I have to reply to.
	String MY_PENDING_CONNECTION_REQUESTS_LIST = "/auth/connectionrequest/myPendingConnectionRequestsList.faces";
	// Connection requests I have made.
	String MY_PENDING_CONNECTION_REQUESTS_OUT_LIST = "/auth/connectionrequest/myPendingConnectionRequestsOutList.faces";
	
	// CONNECTION REQUEST - end
	/////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////
	// LEND REQUEST - start	
	
	// Lend requests I have to reply to.
	String MY_PENDING_LEND_REQUESTS_LIST = "/auth/lendrequest/myPendingLendRequestsList.faces";
	// Lend requests I have made.
	String MY_PENDING_LEND_REQUESTS_OUT_LIST = "/auth/lendrequest/myPendingLendRequestsOutList.faces";
	
	// LEND REQUEST - end
	/////////////////////////////////////////////////////////////////////////	
	
	
	/////////////////////////////////////////////////////////////////////////
	// PERSON - start	
	
	String MY_PROFILE = "/auth/person/myProfile.faces";
	
	String PERSONS_LIST = "/auth/person/personsList.faces";
	String MY_CONNECTIONS_LIST = "/auth/person/myConnectionsList.faces";
	String MY_BANNED_PERSONS_LIST = "/auth/person/myBannedPersonsList.faces";
	
	String PERSON_OVERVIEW = "/auth/person/personOverview.faces";
	String PERSON_OVERVIEW_PARAM_PERSON_ID = "personID";
	
	String PERSON_EDIT = "/auth/person/personEdit.faces";
	String PERSON_EDIT_PARAM_PERSON_ID = PERSON_OVERVIEW_PARAM_PERSON_ID;	
	
	// PERSON - end
	/////////////////////////////////////////////////////////////////////////	
}

package com.pferrot.lendity;

public interface PagesURL {

	/////////////////////////////////////////////////////////////////////////
	// ERROR - start
	
	String ERROR_GENERAL_ERROR = "/public/error/error.faces";
	String ERROR_ACCESS_DENIED = "/public/error/accessDenied.faces";
	String ERROR_PAGE_NOT_FOUND = "/public/error/error_404.faces";
	
	// ERROR - end
	/////////////////////////////////////////////////////////////////////////
	
/////////////////////////////////////////////////////////////////////////
	// TERMS AND CONDITIONS - start
	
	String TERMS_AND_CONDITIONS = "/public/termsandconditions/termsAndConditions.faces";
	
	// TERMS AND CONDITIONS - end
	/////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////
	// CONTACT - start
	
	String CONTACT = "/public/contact/contact.faces";
	
	// CONTACT - end
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
	String INTERNAL_ITEM_ADD_PARAM_NEED_ID = "needID";
	
	String INTERNAL_ITEM_EDIT = "/auth/item/internalItemEdit.faces";
	String INTERNAL_ITEM_EDIT_PARAM_ITEM_ID = INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID;
	
	String INTERNAL_ITEM_LEND_TOOLTIP = "/auth/item/internalItemLendTooltip.faces";
	String INTERNAL_ITEM_LEND_TOOLTIP_PARAM_ITEM_ID = INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID;
	
	String INTERNAL_ITEMS_IMPORT = "/auth/item/internalItemsImport.faces";
	
	String EXTERNAL_ITEM_OVERVIEW = "/auth/item/externalItemOverview.faces";
	String EXTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID = INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID;
	
	String EXTERNAL_ITEM_ADD = "/auth/item/externalItemAdd.faces";
	
	String EXTERNAL_ITEM_EDIT = "/auth/item/externalItemEdit.faces";
	String EXTERNAL_ITEM_EDIT_PARAM_ITEM_ID = EXTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID;	
	
	String ITEMS_LIST = "/auth/item/itemsList.faces";
	String MY_ITEMS_LIST = "/auth/item/myItemsList.faces";
	String MY_BORROWED_ITEMS_LIST = "/auth/item/myBorrowedItemsList.faces";
	String MY_CONNECTIONS_ITEMS_LIST = "/auth/item/myConnectionsItemsList.faces";
	
	// ITEM - end
	/////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////
	// NEED - start
	
	String NEED_OVERVIEW = "/auth/need/needOverview.faces";
	String NEED_OVERVIEW_PARAM_NEED_ID = "needID";
	
	String NEED_ADD = "/auth/need/needAdd.faces";
	
	String NEED_EDIT = "/auth/need/needEdit.faces";
	String NEED_EDIT_PARAM_NEED_ID = NEED_OVERVIEW_PARAM_NEED_ID;
	
	String MY_NEEDS_LIST = "/auth/need/myNeedsList.faces";
	String MY_CONNECTIONS_NEEDS_LIST = "/auth/need/myConnectionsNeedsList.faces";
	
	// NEED - end
	/////////////////////////////////////////////////////////////////////////
	
	
	/////////////////////////////////////////////////////////////////////////
	// CONNECTION REQUEST - start	
	
	// Connection requests I have to reply to.
	String MY_PENDING_CONNECTION_REQUESTS_LIST = "/auth/connectionrequest/myPendingConnectionRequestsList.faces";
	// Connection requests I have made.
	String MY_PENDING_CONNECTION_REQUESTS_OUT_LIST = "/auth/connectionrequest/myPendingConnectionRequestsOutList.faces";
	
	String MY_CONNECTIONS_UPDATES_LIST = "/auth/connectionrequest/myConnectionsUpdatesList.faces";
	
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
	
	String PERSON_EDIT_PICTURE = "/auth/person/personEditPicture.faces";
	String PERSON_EDIT_PICTURE_PARAM_PERSON_ID = PERSON_OVERVIEW_PARAM_PERSON_ID;
	
	// PERSON - end
	/////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////
	// DOCUMENT - start
	
	String DOCUMENT_DOWNLOAD = "/auth/document/documentDownload.htm";
	
	String DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_ID = "documentID";
	
	String DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_PATH = "documentPath";
	String DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_ORIGINAL_FILE_NAME = "documentOriginalFileName";
	String DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_MIME_TYPE = "documentMimeType";
	
	String DOCUMENT_DOWNLOAD_PARAM_AS_ATTACHMENT = "attachment";
	
	// DOCUMENT - end
	/////////////////////////////////////////////////////////////////////////
}

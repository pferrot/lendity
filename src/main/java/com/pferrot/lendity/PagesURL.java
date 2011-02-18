package com.pferrot.lendity;

import com.pferrot.lendity.login.filter.CustomAuthenticationProcessingFilter;

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
	String LOGIN_PARAM_REDIRECT_TO = CustomAuthenticationProcessingFilter.REDIRECT_TO_PARAMETER_NAME;
	
	// LOGIN - end
	/////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////
	// REGISTRATION - start
	
	String REGISTRATION = "/public/registration/registration.faces";
	String REGISTRATION_VALIDATION = "/public/registration/registrationValidation.faces";
	
	// REGISTRATION - end
	/////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////
	// INVITATION - start
	
	String INVITE_FRIENDS = "/auth/invitation/inviteFriends.faces";
	
	// INVITATION - end
	/////////////////////////////////////////////////////////////////////////	
	
	/////////////////////////////////////////////////////////////////////////
	// LOST PASSWORD - start
	
	String LOST_PASSWORD = "/public/lostpassword/lostpassword.faces";
	String RESET_PASSWORD = "/public/lostpassword/resetPassword.faces";
	
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
	
	String INTERNAL_ITEM_OVERVIEW = "/public/item/internalItemOverview.faces";
	String INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID = "itemID";
	
	String INTERNAL_ITEM_ADD = "/auth/item/internalItemAdd.faces";
	String INTERNAL_ITEM_ADD_PARAM_NEED_ID = "needID";
	
	String INTERNAL_ITEM_EDIT = "/auth/item/internalItemEdit.faces";
	String INTERNAL_ITEM_EDIT_PARAM_ITEM_ID = INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID;
	
	String INTERNAL_ITEM_EDIT_PICTURE = "/auth/item/internalItemEditPicture.faces";
	String INTERNAL_ITEM_EDIT_PICTURE_PARAM_ITEM_ID = INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID;
	
	String INTERNAL_ITEM_LEND_TOOLTIP = "/auth/item/internalItemLendTooltip.faces";
	String INTERNAL_ITEM_LEND_TOOLTIP_PARAM_ITEM_ID = INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID;
	
	String INTERNAL_ITEMS_IMPORT = "/auth/item/internalItemsImport.faces";
	
	String ITEMS_LIST = "/auth/item/itemsList.faces";
	String MY_ITEMS_LIST = "/auth/item/myItemsList.faces";
	String MY_LENT_ITEMS_LIST = "/auth/item/myLentItemsList.faces";
	String MY_BORROWED_ITEMS_LIST = "/auth/item/myBorrowedItemsList.faces";
	String ITEMS_SEARCH = "/public/item/search.faces";
	
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
	// LEND TRANSACTION - start
	
	String LEND_TRANSACTION_OVERVIEW = "/auth/lendtransaction/lendTransactionOverview.faces";
	String LEND_TRANSACTION_OVERVIEW_PARAM_NEED_ID = "lendTransactionID";
	
	String MY_IN_PROGRESS_LEND_TRANSACTIONS_LIST = "/auth/lendtransaction/myInProgressLendTransactionsList.faces";
	String MY_IN_PROGRESS_LEND_TRANSACTIONS_OUT_LIST = "/auth/lendtransaction/myInProgressLendTransactionsOutList.faces";

	String MY_COMPLETED_LEND_TRANSACTIONS_LIST = "/auth/lendtransaction/myClosedLendTransactionsList.faces";
	String MY_COMPLETED_LEND_TRANSACTIONS_OUT_LIST = "/auth/lendtransaction/myClosedLendTransactionsOutList.faces";

	String MY_LEND_TRANSACTIONS_LIST = "/auth/lendtransaction/mylendTransactionsList.faces";
	
	// LEND TRANSACTION - end
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
	
	String PERSON_OVERVIEW = "/public/person/personOverview.faces";
	String PERSON_OVERVIEW_PARAM_PERSON_ID = "personID";
	
	String PERSON_EDIT = "/auth/person/personEdit.faces";
	String PERSON_EDIT_PARAM_PERSON_ID = PERSON_OVERVIEW_PARAM_PERSON_ID;
	
	String PERSON_EDIT_PICTURE = "/auth/person/personEditPicture.faces";
	String PERSON_EDIT_PICTURE_PARAM_PERSON_ID = PERSON_OVERVIEW_PARAM_PERSON_ID;
	
	// PERSON - end
	/////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////
	// DOCUMENT - start
	
	String DOCUMENT_DOWNLOAD = "/public/document/documentDownload.htm";
	
	String DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_ID = "documentID";
	
	String DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_PATH = "documentPath";
	String DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_ORIGINAL_FILE_NAME = "documentOriginalFileName";
	String DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_MIME_TYPE = "documentMimeType";
	
	String DOCUMENT_DOWNLOAD_PARAM_AS_ATTACHMENT = "attachment";
	
	// DOCUMENT - end
	/////////////////////////////////////////////////////////////////////////
}

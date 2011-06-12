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
	// TERMS AND CONDITIONS - start
	
	String FAQ = "/public/faq/faq.faces";
	
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
	String HOME_PUBLIC = LOGIN;
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
	// PERSON - start	
	
	String MY_PROFILE = "/auth/person/myProfile.faces";
	
	String PERSONS_LIST = "/public/person/personsList.faces";
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
	// GROUP - start		
	
	String GROUP_ADD = "/auth/group/groupOverview.faces";
	
	String GROUP_OVERVIEW = "/public/group/groupOverview.faces";
	String GROUP_OVERVIEW_PARAM_GROUP_ID = "groupID";
	
	String GROUP_EDIT = "/auth/group/groupEdit.faces";
	String GROUP_EDIT_PARAM_GROUP_ID = GROUP_OVERVIEW_PARAM_GROUP_ID;
	
	String GROUP_EDIT_PICTURE = "/auth/group/groupEditPicture.faces";
	String GROUP_EDIT_PICTURE_PARAM_GROUP_ID = GROUP_OVERVIEW_PARAM_GROUP_ID;
	
	String GROUPS_LIST = "/public/group/groupsList.faces";
	
	String MY_GROUPS_LIST = "/auth/group/myGroupsList.faces";
	
	String MY_GROUPS_WHERE_OWNER_OR_ADMINISTRATOR_LIST = "/auth/group/myGroupsWhereOwnerOrAdministratorList.faces";
	
	String GROUP_MEMBERS_LIST = "/public/group/groupMembersList.faces";
	String GROUP_MEMBERS_LIST_PARAM_GROUP_ID = GROUP_OVERVIEW_PARAM_GROUP_ID;
	
	String GROUP_ADMINISTRATORS_LIST = "/public/group/groupAdministratorsList.faces";
	String GROUP_ADMINISTRATORS_LIST_PARAM_GROUP_ID = GROUP_OVERVIEW_PARAM_GROUP_ID;
	
	
	// GROUP - end
	/////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////
	// GROUP JOIN REQUEST - start	
	
	// Group join requests I have to reply to.
	String MY_PENDING_GROUP_JOIN_REQUESTS_LIST = "/auth/groupjoinrequest/myPendingGroupJoinRequestsList.faces";
	// Group join requests I have made.
	String MY_PENDING_GROUP_JOIN_REQUESTS_OUT_LIST = "/auth/groupjoinrequest/myPendingGroupJoinRequestsOutList.faces";
	
	// GROUP JOIN REQUEST - end
	/////////////////////////////////////////////////////////////////////////
	
	
	/////////////////////////////////////////////////////////////////////////
	// ITEM - start
	
	String ITEM_OVERVIEW = "/public/item/itemOverview.faces";
	String ITEM_OVERVIEW_PARAM_ITEM_ID = "itemID";
	
	String ITEM_ADD = "/auth/item/itemAdd.faces";
	String ITEM_ADD_PARAM_NEED_ID = "needID";
	
	String ITEM_EDIT = "/auth/item/itemEdit.faces";
	String ITEM_EDIT_PARAM_ITEM_ID = ITEM_OVERVIEW_PARAM_ITEM_ID;
	
	String ITEM_EDIT_PICTURE = "/auth/item/itemEditPicture.faces";
	String ITEM_EDIT_PICTURE_PARAM_ITEM_ID = ITEM_OVERVIEW_PARAM_ITEM_ID;
	
	String ITEM_LEND_TOOLTIP = "/auth/item/itemLendTooltip.faces";
	String ITEM_LEND_TOOLTIP_PARAM_ITEM_ID = ITEM_OVERVIEW_PARAM_ITEM_ID;
	
	String ITEMS_IMPORT = "/auth/item/itemsImport.faces";
	
	String ITEMS_LIST = "/auth/item/itemsList.faces";
	String MY_ITEMS_LIST = "/auth/item/myItemsList.faces";
	String MY_LENT_ITEMS_LIST = "/auth/item/myLentItemsList.faces";
	String MY_BORROWED_ITEMS_LIST = "/auth/item/myBorrowedItemsList.faces";
	String ITEMS_SEARCH = "/public/item/searchItems.faces";
	String PERSON_ITEMS_LIST = "/public/item/personItemsList.faces";
	String PERSON_ITEMS_LIST_PARAM_PERSON_ID = PERSON_OVERVIEW_PARAM_PERSON_ID;
	
	// ITEM - end
	/////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////
	// NEED - start
	
	String NEED_OVERVIEW = "/public/need/needOverview.faces";
	String NEED_OVERVIEW_PARAM_NEED_ID = "needID";
	
	String NEED_ADD = "/auth/need/needAdd.faces";
	
	String NEED_EDIT = "/auth/need/needEdit.faces";
	String NEED_EDIT_PARAM_NEED_ID = NEED_OVERVIEW_PARAM_NEED_ID;
	
	String MY_NEEDS_LIST = "/auth/need/myNeedsList.faces";
	String NEEDS_SEARCH = "/public/need/searchNeeds.faces";
	String PERSON_NEEDS_LIST = "/public/need/personNeedsList.faces";
	String PERSON_NEEDS_LIST_PARAM_PERSON_ID = PERSON_OVERVIEW_PARAM_PERSON_ID;
	
	// NEED - end
	/////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////
	// LEND TRANSACTION - start
	
	String LEND_TRANSACTION_OVERVIEW = "/auth/lendtransaction/lendTransactionOverview.faces";
	String LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID = "lendTransactionID";
	
	String LEND_TRANSACTION_EDIT = "/auth/lendtransaction/lendTransactionEdit.faces";
	String LEND_TRANSACTION_EDIT_PARAM_LEND_TRANSACTION_ID = LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID;
	
	String MY_IN_PROGRESS_LEND_TRANSACTIONS_LIST = "/auth/lendtransaction/myInProgressLendTransactionsList.faces";
	String MY_IN_PROGRESS_LEND_TRANSACTIONS_OUT_LIST = "/auth/lendtransaction/myInProgressLendTransactionsOutList.faces";

	String MY_COMPLETED_LEND_TRANSACTIONS_LIST = "/auth/lendtransaction/myClosedLendTransactionsList.faces";
	String MY_COMPLETED_LEND_TRANSACTIONS_OUT_LIST = "/auth/lendtransaction/myClosedLendTransactionsOutList.faces";

	String MY_LEND_TRANSACTIONS_LIST = "/auth/lendtransaction/myLendTransactionsList.faces";
	
	String MY_LEND_TRANSACTIONS_WAITING_FOR_INPUT_LIST = "/auth/lendtransaction/myLendTransactionsWaitingForInputList.faces";

	// Does not exist. Take care of AC if implement!!!
//	String LEND_TRANSACTIONS_FOR_ITEM_AND_PERSON_LIST = "/auth/lendtransaction/lendTransactionsItemPersonList.faces";
	String LEND_TRANSACTIONS_FOR_ITEM_AND_PERSON_LIST_PARAM_ITEM_ID = ITEM_OVERVIEW_PARAM_ITEM_ID;
	String LEND_TRANSACTIONS_FOR_ITEM_AND_PERSON_LIST_PARAM_PERSON_ID = PERSON_OVERVIEW_PARAM_PERSON_ID;
	
	String MY_LEND_TRANSACTIONS_FOR_ITEM_LIST = "/auth/lendtransaction/myLendTransactionsForItemList.faces";
	String MY_LEND_TRANSACTIONS_FOR_ITEM_LIST_PARAM_ITEM_ID = LEND_TRANSACTIONS_FOR_ITEM_AND_PERSON_LIST_PARAM_ITEM_ID;

	
	// LEND TRANSACTION - end
	/////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////
	// EVALUATION - start
	
	String EVALUATION_OVERVIEW = "/auth/evaluation/evaluationOverview.faces";
	String EVALUATION_OVERVIEW_PARAM_EVALUATION_ID = "evaluationID";
	
	String EVALUATION_ADD = "/auth/evaluation/evaluationAdd.faces";
	String EVALUATION_ADD_PARAM_LEND_TRANSACTION_ID = LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID;
	
	String PERSON_EVALUATIONS_LIST = "/auth/evaluation/personEvaluationsList.faces";
	String PERSON_EVALUATIONS_LIST_PARAM_PERSON_ID = "personID";
	
	String PERSON_EVALUATIONS_MADE_LIST = "/auth/evaluation/personEvaluationsMadeList.faces";
	String PERSON_EVALUATIONS_MADE_LIST_PARAM_PERSON_ID = "personID";
	
	String MY_EVALUATIONS_LIST = "/auth/evaluation/myEvaluationsList.faces";
	
	String MY_EVALUATIONS_MADE_LIST = "/auth/evaluation/myEvaluationsMadeList.faces";

	
	// EVALUATION - end
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

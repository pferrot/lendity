package com.pferrot.lendity.lendrequest;

public interface LendRequestConsts {
	
	int NB_LEND_REQUESTS_PER_PAGE = -1;
	
	// Constants used to identify the case when checking whether
	// a lend request is authorized or not.
	int REQUEST_LEND_ALLOWED = 1;
	int REQUEST_LEND_NOT_ALLOWED_TRANSACTION_UNCOMPLETED = 2;
	int REQUEST_LEND_NOT_ALLOWED_BANNED_PERSON = 3;
	int REQUEST_LEND_NOT_ALLOWED_NOT_LOGGED_IN = 4;
	int REQUEST_LEND_NOT_ALLOWED_OWN_ITEM = 5;

}

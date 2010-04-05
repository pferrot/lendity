package com.pferrot.sharedcalendar;

public interface PagesURL {
	
	String ROOT_URL = "http://localhost:8080/shared_calendar";

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
	// HOME - start
	
	String HOME = "/auth/home.faces";
	
	// HOME - end
	/////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////
	// ITEM - start
	
	String ITEM_OVERVIEW = "/auth/item/itemOverview.faces";
	String ITEM_OVERVIEW_PARAM_ITEM_ID = "itemID";
	
	String ITEM_ADD = "/auth/item/itemAdd.faces";
	
	String ITEM_EDIT = "/auth/item/itemEdit.faces";
	String ITEM_EDIT_PARAM_ITEM_ID = ITEM_OVERVIEW_PARAM_ITEM_ID;
	
	String ITEMS_LIST = "/auth/item/itemsList.faces";
	String MY_ITEMS_LIST = "/auth/item/myItemsList.faces";
	
	// ITEM - end
	/////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////
	// MY PROFILE - start
	
	String MY_PROFILE = "/auth/profile/myProfile.faces";
	
	// MY PROFILE - end
	/////////////////////////////////////////////////////////////////////////

	
	
	
	/////////////////////////////////////////////////////////////////////////
	// PERSON - start	
	
	String PERSONS_LIST = "/auth/person/personsList.faces";
	String MY_CONNECTIONS_LIST = "/auth/person/myConnectionsList.faces";
	
	String PERSON_OVERVIEW = "/auth/person/personOverview.faces";
	String PERSON_OVERVIEW_PARAM_PERSON_ID = "personID";
	
	String PERSON_EDIT = "/auth/person/personEdit.faces";
	String PERSON_EDIT_PARAM_PERSON_ID = PERSON_OVERVIEW_PARAM_PERSON_ID;	
	
	// PERSON - end
	/////////////////////////////////////////////////////////////////////////	
}

package com.pferrot.lendity.person;

public interface PersonConsts {

	int NB_PERSONS_PER_PAGE = 20;
	String CURRENT_PERSON_ID_SESSION_ATTRIBUTE_NAME = "currentPersonId";
	String CURRENT_PERSON_FIRST_NAME_SESSION_ATTRIBUTE_NAME = "currentPersonFirstName";
	String CURRENT_PERSON_LAST_NAME_SESSION_ATTRIBUTE_NAME = "currentPersonLastName";
	String CURRENT_PERSON_DISPLAY_NAME_SESSION_ATTRIBUTE_NAME = "currentPersonDisplayName";
	String CURRENT_PERSON_IS_ADDRESS_DEFINED_SESSION_ATTRIBUTE_NAME = "currentPersonIsAddressDefined";
	String CURRENT_PERSON_ADDRESS_HOME_LATITUDE_SESSION_ATTRIBUTE_NAME = "currentPersonAddressHomeLatitude";
	String CURRENT_PERSON_ADDRESS_HOME_LONGITUDE_SESSION_ATTRIBUTE_NAME = "currentPersonAddressHomeLongitude";
	
	String DUMMY_PROFILE_PICTURE_URL = "/public/images/icons/user.gif";
	String DUMMY_PROFILE_THUMBNAIL_URL = "/public/images/icons/user_small.gif";
	
	int MAX_ADDRESS_SIZE = 300;
}

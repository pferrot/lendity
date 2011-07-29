package com.pferrot.lendity.potentialconnection.contactsreader;

import java.util.Set;

import com.pferrot.lendity.potentialconnection.bean.PotentialConnectionContactBean;
import com.pferrot.lendity.potentialconnection.exception.PotentialConnectionException;

public interface PotentialConnectionContactsReader {
	
	int MAX_CONTACTS_TO_FETCH = 3000;

	Set<PotentialConnectionContactBean> getContacts(final Object pObject) throws PotentialConnectionException;
}

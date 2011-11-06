package com.pferrot.lendity.potentialconnection.contactsreader;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.util.ServiceException;
import com.pferrot.lendity.potentialconnection.bean.PotentialConnectionContactBean;
import com.pferrot.lendity.potentialconnection.exception.PotentialConnectionException;

/**
 * For reading potential contacts from Google.
 *
 * @author pferrot
 *
 */
public class GooglePotentialConnectionContactsReader implements PotentialConnectionContactsReader {

	private final static Log log = LogFactory.getLog(GooglePotentialConnectionContactsReader.class);
	
	public Set<PotentialConnectionContactBean> getContacts(final Object pObject) throws PotentialConnectionException {
		try {
			final ContactsService myService = (ContactsService)pObject;
			
			// Request the feed
			URL feedUrl = new URL("https://www.google.com/m8/feeds/contacts/default/full");
			Query myQuery = new Query(feedUrl);
			myQuery.setMaxResults(PotentialConnectionContactsReader.MAX_CONTACTS_TO_FETCH);
			ContactFeed resultFeed = myService.query(myQuery, ContactFeed.class);
			
			Set<PotentialConnectionContactBean> result = new HashSet<PotentialConnectionContactBean>();
			// Print the results
			if (log.isDebugEnabled()) {
				log.debug(resultFeed.getTitle().getPlainText());
			}
			for (int i = 0; i < resultFeed.getEntries().size(); i++) {
				String theName = null; 
				ContactEntry entry = resultFeed.getEntries().get(i);
				if (entry.hasName()) {
					Name name = entry.getName();
					if (name.hasFullName()) {
						theName = name.getFullName().getValue();
						if (log.isDebugEnabled()) {
							log.debug("Full name: " + theName);
						}
					}
				}
				// Same name can have several emails - this is normal.
				for (Email email : entry.getEmailAddresses()) {
					String oneEmail = email.getAddress();					
					PotentialConnectionContactBean pc = new PotentialConnectionContactBean(oneEmail, theName);
					result.add(pc);
				}
			}
			return result;
		}
		catch (ServiceException se) {
			throw new PotentialConnectionException(se);
		}
		catch (IOException ie) {
			throw new PotentialConnectionException(ie);
		}	
	}
}

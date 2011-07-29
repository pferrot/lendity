package com.pferrot.lendity.potentialconnection.contactsreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.potentialconnection.bean.PotentialConnectionContactBean;
import com.pferrot.lendity.potentialconnection.exception.PotentialConnectionException;

/**
 * For reading potential contacts from a text area. One contact per line. First email, then name:
 * 
 * john@123.com John The King
 * bob@test.com
 * fritz@tada.ch Fritz
 * 
 * @author pferrot
 *
 */
public class TextareaPotentialConnectionContactsReader implements PotentialConnectionContactsReader {
	
	private final static Log log = LogFactory.getLog(TextareaPotentialConnectionContactsReader.class);
	
	public Set<PotentialConnectionContactBean> getContacts(final Object pObject) throws PotentialConnectionException {		
		final Set<PotentialConnectionContactBean> result = new HashSet<PotentialConnectionContactBean>();
		
		final String text = (String)pObject;
		if (text == null) {
			return result;
		}
		int counter = 0;
		StringTokenizer st = new StringTokenizer(text, "\n\r");
		while (st.hasMoreTokens()) {
			final String oneToken = st.nextToken();
			
			if (log.isDebugEnabled()) {
				log.debug("One token: " + oneToken);
			}
			if (!StringUtils.isNullOrEmpty(oneToken)) {
				counter++;
				if (counter > PotentialConnectionContactsReader.MAX_CONTACTS_TO_FETCH) {
					return result;
				}
				result.add(getContactBean(oneToken));
			}
		}			
		return result;
	}
	
	public static PotentialConnectionContactBean getContactBean(final String pText) {
		final int i = pText.indexOf(" ");
		String email = null;
		String name = null;
		if (i > 0) {
			email = pText.substring(0, i);
			email = email.trim();
			name = pText.substring(i);
			name = name.trim();
			if (name.isEmpty()) {
				name = null;
			}
		}
		else {
			email = pText.trim();
		}
		return new PotentialConnectionContactBean(email, name);
	}
}

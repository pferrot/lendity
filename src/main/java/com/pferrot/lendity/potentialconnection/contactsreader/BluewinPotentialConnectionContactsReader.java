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
import com.pferrot.lendity.utils.file.FileConsts;
import com.pferrot.lendity.utils.file.exception.NotUtf8FileException;

/**
 * For reading potential contacts from a CSV file exported from Bluewin mail.
 * First name == column 2
 * Middle name == column 3
 * Last name == column 4
 * Email 1 == column 6
 * Email 2 == column 7
 * Email 3 == column 8
 * 
 * @author pferrot
 *
 */
public class BluewinPotentialConnectionContactsReader extends AbstractFilePotentialConnectionContactsReader {
	
	private final static Log log = LogFactory.getLog(BluewinPotentialConnectionContactsReader.class);
	
	@Override
	public Set<PotentialConnectionContactBean> process(final InputStream pIs, final String pEncoding) throws PotentialConnectionException, NotUtf8FileException {
		try {			
			final Set<PotentialConnectionContactBean> result = new HashSet<PotentialConnectionContactBean>();
			
			// Files exported from bluewin are using that encoding.
			final InputStreamReader isr = new InputStreamReader(pIs, pEncoding);
			final BufferedReader bufferedReader = new BufferedReader(isr);
			String line = null;
			int counter = 0;
			
			boolean firstLine = true;	
			
			// First just read the file - no db access.
			while ((line = bufferedReader.readLine()) != null) {
				// Skip headers.
				if (firstLine) {
					firstLine = false;
					continue;
				}
				if (log.isDebugEnabled()) {
					log.debug(line);
				}
				if (!StringUtils.isNullOrEmpty(line)) {
					if (line.contains(FileConsts.UNICODE_REPLACEMENT_CHARACTER)) {
						if (log.isWarnEnabled()) {
							log.warn("Line contains Unicode Replacement Character: " + line);
						}
						throw new NotUtf8FileException();
					}
					counter++;
					final StringTokenizer st = new StringTokenizer(line, ",");
					int columIndex = 0;
					String firstName = null;
					String middleName = null;
					String lastName = null;
					String email1 = null;
					String email2 = null;
					String email3 = null;
					while (st.hasMoreTokens()) {
						columIndex++;
						final String token = st.nextToken();
						if (columIndex == getFirstNameColumnIndex()) {
							firstName = getValueWithourQuotes(token);
						}
						else if (columIndex == getMiddleNameColumnIndex()) {
							middleName = getValueWithourQuotes(token);						
						}
						else if (columIndex == getLastNameColumnIndex()) {
							lastName = getValueWithourQuotes(token);
						}
						else if (columIndex == getEmail1NameColumnIndex()) {
							email1 = getValueWithourQuotes(token);
						}
						else if (columIndex == getEmail2NameColumnIndex()) {
							email2 = getValueWithourQuotes(token);
						}
						else if (columIndex == getEmail3NameColumnIndex()) {
							email3 = getValueWithourQuotes(token);
						}
					}
					final StringBuffer nameSb = new StringBuffer();
					if (!StringUtils.isNullOrEmpty(firstName)) {
						nameSb.append(firstName);
						nameSb.append(" ");
					}
					if (!StringUtils.isNullOrEmpty(middleName)) {
						nameSb.append(middleName);
						nameSb.append(" ");
					}
					if (!StringUtils.isNullOrEmpty(lastName)) {
						nameSb.append(lastName);
					}
					String name = nameSb.toString().trim();
					if (StringUtils.isNullOrEmpty(name)) {
						name = null;
					}
					if (!StringUtils.isNullOrEmpty(email1)) {
						result.add(new PotentialConnectionContactBean(email1, name));
					}
					if (!StringUtils.isNullOrEmpty(email2)) {
						result.add(new PotentialConnectionContactBean(email2, name));
					}
					if (!StringUtils.isNullOrEmpty(email3)) {
						result.add(new PotentialConnectionContactBean(email3, name));
					}
					if (counter > PotentialConnectionContactsReader.MAX_CONTACTS_TO_FETCH) {
						return result;
					}
					
				}
			}			
			return result;
		}
		catch (IOException ie) {
			throw new PotentialConnectionException(ie);
		}		
		
	}

	protected String getValueWithourQuotes(final String pText) {
		if (pText == null) {
			return null;
		}
		String result = pText.trim();
		if (result == null || result.length() == 0) {
			return null;
		}
		if (result.charAt(0) == '"') {
			if (result.length() == 1) {
				return null;
			}
			result = result.substring(1);
		}
		if (result.charAt(result.length() - 1) == '"') {
			if (result.length() == 1) {
				return null;
			}
			result = result.substring(0, result.length() - 1);
		}
		return result.trim();
	}

	protected int getFirstNameColumnIndex() {
		return 2;
	}
	
	protected int getMiddleNameColumnIndex() {
		return 3;
	}
	
	protected int getLastNameColumnIndex() {
		return 4;
	}
	
	protected int getEmail1NameColumnIndex() {
		return 6;
	}
	
	protected int getEmail2NameColumnIndex() {
		return 7;
	}
	
	protected int getEmail3NameColumnIndex() {
		return 8;
	}
}

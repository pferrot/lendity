package com.pferrot.lendity.potentialconnection.contactsreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.potentialconnection.bean.PotentialConnectionContactBean;
import com.pferrot.lendity.potentialconnection.exception.PotentialConnectionException;
import com.pferrot.lendity.utils.file.FileConsts;
import com.pferrot.lendity.utils.file.exception.NotUtf8FileException;

/**
 * For reading potential contacts from a text file. One contact per line. First email, then name:
 * 
 * john@123.com John The King
 * bob@test.com
 * fritz@tada.ch Fritz
 * 
 * @author pferrot
 *
 */
public class FilePotentialConnectionContactsReader extends AbstractFilePotentialConnectionContactsReader {
	
	private final static Log log = LogFactory.getLog(FilePotentialConnectionContactsReader.class);
	
	@Override
	protected Set<PotentialConnectionContactBean> process(final InputStream pIs, final String pEncoding) throws NotUtf8FileException, PotentialConnectionException {
		try {
			final Set<PotentialConnectionContactBean> result = new HashSet<PotentialConnectionContactBean>();
			final InputStreamReader isr = new InputStreamReader(pIs, pEncoding);
			final BufferedReader bufferedReader = new BufferedReader(isr);
			String line = null;
			int counter = 0;
	
			// First just read the file - no db access.
			while ((line = bufferedReader.readLine()) != null) {
				if (log.isDebugEnabled()) {
					log.debug(line);
				}
				if (!StringUtils.isNullOrEmpty(line)) {
					// Fix bug when the first character of the first line is the BOM (Byte Order Mark), unicode = 65279.
					int firstCharUnicode = line.charAt(0);
					if (firstCharUnicode == 65279) {
						if (line.length() > 1) {
							line = line.substring(1);
						}
						else {
							line = null;
						}
					}
					// Still not null or empty !?
					if (!StringUtils.isNullOrEmpty(line)) {
						if (line.contains(FileConsts.UNICODE_REPLACEMENT_CHARACTER)) {
							if (log.isWarnEnabled()) {
								log.warn("Line contains Unicode Replacement Character: " + line);
							}
							throw new NotUtf8FileException();
						}
						counter++;
						if (counter > PotentialConnectionContactsReader.MAX_CONTACTS_TO_FETCH) {
							return result;
						}
						result.add(TextareaPotentialConnectionContactsReader.getContactBean(line));
					}
				}
			}			
			return result;
		}
		catch (IOException ie) {
			throw new PotentialConnectionException(ie);
		}	
	}
}

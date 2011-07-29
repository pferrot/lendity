package com.pferrot.lendity.potentialconnection.contactsreader;

import java.io.InputStream;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.potentialconnection.bean.PotentialConnectionContactBean;
import com.pferrot.lendity.potentialconnection.exception.PotentialConnectionException;
import com.pferrot.lendity.utils.file.exception.NotUtf8FileException;


public abstract class AbstractFilePotentialConnectionContactsReader implements PotentialConnectionContactsReader {
	
	private final static Log log = LogFactory.getLog(AbstractFilePotentialConnectionContactsReader.class);
	
	protected abstract Set<PotentialConnectionContactBean> process(final InputStream pIs, final String pEncoding) throws PotentialConnectionException, NotUtf8FileException;
	
	public Set<PotentialConnectionContactBean> getContacts(final Object pObject) throws PotentialConnectionException {
		try {			
			final InputStream is = (InputStream)pObject;
			is.mark(10000000);
			
			try {
				return process(is, "UTF-8");
			}
			catch (NotUtf8FileException e) {
				is.reset();
				return process(is, "ISO-8859-1");
			}
			
		}
		catch (Exception ie) {
			throw new PotentialConnectionException(ie);
		}	
	}
}

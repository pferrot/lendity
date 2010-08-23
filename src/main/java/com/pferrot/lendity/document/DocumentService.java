package com.pferrot.lendity.document;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.dao.DocumentDao;
import com.pferrot.lendity.model.Document;

public class DocumentService {
	
	private final static Log log = LogFactory.getLog(DocumentService.class);
	
	private DocumentDao documentDao;

	// Key = document ID
	// Value = Map: key = personId
	//              value = expiration date in miliseconds
	private Map<Long, Map<Long, Long>> downloadsAuthorizations = Collections.synchronizedMap(new HashMap<Long, Map<Long, Long>>()); 
	
	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}

	public Document findDocument(final Long pDocumentId) {
		return documentDao.findDocument(pDocumentId);		
	}


	/**
	 * Allows a person to download a document until a given date.
	 *
	 * @param pSession
	 * @param pDocumentId
	 * @param pPersonId
	 * @param pExpirationTime
	 */
	public void authorizeDownload(final HttpSession pSession, final Long pDocumentId, final Long pExpirationTime) {
		CoreUtils.assertNotNull(pSession);
		CoreUtils.assertNotNull(pDocumentId);
		CoreUtils.assertNotNull(pExpirationTime);
		
		Map<Long, Long> userAuthorizations = (Map<Long, Long>)pSession.getAttribute(DocumentConsts.AUTHORIZATIONS_SESSION_ATTRIBUTE_NAME);
		if (userAuthorizations == null) {
			userAuthorizations = Collections.synchronizedMap(new HashMap<Long, Long>());
			pSession.setAttribute(DocumentConsts.AUTHORIZATIONS_SESSION_ATTRIBUTE_NAME, userAuthorizations);
		}
		userAuthorizations.put(pDocumentId, pExpirationTime);
	}

	public void authorizeDownloadOneMinute(final HttpSession pSession, final Long pDocumentId) {
		final long expiration = Calendar.getInstance().getTimeInMillis() + 60000;
		authorizeDownload(pSession, pDocumentId, expiration);
	}

	/**
	 * Returns true if the person with the given ID is authorized to download the given document.
	 * 
	 * @param pSession
	 * @param pDocumentId
	 * @return
	 */
	public boolean isAuthorizedDownload(final HttpSession pSession, final Long pDocumentId) {
		CoreUtils.assertNotNull(pDocumentId);
		CoreUtils.assertNotNull(pSession);
		final Map<Long, Long> userAuthorizations = (Map<Long, Long>)pSession.getAttribute(DocumentConsts.AUTHORIZATIONS_SESSION_ATTRIBUTE_NAME);
		if (userAuthorizations == null) {
			return false;
		}
		final Long expirationTime = userAuthorizations.get(pDocumentId);
		if (expirationTime == null) {
			return false;
		}
		return expirationTime.longValue() > Calendar.getInstance().getTimeInMillis();
	}
}

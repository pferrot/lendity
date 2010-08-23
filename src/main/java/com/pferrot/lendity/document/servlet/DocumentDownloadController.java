package com.pferrot.lendity.document.servlet;

import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.document.DocumentService;
import com.pferrot.lendity.model.Document;
import com.pferrot.lendity.person.PersonUtils;


/**
 * Servlet to download documents.
 *
 * Not that custom AC applies - users are allowed to access files for
 * a given duration only. See authorizeDownload() and isAuthorizedDownload()
 * in DocumentService.
 *
 * @author pferrot
 *
 */
public class DocumentDownloadController extends AbstractController {
	
	private final static Log log = LogFactory.getLog(DocumentDownloadController.class);
	
	private DocumentService documentService;

	public DocumentDownloadController() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DocumentService getDocumentService() {
		return documentService;
	}
	
	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	protected ModelAndView handleRequestInternal(final HttpServletRequest pRequest, final HttpServletResponse pResponse) throws Exception {
		try {
			final String documentIdAsString = pRequest.getParameter(PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_ID);
			if (documentIdAsString == null) {
				return null;
			}
			final Long documentId = Long.parseLong(documentIdAsString);
			
			final Long currentPersonId = PersonUtils.getCurrentPersonId(pRequest.getSession());			
			
			if (documentService.isAuthorizedDownload(pRequest.getSession(), documentId)) {
				final Document document = documentService.findDocument(documentId);
				
				pResponse.setContentType(document.getMimeType());
				if ("true".equals(pRequest.getParameter(PagesURL.DOCUMENT_DOWNLOAD_PARAM_AS_ATTACHMENT))) {
					pResponse.setHeader("Content-Disposition", "attachment; filename="+ document.getName());
				}
				final ServletOutputStream out = pResponse.getOutputStream();
				
				final InputStream in = document.getContent().getBinaryStream();
				final byte[] buffer = new byte[in.available()];
				while(in.read(buffer) != -1) {
					out.write(buffer);
				}
				out.flush();
			}
			else {
				if (log.isDebugEnabled()) {
					log.debug("Forbidden access person ID: " + currentPersonId + " for document : " + documentIdAsString);
				}
			}
		}
		catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}
		}
		// Always return null.
		return null;
	}

}

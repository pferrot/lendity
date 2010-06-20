package com.pferrot.lendity.error;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

public class CustomErrorPageWriter {
	
	private final static Log log = LogFactory.getLog(CustomErrorPageWriter.class);
	
	public CustomErrorPageWriter() {
		super();
	}

	public static void handleException(final FacesContext pFacesContext, final Exception pException)
			throws ServletException, IOException {
		if (log.isErrorEnabled()) {
			String message = "Exception";
			String userInfo = PersonUtils.getCurrentPersonDisplayName();
			if (userInfo != null) {
				userInfo += " (ID: " + PersonUtils.getCurrentPersonId() + ")";	 
			}
			else {
				userInfo = "anonymous";
			}
			message += " for user: " + userInfo;
			log.error(message, pException);
		}
        JsfUtils.redirect(PagesURL.ERROR_GENERAL_ERROR);
    }	
}


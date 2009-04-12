package com.pferrot.sharedcalendar.utils;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class JsfUtils {
	
	public static String getRequestParameter(final String parameterName) {
		HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		return request.getParameter(parameterName);
	}
}

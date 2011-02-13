package com.pferrot.lendity.utils;

import java.io.IOException;
import java.util.Enumeration;

import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpServletRequest;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;

public class NavigationUtils {
	

	/**
	 * Redirects the user to the login page and then to the current page as
	 * when he is logged in.
	 */
	public static void redirectToCurrentPageThroughLogin() {
		try {
			final ExternalContext externalContext = JsfUtils.getExternalContext();
			// Do not use JsfUtils.redirect() - it would add the context path a second time,
			// which would result in a wrong URL.
			externalContext.redirect(getCurrentPageThroughLoginURL());
		}
		catch (IOException e) {
			throw new JsfException(e);
		}
	}
	
	/**
	 * Returns the URL to login and automatically be redirect to the current page after login.
	 * 
	 * @return
	 */
	public static String getCurrentPageThroughLoginURL() {
		final HttpServletRequest request = JsfUtils.getRequest();
		
		final String requestURI = request.getRequestURI();
		final String contextPath = request.getContextPath();
		// Need to remove the context path because it is added
		// by the authentication filter.
		String requestURIMinusContextPath = null;
		if (contextPath != null && contextPath.length() > 2) {
			requestURIMinusContextPath = requestURI.substring(contextPath.length(), requestURI.length());
		}
		else {
			requestURIMinusContextPath = requestURI;
		}
		
		final StringBuffer currentPage = new StringBuffer(requestURIMinusContextPath);
		
		final Enumeration<String> paramNames = request.getParameterNames();
		
		boolean isFirst = true;
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			currentPage.append(isFirst?"?":"&");
			currentPage.append(paramName);
			currentPage.append("=");
			currentPage.append(request.getParameter(paramName));
			isFirst = false;
		}
		
		
		
		return JsfUtils.getFullUrl(PagesURL.LOGIN, PagesURL.LOGIN_PARAM_REDIRECT_TO, currentPage.toString());	
	}

}

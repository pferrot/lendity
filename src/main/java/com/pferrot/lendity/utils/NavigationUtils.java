package com.pferrot.lendity.utils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.item.jsf.AbstractItemAddEditController;

public class NavigationUtils {
	
	private final static Log log = LogFactory.getLog(NavigationUtils.class);

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
		String currentPage = getCurrentPageWithParameters();		
		return JsfUtils.getFullUrl(PagesURL.LOGIN, PagesURL.LOGIN_PARAM_REDIRECT_TO, currentPage.toString());	
	}
	
	/**
	 * Returns the current page URI without context path.
	 *
	 * @return
	 */
	public static String getCurrentPageWithParameters() {
		return getCurrentPageWithParameters(null, null, null);
		
	}

	/**
	 * Returns the current page URI without context path.
	 * If pParametersToKeep is not null, only parameter names in that set will be kept.
	 * If pParameterName is not null, that parameter and its value will be added/replaced.
	 * 
	 * @param pParameterName
	 * @param pParameterValue
	 * @param pParametersToKeep
	 * @return
	 */
	public static String getCurrentPageWithParameters(final String pParameterName,
			                                          final String pParameterValue,
			                                          final Set<String> pParametersToKeep) {
		final HttpServletRequest request = JsfUtils.getRequest();
		
		final String requestURI = request.getRequestURI();
	
		final StringBuffer currentPage = new StringBuffer(requestURI);
		
		final Enumeration<String> paramNames = request.getParameterNames();
		
		boolean isFirst = true;
		boolean isTheParamFound = false;
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			if (pParametersToKeep == null ||
				pParametersToKeep.contains(paramName)) {
				currentPage.append(isFirst?"?":"&");
				currentPage.append(paramName);
				currentPage.append("=");
				if (pParameterName != null &&
					paramName.equalsIgnoreCase(pParameterName)) {
					isTheParamFound = true;
					currentPage.append(pParameterValue);
				}
				else {
					currentPage.append(request.getParameter(paramName));
				}
				isFirst = false;
			}
		}
		
		if (pParameterName != null &&
			!isTheParamFound) {
			currentPage.append(isFirst?"?":"&");
			currentPage.append(pParameterName);
			currentPage.append("=");
			currentPage.append(pParameterValue);
		}		
		
		final String contextPath = request.getContextPath();
		// Need to remove the context path because it is added
		// by the authentication filter.
		if (contextPath != null && contextPath.length() > 2) {
			return currentPage.substring(contextPath.length(), currentPage.length());
		}
		
		return currentPage.toString();
		
	}

	public static String getUrl(final String pOriginalUrl, final String pParameterName, final String pParameterValue) {
		String result = null;
		// No parameter in the original URL
		final int indexOfQuestionMark = pOriginalUrl.indexOf("?");
		if (indexOfQuestionMark < 0) {
			result = pOriginalUrl + "?" + pParameterName + "=" + pParameterValue;
		}
		else {
			final String beginning = pOriginalUrl.substring(0, indexOfQuestionMark);
			final String end = pOriginalUrl.substring(indexOfQuestionMark);
			final int indexOfParameterName = end.indexOf(pParameterName);
			// Parameter was not present before.
			if (indexOfParameterName < 0) {
				result = pOriginalUrl + "&" + pParameterName + "=" + pParameterValue;
			}
			// Parameter is present - replace value.
			else {
				final String beginningOfParams = end.substring(0, indexOfParameterName);
				String endOfParams = end.substring(indexOfParameterName + pParameterName.length());
				final int indexOfEquals = end.indexOf("=");
				final int indexOfAnd = end.indexOf("&");
				if (indexOfEquals < 0 && indexOfAnd < 0) {
					result = beginning + beginningOfParams + pParameterName + "=" + pParameterValue;
				}
				else if (indexOfEquals < 0) {
					result = beginning + beginningOfParams + pParameterName + "=" + pParameterValue + endOfParams;
				}
				else if (indexOfAnd < 0) {
					result = beginning + beginningOfParams + pParameterName + "=" + pParameterValue;
				}
				else if (indexOfEquals > indexOfAnd) {
					result = beginning + beginningOfParams + pParameterName + "=" + pParameterValue + endOfParams;
				}
				else {
					// Remove previous parameter value.
					endOfParams = endOfParams.substring(endOfParams.indexOf("&"));
					result = beginning + beginningOfParams + pParameterName + "=" + pParameterValue + endOfParams;
				}
			}
		}
		
		if (log.isErrorEnabled()) {
			log.error("URL: " + result);
		}
		return result;
	}

}

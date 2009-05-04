package com.pferrot.sharedcalendar.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Some methods to make our lives easier with JSF.
 * 
 * @author Patrice
 *
 */
public class JsfUtils {
	
	private final static Log log = LogFactory.getLog(JsfUtils.class);
	
	public static final String URL_ENCODING = "UTF-8";
	
	/**
	 * Returns a request parameter.
	 * 
	 * @param parameterName
	 * @return
	 */
	public static String getRequestParameter(final String pParameterName) {
		HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		return request.getParameter(pParameterName);
	}

	/**
	 * Returns the full URL to a page, i.e. with context path and parameters.
	 * 
	 * @param pUrl
	 * @param pParametersValues
	 * @return
	 */
	public static String getFullUrl(final String pUrl, final String[][] pParametersValues) {
		try {
			final FacesContext facesContext = FacesContext.getCurrentInstance();
			final ExternalContext externalContext = facesContext.getExternalContext(); 
			
			final String contextPath = externalContext.getRequestContextPath();
			
			final StringBuffer finalUrl = new StringBuffer(contextPath);
			finalUrl.append(pUrl);
			
			if (pParametersValues != null) {
				boolean firstParameter = pUrl.indexOf("?") < 0;
				for (String[] paramValue: pParametersValues) {
					if (firstParameter) {
						finalUrl.append("?");
						
						firstParameter = false;
					}
					else {
						finalUrl.append("&");
					}
					
					finalUrl.append(paramValue[0]);
					finalUrl.append("=");
					finalUrl.append(URLEncoder.encode(paramValue[1], URL_ENCODING));				
				}
			}
			
			if (log.isDebugEnabled()) {
				log.debug("Redirect URL: " + finalUrl.toString());
			}
			
			return finalUrl.toString();
		}
		catch (UnsupportedEncodingException e) {
			throw new JsfException(e);
		}		
	}
	
	public static String getFullUrl(final String pUrl) {
		return getFullUrl(pUrl, null);
	}
	
	public static String getFullUrl(final String pUrl, final String pParam, final String pValue) {
		return getFullUrl(pUrl, constructParametersValues(pParam, pValue));
	}
	
	/**
	 * Generates an HTTP redirect with an unlimited number of parameters
	 * to be appended to the URL.
	 * 
	 * @param pUrl
	 * @param pParametersValues
	 * @throws IOException
	 */
	public static void redirect(final String pUrl, final String[][] pParametersValues) {
		try {
			
			final FacesContext facesContext = FacesContext.getCurrentInstance();
			final ExternalContext externalContext = facesContext.getExternalContext();			
			
			externalContext.redirect(getFullUrl(pUrl, pParametersValues));
		}
		catch (IOException e) {
			throw new JsfException(e);
		}
	}
	
	/**
	 * Generates an HTTP redirect with one parameter / value,
	 * 
	 * @param pUrl
	 * @param pParam
	 * @param pValue
	 * @throws IOException
	 */
	public static void redirect(final String pUrl, final String pParam, final String pValue) {
		redirect(pUrl, constructParametersValues(pParam, pValue));
	}
	
	/**
	 * Generates an HTTP redirect without any parameter.
	 * 
	 * @param pUrl
	 * @throws IOException
	 */
	public static void redirect(final String pUrl) {
		redirect(pUrl, null);
	}
	
	private static String[][] constructParametersValues(final String pParam, final String pValue) {
		final String[] paramValue = new String[2];
		paramValue[0] = pParam;
		paramValue[1] = pValue;
		final String[][] parametersValues = new String[1][];
		parametersValues[0] = paramValue;
		return parametersValues;
	}
}

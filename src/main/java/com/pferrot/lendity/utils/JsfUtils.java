package com.pferrot.lendity.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

    /*
     * Application Helper Methods
     */
    public static String getContextRoot()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return facesContext.getExternalContext().getRequestContextPath();
    }

    /*
     * Servlet Utility Methods
     */
    public static HttpServletRequest getHttpServletRequest()
    {
        ExternalContext context = getExternalContext();
        return (HttpServletRequest) context.getRequest();
    }

    public static HttpServletResponse getHttpServletResponse()
    {
        ExternalContext context = getExternalContext();
        return (HttpServletResponse) context.getResponse();
    }

    public static HttpSession getSession()
    {
        ExternalContext context = getExternalContext();
        HttpSession session = (HttpSession) context.getSession(false);
        return session;
    }
	
	/**
	 * Returns a request parameter.
	 * 
	 * @param parameterName
	 * @return
	 */
	public static String getRequestParameter(final String pParameterName) {
		HttpServletRequest request = (HttpServletRequest)getExternalContext().getRequest();
		return request.getParameter(pParameterName);
	}
	
	public static HttpServletRequest getRequest() {
		return (HttpServletRequest)getExternalContext().getRequest();
	}
	
	/**
	 * Returns a session attribute.
	 *
	 * @param key
	 * @return
	 */
    public static Object getSessionAttribute(final String key)
    {
        return getExternalContext().getSessionMap().get(key);
    }

    /**
     * Sets a session attribute.
     *
     * @param key
     * @param value
     */
    public static void setSessionAttribute(final String key, final Object value)
    {
        getExternalContext().getSessionMap().put(key, value);
    }

    /**
     * Returns the value of a UI component.
     *
     * @param component
     * @param fieldName
     * @return
     */
    public static Object getFormValue(final UIComponent component, final String fieldName)
    {
        String componentId = (String) component.getAttributes().get(fieldName);
        UIInput input = (UIInput) getFacesContext().getViewRoot().findComponent(componentId);
        return input.getValue();
    }

	/**
	 * Finds component with the given id
	 */
	public static UIComponent findComponent(final UIComponent c, final String id) {
		if (id.equals(c.getId())) {
			return c;
		}
		Iterator<UIComponent> kids = c.getFacetsAndChildren();
		while (kids.hasNext()) {
			UIComponent found = findComponent(kids.next(), id);
			if (found != null) {
				return found;
			}
		}
		return null;
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
			final ExternalContext externalContext = getExternalContext(); 
			
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
			final ExternalContext externalContext = getExternalContext();			
			
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

    /*
     * Faces Message Helper Methods
     */
    public static void addInfoMessage(final String summary) {
        getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null));
    }

    public static void addInfoMessage(final UIComponent component, final String summary) {
        getFacesContext().addMessage(component.getClientId(getFacesContext()),
                new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null));
    }

    public static void addWarningMessage(final String summary) {
        getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, summary, null));
    }

    public static void addErrorMessageToUiComponent(final UIComponent component, final String summary) {
    	addErrorMessage(component.getClientId(getFacesContext()), summary);
    }
    
    public static void addErrorMessage(final String clientId, final String summary) {
        getFacesContext().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null));
    }
    
    public static void addErrorMessage(final String summary) {
    	addErrorMessage(null, summary);
    }

    public static void addFatalMessage(final String summary) {
        getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, summary, null));
    }

    /**
     * Returns true if the provided clientId has messages in the current
     * FacesContext
     *
     * @param clientId
     * @return
     */
	public static boolean hasMessages(final String clientId) {
		Iterator<String> iterator = getFacesContext().getClientIdsWithMessages();
		while (iterator.hasNext()) {
			String id = iterator.next();
			if (id.equals(clientId)) {
				return true;
			}
		}
		return false;
	}

    /*
     * Faces Object Helpers
     */
    public static FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    public static ExternalContext getExternalContext() {
        FacesContext faces = getFacesContext();
        ExternalContext context = faces.getExternalContext();
        return context;
    }

	public static void doForward(final String url) {
		try {
			FacesContext context = getFacesContext();
			context.getExternalContext().dispatch(url);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Send a client HTTP redirect and halt further Faces Life-cycle processing
	 */
	public static void doRedirect(final String url) {
		try {
			FacesContext context = getFacesContext();
			context.getExternalContext().redirect(url);
			context.responseComplete();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void show404() {
		try {
			FacesContext context = getFacesContext();
			HttpServletResponse response = (HttpServletResponse) context
					.getExternalContext().getResponse();
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			context.responseComplete();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean isCurrentPage(final String pPage) {
		return getRequest().getRequestURI().indexOf(pPage) >= 0;
	}
}

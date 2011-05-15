package com.pferrot.lendity.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import com.pferrot.core.CoreUtils;

/**
 * Some utils methods to manage cookies.
 * 
 * @author pferrot
 *
 */
public class CookieUtils {
	
	/**
	 * Get the value for a given cookie. Note that the value is stored Base64 encoded
	 * in the cookie, but the value returned by this method is decoded already.
	 *
	 * @param pCookieName
	 * @return
	 */
	public static String getCookieValue(final String pCookieName) {
		final Cookie cookie = getCookie(pCookieName);
		if (cookie != null) {
			final String base64EncodedValue = cookie.getValue();
			if (base64EncodedValue != null) {
				return new String(Base64.decodeBase64(base64EncodedValue.getBytes())); 
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}

	/**
	 * Create a unsecure cookie with the maximum life duration.
	 * Note that the value is Base64 encoded in the cookie.
	 *
	 * @param pCookieName
	 * @param pCookieValue
	 */
	public static void createCookie(final String pCookieName, final String pCookieValue) {
		CoreUtils.assertNotNull(pCookieName);
		CoreUtils.assertNotNull(pCookieValue);
		HttpServletResponse httpServletResponse = 
			(HttpServletResponse)JsfUtils.getExternalContext().getResponse();
		final String base64EncodedValue = new String(Base64.encodeBase64(pCookieValue.getBytes()));
		Cookie cookie = new Cookie(pCookieName, base64EncodedValue);   
		cookie.setMaxAge(Integer.MAX_VALUE);
		httpServletResponse.addCookie(cookie);  
	}

	/**
	 * Delete the given cookie.
	 *
	 * @param pCookieName
	 */
	public static void deleteCookie(final String pCookieName) {
		final Cookie cookie = getCookie(pCookieName);
		if (cookie != null) {
			// This deletes the cookie.
			cookie.setMaxAge(0);
			HttpServletResponse httpServletResponse = 
				(HttpServletResponse)JsfUtils.getExternalContext().getResponse();
			httpServletResponse.addCookie(cookie);
			
		}  
	}
	
	/**
	 * Returns the cookie with the specified name or null if
	 * it is not found.
	 *
	 * @param pCookieName
	 * @return
	 */
	public static Cookie getCookie(final String pCookieName) {
		CoreUtils.assertNotNull(pCookieName);
		HttpServletRequest httpServletRequest = 
			(HttpServletRequest)JsfUtils.getExternalContext().getRequest();   
		Cookie[] cookies = httpServletRequest.getCookies();
		if (cookies != null) {
			for(int i=0; i<cookies.length; i++) {
				if (cookies[i].getName().equalsIgnoreCase(pCookieName)) {
					return cookies[i]; 
				}
			}
		}
		return null;
	}

}

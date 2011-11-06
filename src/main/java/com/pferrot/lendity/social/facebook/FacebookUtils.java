package com.pferrot.lendity.social.facebook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.social.facebook.exception.FacebookException;

/**
 * Some utils methods to make interactions with Facebook easier.
 * 
 * @author pferrot
 *
 */
public class FacebookUtils {
	
	private final static Log log = LogFactory.getLog(FacebookUtils.class);
	
	/**
	 * Get the URL for the Facebook user to grant access to its data.
	 * When access is granted, the user is redirected to pRedirectUriNotEncoded. On top of
	 * that, a "code" parameter is appended to that URL. That code is to be used with the
	 * <code>getFacebookAccessToken</code> method.  
	 * See https://developers.facebook.com/docs/authentication/
	 *  
	 * @param pReturnUrlNotEncoded
	 * @return
	 * @throws FacebookException 
	 */
	public static String getFacebookOAuthLink(final String pRedirectUriNotEncoded) throws FacebookException {
		CoreUtils.assertNotNullOrEmptyString(pRedirectUriNotEncoded);
		try {
			final StringBuffer sb = new StringBuffer();
			sb.append("https://www.facebook.com/dialog/oauth?client_id=");
			sb.append(Configuration.getFacebookApplicationId());
			sb.append("&redirect_uri=");
			sb.append(URLEncoder.encode(pRedirectUriNotEncoded, "UTF-8"));
			return sb.toString();
		}
		catch (UnsupportedEncodingException e) {
			throw new FacebookException(e);
		}
	}
	
	/**
	 * Returns the access token to be used when making calls to the Facebook API.
	 * 
	 * @param pCode: see <code>getFacebookOAuthLink</code>
	 * @param pRedirectUriNotEncoded
	 * @return
	 * @throws FacebookException
	 */
	public static String getFacebookAccessToken(final String pCode, final String pRedirectUriNotEncoded) throws FacebookException {
		CoreUtils.assertNotNullOrEmptyString(pCode);
		CoreUtils.assertNotNullOrEmptyString(pRedirectUriNotEncoded);
		
		InputStreamReader is = null;
		BufferedReader br = null;
		try {
			final StringBuffer sb = new StringBuffer();
			sb.append("https://graph.facebook.com/oauth/access_token?client_id=");
			sb.append(Configuration.getFacebookApplicationId());
			sb.append("&redirect_uri=");
			sb.append(URLEncoder.encode(pRedirectUriNotEncoded, "UTF-8"));
			sb.append("&client_secret=");
			sb.append(Configuration.getFacebookSecret());
			sb.append("&code=");
			sb.append(pCode);
			
			URL url = new URL(sb.toString());
			URLConnection connection  = url.openConnection();
			is = new InputStreamReader(connection.getInputStream());
			br = new BufferedReader(is);
	        StringBuffer tmp = new StringBuffer();
	        String inputLine = null;
	        while ((inputLine = br.readLine()) != null) {
	        	tmp.append(inputLine);
	        }
	        // Should be possible to get parameters values differntly...!?
	        final String s = tmp.toString().trim();
	        final String s1 = "access_token=";
	        final int i1 = s.indexOf(s1);
	        final String s2 = "&expires=";
	        final int i2 = s.indexOf(s2);
	        final String accessToken = s.substring(i1 + s1.length(), i2);
	        final String expires = s.substring(i2 + s2.length());
	        	
	        if (log.isDebugEnabled()) {
	        	log.debug("Facebook full input: " + s);
	        	log.debug("Facebook access token: " + accessToken);
	        	log.debug("Facebook expires: " + expires);
	        }
	        return accessToken;
		}
		catch (UnsupportedEncodingException e) {
			throw new FacebookException(e);
		} 
		catch (IOException e) {
			throw new FacebookException(e);
		}
		finally {
			if (br != null) {
				try {
					br.close();
				}
				catch (IOException e) {
					throw new FacebookException(e);
				}
			}
			if (is != null) {
				try {
					is.close();
				} 
				catch (IOException e) {
					throw new FacebookException(e);
				}
			}
		}
	}
	
	/**
	 * Returns the friends list in JSON format.
	 * 
	 * @param pAccessToken
	 * @return
	 * @throws FacebookException
	 */
	public static String getFacebookFriendsListJson(final String pAccessToken) throws FacebookException {
		CoreUtils.assertNotNullOrEmptyString(pAccessToken);
		InputStreamReader is = null;
		BufferedReader br = null;
		try {
			final String friendsUrl = "https://graph.facebook.com/me/friends?access_token=" + pAccessToken;
	        final URL url = new URL(friendsUrl);
			final URLConnection connection  = url.openConnection();
			is = new InputStreamReader(connection.getInputStream());
			br = new BufferedReader(is);
	        final StringBuffer tmp = new StringBuffer();
	        String inputLine = null;
	        while ((inputLine = br.readLine()) != null) {
	        	tmp.append(inputLine);
	        }	        
	        final String jsonResult = tmp.toString().trim();
	        if (log.isDebugEnabled()) {
	        	log.debug("Facebook JSON result: " + jsonResult);
	        }
	        return jsonResult;
		}
		catch (UnsupportedEncodingException e) {
			throw new FacebookException(e);
		} 
		catch (IOException e) {
			throw new FacebookException(e);
		}
		finally {
			if (br != null) {
				try {
					br.close();
				}
				catch (IOException e) {
					throw new FacebookException(e);
				}
			}
			if (is != null) {
				try {
					is.close();
				} 
				catch (IOException e) {
					throw new FacebookException(e);
				}
			}
		}
	}

}

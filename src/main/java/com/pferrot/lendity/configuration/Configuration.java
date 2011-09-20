package com.pferrot.lendity.configuration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

/**
 * Values are set from applicationContext.xml.
 * As I wanted to be able to access that bean in a static manner rather than
 * wiring it did to every single controller or service where it is needed, I
 * am using a workaround which consists of setting static member variables through
 * instance setters. Indeed, an instance of that bean is created at application
 * startup by Spring.
 * 
 * Probably not best practice, but it fits my needs.
 *
 * @author pferrot
 *
 */
public class Configuration {

	private static String siteName;
	private static String rootURL;
	private static String noReplyEmailAddress;
	private static String noReplySenderName;
	private static String supportEmailAddress;
	private static String contactEmailAddress;
	private static boolean facebookLikeEnabled;
	private static String googleAnalyticsId;
	private static int nbDaysToValidateRegistration;
	private static int minimumAge;
	private static int maxNbCategories;
	private static Set<String> categoriesNotAllowedPublicVisibility = new HashSet<String>();
	private static Set<String> categoriesNotAllowedToRent = new HashSet<String>();

	public static String getRootURL() {
		return rootURL;
	}
	
	public String getRootURLNotStatic() {
		return rootURL;
	}
	
	/**
	 * Returns the root as it should be encoded to appear in the facebook like button. 
	 */
	public String getRootURLFacebookEncodedNotStatic() {
		try {
			return URLEncoder.encode(getRootURLNotStatic(), "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public void setRootURL(String rootURL) {
		this.rootURL = rootURL;
	}

	public static String getSiteName() {
		return siteName;
	}
	
	public String getSiteNameNotStatic() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public static String getNoReplyEmailAddress() {
		return noReplyEmailAddress;
	}

	public void setNoReplyEmailAddress(String noReplyEmailAddress) {
		this.noReplyEmailAddress = noReplyEmailAddress;
	}

	public static String getNoReplySenderName() {
		return noReplySenderName;
	}

	public void setNoReplySenderName(String noReplySenderName) {
		this.noReplySenderName = noReplySenderName;
	}

	public static String getSupportEmailAddress() {
		return supportEmailAddress;
	}
	
	public String getSupportEmailAddressNotStatic() {
		return supportEmailAddress;
	}

	public void setSupportEmailAddress(String supportEmailAddress) {
		this.supportEmailAddress = supportEmailAddress;
	}

	public static String getContactEmailAddress() {
		return contactEmailAddress;
	}
	
	public String getContactEmailAddressNotStatic() {
		return contactEmailAddress;
	}

	public void setContactEmailAddress(String contactEmailAddress) {
		this.contactEmailAddress = contactEmailAddress;
	}

	public static boolean isFacebookLikeEnabled() {
		return facebookLikeEnabled;
	}
	
	public boolean isFacebookLikeEnabledNotStatic() {
		return facebookLikeEnabled;
	}

	public void setFacebookLikeEnabled(boolean facebookLikeEnabled) {
		this.facebookLikeEnabled = facebookLikeEnabled;
	}

	public static String getGoogleAnalyticsId() {
		return googleAnalyticsId;
	}
	
	public String getGoogleAnalyticsIdNotStatic() {
		return googleAnalyticsId;
	}

	public  void setGoogleAnalyticsId(String googleAnalyticsId) {
		this.googleAnalyticsId = googleAnalyticsId;
	}

	public static int getNbDaysToValidateRegistration() {
		return nbDaysToValidateRegistration;
	}

	public void setNbDaysToValidateRegistration(
			int nbDaysToValidateRegistration) {
		this.nbDaysToValidateRegistration = nbDaysToValidateRegistration;
	}

	public static int getMinimumAge() {
		return minimumAge;
	}

	public void setMinimumAge(int minimumAge) {
		this.minimumAge = minimumAge;
	}

	public static int getMaxNbCategories() {
		return maxNbCategories;
	}

	public void setMaxNbCategories(int maxNbCategories) {
		Configuration.maxNbCategories = maxNbCategories;
	}

	public static Set<String> getCategoriesNotAllowedPublicVisibility() {
		return categoriesNotAllowedPublicVisibility;
	}

	public void setCategoriesNotAllowedPublicVisibility(
			Set<String> categoriesNotAllowedPublicVisibility) {
		Configuration.categoriesNotAllowedPublicVisibility = categoriesNotAllowedPublicVisibility;
	}
	
	public void addCategoriesNotAllowedPublicVisibility(final String pValue) {
		Configuration.categoriesNotAllowedPublicVisibility.add(pValue);
	}

	public static Set<String> getCategoriesNotAllowedToRent() {
		return categoriesNotAllowedToRent;
	}
	
	public void addCategoriesNotAllowedToRent(final String pValue) {
		Configuration.categoriesNotAllowedToRent.add(pValue);
	}

	public void setCategoriesNotAllowedToRent(
			Set<String> categoriesNotAllowedToRent) {
		Configuration.categoriesNotAllowedToRent = categoriesNotAllowedToRent;
	}
}

package com.pferrot.lendity.configuration;

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
	private static int nbDaysToValidateRegistration;
	private static int nbInvitationsNewUsers;

	public static String getRootURL() {
		return rootURL;
	}

	public void setRootURL(String rootURL) {
		this.rootURL = rootURL;
	}

	public static String getSiteName() {
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

	public static int getNbDaysToValidateRegistration() {
		return nbDaysToValidateRegistration;
	}

	public void setNbDaysToValidateRegistration(
			int nbDaysToValidateRegistration) {
		this.nbDaysToValidateRegistration = nbDaysToValidateRegistration;
	}

	public static int getNbInvitationsNewUsers() {
		return nbInvitationsNewUsers;
	}

	public void setNbInvitationsNewUsers(
			int nbInvitationsNewUsers) {
		this.nbInvitationsNewUsers = nbInvitationsNewUsers;
	}
}

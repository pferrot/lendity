package com.pferrot.sharedcalendar.configuration;

public class Configuration {

	private static String siteName = "patriceferrot.com";
	private static String rootURL = "http://localhost:8080/shared_calendar";
	private static String noReplyEmailAddress = "no_reply@patriceferrot.com";
	private static String noReplySenderName = "patriceferrot.com";
	private static String supportEmailAddress = "support@patriceferrot.com";
	private static int nbDaysToValidateRegistration = 30;

	public static String getRootURL() {
		return rootURL;
	}

	public static void setRootURL(String rootURL) {
		Configuration.rootURL = rootURL;
	}

	public static String getSiteName() {
		return siteName;
	}

	public static void setSiteName(String siteName) {
		Configuration.siteName = siteName;
	}

	public static String getNoReplyEmailAddress() {
		return noReplyEmailAddress;
	}

	public static void setNoReplyEmailAddress(String noReplyEmailAddress) {
		Configuration.noReplyEmailAddress = noReplyEmailAddress;
	}

	public static String getNoReplySenderName() {
		return noReplySenderName;
	}

	public static void setNoReplySenderName(String noReplySenderName) {
		Configuration.noReplySenderName = noReplySenderName;
	}

	public static String getSupportEmailAddress() {
		return supportEmailAddress;
	}

	public static void setSupportEmailAddress(String supportEmailAddress) {
		Configuration.supportEmailAddress = supportEmailAddress;
	}

	public static int getNbDaysToValidateRegistration() {
		return nbDaysToValidateRegistration;
	}

	public static void setNbDaysToValidateRegistration(
			int nbDaysToValidateRegistration) {
		Configuration.nbDaysToValidateRegistration = nbDaysToValidateRegistration;
	}	
}

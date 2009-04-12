package com.pferrot.sharedcalendar.i18n;

import java.util.ArrayList;
import java.util.Collection;

public class I18nConsts {
	
	public static final String DEFAULT_RESOURCE_BUNDLE = "messages";
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// LANGUAGES - start
	
	public static final String EN_LANGUAGE = "en";
	public static final String FR_LANGUAGE = "fr";
	public static final String DEFAULT_LANGUAGE = FR_LANGUAGE;	
	public static final String NO_TEXT_FOUND = "NO_TEXT_FOUND";
	
	public static final Collection<String> SUPPORTED_LANGUAGES = new ArrayList<String>();
	
	static {
		SUPPORTED_LANGUAGES.add(EN_LANGUAGE);
		SUPPORTED_LANGUAGES.add(FR_LANGUAGE);
	}

	// LANGUAGES - end
	/////////////////////////////////////////////////////////////////////////////////////////////////
}

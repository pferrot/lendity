package com.pferrot.lendity.i18n;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;


public class I18nUtils {
	
	private final static Log log = LogFactory.getLog(I18nUtils.class);
	
	private final static Locale SWISS_FRENCH_LOCALE = new Locale("fr", "CH");
	
	private static ClassLoader getCurrentClassLoader(Object defaultObject){
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if(loader == null){
			loader = defaultObject.getClass().getClassLoader();
		}		
		return loader;
	}

	public static String getMessageResourceString(final String bundleName, final String key, 
												  final Object params[], final Locale locale){		
		String text = null;
		
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale, getCurrentClassLoader(params));
		
		try{
			text = bundle.getString(key);
		} catch(MissingResourceException e){
			text = "??? " + key + " ???";
		}
		
		if(params != null){
			MessageFormat mf = new MessageFormat(text, locale);
			text = mf.format(params, new StringBuffer(), null).toString();
		}
		
		return text;
	}
	
	public static String getMessageResourceString(final String key, final Object params[], final Locale locale) {	
		return getMessageResourceString(I18nConsts.DEFAULT_RESOURCE_BUNDLE, key, params, locale);
	}
	
	public static String getMessageResourceString(final String key, final Locale locale) {	
		return getMessageResourceString(I18nConsts.DEFAULT_RESOURCE_BUNDLE, key, null, locale);
	}
	
	/**
	 * From a map containing several languages, returns the one for the
	 * current locale.
	 * 
	 * @param textMap
	 * @return
	 */
	public static String getLocalizedText(final Map<String, String> pTextMap) {
		CoreUtils.assertNotNull(pTextMap);
		String lang = getLocalAsString();
		String result = null;
		
		result = pTextMap.get(lang);
		// If not found, try the default language (if it was not it already).
		if (result == null && !I18nConsts.DEFAULT_LANGUAGE.equals(lang)) {
			result = pTextMap.get(I18nConsts.DEFAULT_LANGUAGE);
		}
		
		// If still not found, try any language.
		if (result == null) {
			Collection<String> texts = pTextMap.values();
			if (texts != null && !texts.isEmpty()) {
				for (String text: texts) {
					if (text != null) {
						result = text;
						break;
					}
				}
			}
		}
		
		// No text in any language.
		if (result == null) {
			result = I18nConsts.NO_TEXT_FOUND;
		}
		
		return result;		
	}
	
	/**
	 * Returns the current locale in 2 characters format ('fr' or 'en' for instance).
	 * 
	 * @return
	 */
	public static String getLocalAsString() {
		final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		String lang = null;
		if (locale == null) {
			lang = I18nConsts.DEFAULT_LANGUAGE;
		}
		else {
			lang = locale.getLanguage();
			if (lang == null) {
				lang = I18nConsts.DEFAULT_LANGUAGE;
			}
			else {
				if (lang.length() > 2) {
					lang = lang.substring(0, 2);
				}
				// Check that it is a supported locale.
				if (!I18nConsts.SUPPORTED_LANGUAGES.contains(lang)) {
					if (log.isDebugEnabled()) {
						log.debug("Language not supported : '" + lang + "', using default language.");
					}
					lang = I18nConsts.DEFAULT_LANGUAGE;
				}
			}
		}
		return lang;
	}

	public static Locale getSwissFrenchLocale() {
		return SWISS_FRENCH_LOCALE;
	}
	
	public static Locale getDefaultLocale() {
		return SWISS_FRENCH_LOCALE;
	}
	
	

}

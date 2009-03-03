package com.pferrot.sharedcalendar.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18nUtils {
	
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
	
	

}

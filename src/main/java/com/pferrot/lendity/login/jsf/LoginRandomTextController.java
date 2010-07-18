package com.pferrot.lendity.login.jsf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;

import com.pferrot.lendity.i18n.I18nUtils;

public class LoginRandomTextController {
	
	private static final int NB_BACKGROUND_TEXTS = 3;
	private static final String KEY_PREFIX = "login_backgroundText";
	
	private static List<Integer> getShufflList() {
		final List<Integer> result = new ArrayList<Integer>();
		
		for (int i = 1; i < NB_BACKGROUND_TEXTS + 1; i++) {
			result.add(Integer.valueOf(i));
		}
		
		Collections.shuffle(result);
		
		return result;
	}
	
	public String getTextInRandomOrder() {
		final List<Integer> nbInRandomOrder = getShufflList();
		final StringBuffer text = new StringBuffer();
		for (Integer textNb: nbInRandomOrder) {
			text.append(getText(textNb.intValue()));
			text.append("<br/><br/><br/>");
			text.append("<center>---------</center>");
			text.append("<br/><br/>");
		}
		return text.toString();
	}
	
	private String getText(final int pTextNumber) {
		final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		return I18nUtils.getMessageResourceString(KEY_PREFIX + pTextNumber, locale);
	}

}

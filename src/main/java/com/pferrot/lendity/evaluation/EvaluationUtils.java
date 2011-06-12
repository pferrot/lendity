package com.pferrot.lendity.evaluation;

import java.util.Locale;

import com.pferrot.lendity.i18n.I18nUtils;

public class EvaluationUtils {

	public static String getEvaluationLabel(final Integer pScore) {
		if (pScore != null) {
			if (pScore.intValue() == 1) {
				final Locale locale = I18nUtils.getDefaultLocale();
				return I18nUtils.getMessageResourceString("evaluation_score1", locale);
			}
			else if (pScore.intValue() == 2) {
				final Locale locale = I18nUtils.getDefaultLocale();
				return I18nUtils.getMessageResourceString("evaluation_score2", locale);
			}
		}
		return "";
	}
}

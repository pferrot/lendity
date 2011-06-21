package com.pferrot.lendity.evaluation.jsf;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Evaluation;
import com.pferrot.lendity.person.PersonUtils;

public class MyEvaluationsMadeListController extends PersonEvaluationsMadeListController {

	private final static Log log = LogFactory.getLog(MyEvaluationsMadeListController.class);	
	
	@Override
	public Long getPersonId() {
		return PersonUtils.getCurrentPersonId();
	}
	
	@Override
	public String getPersonDisplayName() {
		return getPersonService().findPersonDisplayName(PersonUtils.getCurrentPersonId());
	}
	
	@Override
	public String getEvaluationTitleLabel() {
		final Evaluation eval = (Evaluation)getTable().getRowData();
		final Locale locale = I18nUtils.getDefaultLocale();
		final String s = I18nUtils.getMessageResourceString("evaluation_for", locale);
		return eval.getLendTransaction().getTitle() + " " + s + " " + eval.getEvaluated().getDisplayName();
	}
}

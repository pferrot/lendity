package com.pferrot.lendity.evaluation.jsf;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Evaluation;
import com.pferrot.lendity.person.PersonUtils;

public class MyEvaluationsListController extends PersonEvaluationsListController {

	private final static Log log = LogFactory.getLog(MyEvaluationsListController.class);	
	
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
		final String s = I18nUtils.getMessageResourceString("evaluation_by", locale);
		return eval.getLendTransaction().getTitle() + " " + s + " " + eval.getEvaluator().getDisplayName();
	}
}

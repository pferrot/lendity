package com.pferrot.lendity.evaluation.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
}

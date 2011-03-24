package com.pferrot.lendity.evaluation.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
}

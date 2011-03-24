package com.pferrot.lendity.evaluation.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/public/evaluation/personEvaluationsMadeList.jspx"})
public class PersonEvaluationsMadeListController extends AbstractEvaluationsListController {

	private final static Log log = LogFactory.getLog(PersonEvaluationsMadeListController.class);
	
	
	@InitView
	public void initView() {
		final String personIdString = JsfUtils.getRequestParameter(PagesURL.PERSON_EVALUATIONS_MADE_LIST_PARAM_PERSON_ID);
		if (!StringUtils.isNullOrEmpty(personIdString)) {
			resetFilters();
			setPersonId(Long.valueOf(personIdString));
			setPersonDisplayName(getPersonService().findPersonDisplayName(getPersonId()));
		}
	}
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getEvaluationService().findPersonEvaluationsMade(getPersonId(), getFirstRow(), getRowsPerPage());
	}
}

package com.pferrot.lendity.evaluation.jsf;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.evaluation.EvaluationService;
import com.pferrot.lendity.evaluation.EvaluationUtils;
import com.pferrot.lendity.jsf.list.AbstractListController;
import com.pferrot.lendity.model.Evaluation;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;


public abstract class AbstractEvaluationsListController extends AbstractListController {
	
	private final static Log log = LogFactory.getLog(AbstractEvaluationsListController.class);
	
	private EvaluationService evaluationService;
	private PersonService personService;
	
	private Long personId;
	private String personDisplayName;

	public EvaluationService getEvaluationService() {
		return evaluationService;
	}

	public void setEvaluationService(EvaluationService evaluationService) {
		this.evaluationService = evaluationService;
	}

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public String getPersonDisplayName() {
		return personDisplayName;
	}

	public void setPersonDisplayName(String personDisplayName) {
		this.personDisplayName = personDisplayName;
	}
	
	public String getEvaluationOverviewHref() {
		final Evaluation eval = (Evaluation)getTable().getRowData();
		return JsfUtils.getFullUrl(PagesURL.EVALUATION_OVERVIEW, PagesURL.EVALUATION_OVERVIEW_PARAM_EVALUATION_ID, eval.getId().toString());
	}
	
	public String getEvaluationLabel() {
		final Evaluation eval = (Evaluation)getTable().getRowData();
		return EvaluationUtils.getEvaluationLabel(eval.getScore());
	}
	
	public String getCreationDateLabel() {
		final Evaluation eval = (Evaluation)getTable().getRowData();
		return UiUtils.getDateAsString(eval.getCreationDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}
	
	public abstract String getEvaluationTitleLabel();
}

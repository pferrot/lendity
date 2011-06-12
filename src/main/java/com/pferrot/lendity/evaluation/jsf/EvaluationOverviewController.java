package com.pferrot.lendity.evaluation.jsf;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.evaluation.EvaluationService;
import com.pferrot.lendity.evaluation.EvaluationUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.lendtransaction.LendTransactionService;
import com.pferrot.lendity.model.Evaluation;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.HtmlUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/evaluation/evaluationOverview.jspx"})
public class EvaluationOverviewController {
	
	private final static Log log = LogFactory.getLog(EvaluationOverviewController.class);
	
	private EvaluationService evaluationService;
	private LendTransactionService lendTransactionService;
	
	private Evaluation evaluation;
	private Long evaluationId;
	
	@InitView
	public void initView() {
		final String evaluationIdString = JsfUtils.getRequestParameter(PagesURL.EVALUATION_OVERVIEW_PARAM_EVALUATION_ID);
		Evaluation eval = null;
		if (evaluationIdString != null) {
			setEvaluationId(Long.parseLong(evaluationIdString));
			eval = getEvaluationService().findEvaluation(getEvaluationId());
			// Access control check.
			if (!getEvaluationService().isCurrentUserAuthorizedToView(eval)) {
				JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
				if (log.isWarnEnabled()) {
					log.warn("Access denied (evaluation view): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), evaluation = " + getEvaluationId());
				}
				return;
			}
			setEvaluation(eval);
		}	
	}

	public EvaluationService getEvaluationService() {
		return evaluationService;
	}

	public void setEvaluationService(EvaluationService evaluationService) {
		this.evaluationService = evaluationService;
	}

	public LendTransactionService getLendTransactionService() {
		return lendTransactionService;
	}

	public void setLendTransactionService(
			LendTransactionService lendTransactionService) {
		this.lendTransactionService = lendTransactionService;
	}

	public Evaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(Evaluation evaluation) {
		this.evaluation = evaluation;
	}

	public Long getEvaluationId() {
		return evaluationId;
	}

	public void setEvaluationId(Long evaluationId) {
		this.evaluationId = evaluationId;
	}
	
	public String getText() {
		final String text = getEvaluation().getText();
		if (text != null) {
			return HtmlUtils.escapeHtmlAndReplaceCr(text);
		}
		return "";
	}
	
	public String getEvaluationScoreLabel() {
		final Integer score = getEvaluation().getScore();
		return EvaluationUtils.getEvaluationLabel(score);
	}
}

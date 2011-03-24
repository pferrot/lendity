package com.pferrot.lendity.evaluation.jsf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.evaluation.EvaluationConsts;
import com.pferrot.lendity.evaluation.EvaluationService;
import com.pferrot.lendity.evaluation.exception.EvaluationException;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.lendtransaction.LendTransactionService;
import com.pferrot.lendity.model.Evaluation;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

@ViewController(viewIds={"/auth/evaluation/evaluationAdd.jspx"})
public class EvaluationAddController {
	
	private final static Log log = LogFactory.getLog(EvaluationAddController.class);
	
	private EvaluationService evaluationService;
	private LendTransactionService lendTransactionService;
	
	private String text;
	private Long score;
	private List<SelectItem> scoreSelectItems;
	private LendTransaction lendTransaction;
	
	@InitView
	public void initView() {
		try {
			final String lendTransactionIdString = JsfUtils.getRequestParameter(PagesURL.EVALUATION_ADD_PARAM_LEND_TRANSACTION_ID);
			if (lendTransactionIdString != null) {
				final LendTransaction lt = getLendTransactionService().findLendTransaction(Long.valueOf(lendTransactionIdString));
				getEvaluationService().assertIsEvaluationAsLenderOrBorrowerAuthorized(PersonUtils.getCurrentPersonId(), lt);
				setLendTransaction(lt);
			}				
		}
		catch (Exception e) {
			JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
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

	public LendTransaction getLendTransaction() {
		return lendTransaction;
	}

	public void setLendTransaction(LendTransaction lendTransaction) {
		this.lendTransaction = lendTransaction;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = StringUtils.getNullIfEmpty(text);
	}

	public Long getScore() {
		return score;
	}

	public void setScore(Long score) {
		this.score = UiUtils.getPositiveLongOrNull(score);
	}

	public List<SelectItem> getScoreSelectItems() {
		if (scoreSelectItems == null) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			
			scoreSelectItems = new ArrayList<SelectItem>();
			
			scoreSelectItems.add(UiUtils.getPleaseSelectSelectItem(locale));
			scoreSelectItems.add(new SelectItem(Long.valueOf(1), I18nUtils.getMessageResourceString("evaluation_score1", locale)));
			scoreSelectItems.add(new SelectItem(Long.valueOf(2), I18nUtils.getMessageResourceString("evaluation_score2", locale)));
			scoreSelectItems.add(new SelectItem(Long.valueOf(3), I18nUtils.getMessageResourceString("evaluation_score3", locale)));
		}		
		return scoreSelectItems;	
	}

	public String submit() {
		try {
			final Evaluation eval = new Evaluation();
			eval.setCreationDate(new Date());
			eval.setScore(Integer.valueOf(getScore().intValue()));
			eval.setText(getText());
			eval.setLendTransaction(getLendTransaction());		
			
			Long newEvalId = null;
			if (getEvaluationService().isEvaluationAsBorrowerAuthorized(PersonUtils.getCurrentPersonId(), getLendTransaction())) {
				eval.setEvaluator(getLendTransaction().getBorrower());
				eval.setEvaluated(getLendTransaction().getLender());
				
				newEvalId = getEvaluationService().createEvaluationByBorrower(eval, getLendTransaction());
			}
			else if (getEvaluationService().isEvaluationAsLenderAuthorized(PersonUtils.getCurrentPersonId(), getLendTransaction())) {
				eval.setEvaluator(getLendTransaction().getLender());
				eval.setEvaluated(getLendTransaction().getBorrower());
				
				newEvalId = getEvaluationService().createEvaluationByLender(eval, getLendTransaction());
			}
			else {
				throw new RuntimeException("Evaluation not allowed");
			}
			
			
			JsfUtils.redirect(PagesURL.EVALUATION_OVERVIEW, PagesURL.EVALUATION_OVERVIEW_PARAM_EVALUATION_ID, newEvalId.toString());
		
			// As a redirect is used, this is actually useless.
			return null;
		}
		catch (EvaluationException e) {
			throw new RuntimeException(e);
		}
	}

	public void validateTextSize(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		String text = (String) value;
		if (text != null && text.length() > EvaluationConsts.MAX_TEXT_SIZE) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			message = I18nUtils.getMessageResourceString("validation_maxSizeExceeded", new Object[]{String.valueOf(EvaluationConsts.MAX_TEXT_SIZE)}, locale);
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
	}
	

}

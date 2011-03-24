package com.pferrot.lendity.evaluation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.dao.EvaluationDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.dao.bean.EvaluationDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.evaluation.exception.EvaluationException;
import com.pferrot.lendity.lendtransaction.LendTransactionService;
import com.pferrot.lendity.lendtransaction.LendTransactionWithCommentService;
import com.pferrot.lendity.lendtransaction.exception.LendTransactionException;
import com.pferrot.lendity.model.Evaluation;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.model.LendTransactionStatus;
import com.pferrot.lendity.model.Objekt;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.security.SecurityUtils;

public class EvaluationService {

	private final static Log log = LogFactory.getLog(EvaluationService.class);
	
	private EvaluationDao evaluationDao;
	private ListValueDao listValueDao;
	private LendTransactionWithCommentService lendTransactionWithCommentService;
	private LendTransactionService lendTransactionService;
	private PersonService personService;

	public EvaluationDao getEvaluationDao() {
		return evaluationDao;
	}

	public void setEvaluationDao(EvaluationDao evaluationDao) {
		this.evaluationDao = evaluationDao;
	}
	
	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public ListValueDao getListValueDao() {
		return listValueDao;
	}

	public void setListValueDao(ListValueDao listValueDao) {
		this.listValueDao = listValueDao;
	}

	public LendTransactionWithCommentService getLendTransactionWithCommentService() {
		return lendTransactionWithCommentService;
	}

	public void setLendTransactionWithCommentService(
			LendTransactionWithCommentService lendTransactionWithCommentService) {
		this.lendTransactionWithCommentService = lendTransactionWithCommentService;
	}

	public LendTransactionService getLendTransactionService() {
		return lendTransactionService;
	}

	public void setLendTransactionService(
			LendTransactionService lendTransactionService) {
		this.lendTransactionService = lendTransactionService;
	}

	public Long createEvaluationByBorrower(final Evaluation pEvaluation, final LendTransaction pLendTransaction) throws EvaluationException {
		try {
			final Long evaluationID = getEvaluationDao().createEvaluation(pEvaluation);
			personService.updatePersonAddEvaluation(pLendTransaction.getLender(), pEvaluation);
			getLendTransactionWithCommentService().updateSetEvaluationByBorrower(pLendTransaction, pEvaluation);
			
			return evaluationID;
		}
		catch (LendTransactionException e) {
			throw new EvaluationException(e);
		}
	}

	public Long createEvaluationByLender(final Evaluation pEvaluation, final LendTransaction pLendTransaction) throws EvaluationException {
		try {
			final Long evaluationID = getEvaluationDao().createEvaluation(pEvaluation);
			personService.updatePersonAddEvaluation(pLendTransaction.getBorrower(), pEvaluation);
			getLendTransactionWithCommentService().updateSetEvaluationByLender(pLendTransaction, pEvaluation);
			
			return evaluationID;
		}
		catch (LendTransactionException e) {
			throw new EvaluationException(e);
		}
	}
	
	public void updateEvaluation(final Evaluation pEvaluation) {
		getEvaluationDao().updateEvaluation(pEvaluation);
	}
	
	public void deleteEvaluation(final Evaluation pEvaluation) {
		getEvaluationDao().deleteEvaluation(pEvaluation);
	}

	public Evaluation findEvaluation(final Long pEvaluationId) {
		return getEvaluationDao().findEvaluation(pEvaluationId);
	}
	
	public ListWithRowCount findPersonEvaluations(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		
		final EvaluationDaoQueryBean queryBean = new EvaluationDaoQueryBean();
		queryBean.setFirstResult(pFirstResult);
		queryBean.setMaxResults(pMaxResults);		
		queryBean.setEvaluatedId(pPersonId);
		
		return getEvaluationDao().findEvaluations(queryBean);
	}

	public long countPersonEvaluations(final Long pPersonId) {
		CoreUtils.assertNotNull(pPersonId);
		
		final EvaluationDaoQueryBean queryBean = new EvaluationDaoQueryBean();
		queryBean.setEvaluatedId(pPersonId);
		
		return getEvaluationDao().countEvaluations(queryBean);
	}

	public ListWithRowCount findPersonEvaluationsMade(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		
		final EvaluationDaoQueryBean queryBean = new EvaluationDaoQueryBean();
		queryBean.setFirstResult(pFirstResult);
		queryBean.setMaxResults(pMaxResults);		
		queryBean.setEvaluatorId(pPersonId);
		
		return getEvaluationDao().findEvaluations(queryBean);
	}

	public long countPersonEvaluationsMade(final Long pPersonId) {
		CoreUtils.assertNotNull(pPersonId);
		
		final EvaluationDaoQueryBean queryBean = new EvaluationDaoQueryBean();		
		queryBean.setEvaluatorId(pPersonId);
		
		return getEvaluationDao().countEvaluations(queryBean);		
	}

	/////////////////////////////////////////////////////////
	// Access control
	
	public boolean isEvaluationAsLenderAuthorized(final Long pPersonId, final LendTransaction pLendTransaction) {
		CoreUtils.assertNotNull(pPersonId);
		CoreUtils.assertNotNull(pLendTransaction);
		
		return pPersonId.equals(pLendTransaction.getLender().getId()) &&
			pLendTransaction.getStatus().getLabelCode().equals(LendTransactionStatus.COMPLETED_LABEL_CODE) &&
			pLendTransaction.getEvaluationByLender() == null; 
	}

	public boolean isEvaluationAsBorrowerAuthorized(final Long pPersonId, final LendTransaction pLendTransaction) {
		CoreUtils.assertNotNull(pPersonId);
		CoreUtils.assertNotNull(pLendTransaction);
		
		return pPersonId.equals(pLendTransaction.getBorrower().getId()) &&
			pLendTransaction.getStatus().getLabelCode().equals(LendTransactionStatus.COMPLETED_LABEL_CODE) &&
			pLendTransaction.getEvaluationByBorrower() == null; 
	}
	
	public void assertIsEvaluationAsLenderAuthorized(final Long pPersonId, final LendTransaction pLendTransaction) {
		if (!isEvaluationAsLenderAuthorized(pPersonId, pLendTransaction)) {
			throw new SecurityException("Person " + pPersonId + " is not authorized to evaluate lend transaction " + pLendTransaction.getId());
		}
	}

	public void assertIsEvaluationAsBorrowerAuthorized(final Long pPersonId, final LendTransaction pLendTransaction) {
		if (!isEvaluationAsBorrowerAuthorized(pPersonId, pLendTransaction)) {
			throw new SecurityException("Person " + pPersonId + " is not authorized to evaluate lend transaction " + pLendTransaction.getId());
		}
	}

	public void assertIsEvaluationAsLenderOrBorrowerAuthorized(final Long pPersonId, final LendTransaction pLendTransaction) {
		if (! (isEvaluationAsLenderAuthorized(pPersonId, pLendTransaction) ||
			   isEvaluationAsBorrowerAuthorized(pPersonId, pLendTransaction))) {
			throw new SecurityException("Person " + pPersonId + " is not authorized to evaluate lend transaction " + pLendTransaction.getId());
		}
	}
	
	public boolean isUserAuthorizedToEdit(final Person pPerson, final Evaluation pEvaluation) {
		return false;
	}

	public boolean isUserAuthorizedToView(final Person pPerson, final Evaluation pEvaluation) {
		CoreUtils.assertNotNull(pEvaluation);
		if (!SecurityUtils.isLoggedIn() || pPerson == null) {
			return false;
		}
		else {
			if (isUserAuthorizedToEdit(pPerson, pEvaluation)) {
				return true;
			}
			// Connections can view.
			if (pPerson.equals(pEvaluation.getEvaluator()) || 
				pPerson.equals(pEvaluation.getEvaluated()) ||
				(pPerson.getUser() != null && pPerson.getUser().isAdmin())) {
				return true;
			}
			return false;
		}
	}

	public boolean isCurrentUserAuthorizedToView(final Evaluation pEvaluation) {
		return isUserAuthorizedToView(personService.getCurrentPerson(), pEvaluation);
	}

	
	// Access control
	/////////////////////////////////////////////////////////

	
}

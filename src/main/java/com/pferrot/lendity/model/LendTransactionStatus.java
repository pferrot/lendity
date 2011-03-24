package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("LendTransactionStatus")
@Audited
public class LendTransactionStatus extends OrderedListValue {
	
	// Before the lender accepts or deny the lend request (if any).
	public static final String INITIALIZED_LABEL_CODE = "lendTransaction_statusInitialized";
	// After the lender has accepted the lend request.
	public static final String OPENED_LABEL_CODE = "lendTransaction_statusOpened";
	// When the item is lent.
	public static final String IN_PROGRESS_LABEL_CODE = "lendTransaction_statusInProgress";	
	// Item has been given back, but still need evaluation from both.
//	public static final String WAITING_EVALUATION_FROM_BOTH_LABEL_CODE = "lendTransaction_statusWaitingEvaluationBoth";
	// Item has been given back, only borrower must still evaluate.
//	public static final String WAITING_EVALUATION_FROM_BORROWER_CODE = "lendTransaction_statusWaitingEvaluationBorrower";
	// Item has been given back, only lender must still evaluate
//	public static final String WAITING_EVALUATION_FROM_LENDER_LABEL_CODE = "lendTransaction_statusWaitingEvaluationLender";
	// Everything done.
	public static final String COMPLETED_LABEL_CODE = "lendTransaction_statusCompleted";
	// Transaction canceled. Can happen at any time except when in status
	// WAITING_FEEDBACK_LABEL_CODE or COMPLETED_LABEL_CODE.
	public static final String CANCELED_LABEL_CODE = "lendTransaction_statusCanceled";

	
	public LendTransactionStatus() {
		super();
	}

	public LendTransactionStatus(final String labelCode, final Integer position) {
		super(labelCode, position);
	}
}

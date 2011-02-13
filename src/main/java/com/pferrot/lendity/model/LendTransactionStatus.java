package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("LendTransactionStatus")
@Audited
public class LendTransactionStatus extends OrderedListValue {
	
	public static final String OPENED_LABEL_CODE = "lendTransaction_statusOpened";
	public static final String IN_PROGRESS_LABEL_CODE = "lendTransaction_statusInProgress";
	public static final String CANCELED_LABEL_CODE = "lendTransaction_statusCanceled";
	public static final String COMPLETED_LABEL_CODE = "lendTransaction_statusCompleted";	
	
	public LendTransactionStatus() {
		super();
	}

	public LendTransactionStatus(final String labelCode, final Integer position) {
		super(labelCode, position);
	}
}

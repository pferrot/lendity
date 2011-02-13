package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("LendTransactionComment")
@Audited
public class LendTransactionComment extends Comment {

	@ManyToOne
	@JoinColumn(name = "LEND_TRANSACTION_ID")
	private LendTransaction lendTransaction;

	public LendTransaction getLendTransaction() {
		return lendTransaction;
	}

	public void setLendTransaction(LendTransaction lendTransaction) {
		this.lendTransaction = lendTransaction;
	}

	@Override
	public Commentable getContainer() {
		return getLendTransaction();
	}
}

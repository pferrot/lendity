package com.pferrot.lendity.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;


@Entity
@Table(name = "EVALUATIONS")
@Audited
public class Evaluation implements Serializable {

	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;
	
	@Column(name = "SCORE", nullable = false)
    private Integer score;

	@Column(name = "TEXT", nullable = false, length = 3999)
    private String text;
	
	@OneToOne(targetEntity = Person.class)
	@JoinColumn(name = "EVALUATOR_ID", nullable = false)
	private Person evaluator;
	
	@OneToOne(targetEntity = Person.class)
	@JoinColumn(name = "EVALUATED_ID", nullable = false)
	private Person evaluated;
	
	@OneToOne(targetEntity = LendTransaction.class)
	@JoinColumn(name = "LEND_TRANSACTION_ID", nullable = false)
	private LendTransaction lendTransaction;
	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date creationDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Person getEvaluator() {
		return evaluator;
	}

	public void setEvaluator(Person evaluator) {
		this.evaluator = evaluator;
	}

	public Person getEvaluated() {
		return evaluated;
	}

	public LendTransaction getLendTransaction() {
		return lendTransaction;
	}

	public void setLendTransaction(LendTransaction lendTransaction) {
		this.lendTransaction = lendTransaction;
	}

	public void setEvaluated(Person evaluated) {
		this.evaluated = evaluated;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		// This tests if null at the same time.
		else if (!(obj instanceof Comment)){
			return false;
		}
		else {
			final Comment other = (Comment)obj;
			return id != null && id.equals(other.getId());
		}
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("ID: ");
		sb.append(getId());
		return sb.toString();
	}
}

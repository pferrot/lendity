package com.pferrot.lendity.dao.bean;


public class EvaluationDaoQueryBean {
	
	private Long evaluatorId = null;
	private Long evaluatedId = null;
	private Long evaluatorOrEvaluatedId = null;
	private Long lendTransactionId = null;
	private String orderBy = "creationDate";
	private Boolean orderByAscending = Boolean.FALSE;
	private int firstResult = 0;
	private int maxResults = 0;
	
	public Long getEvaluatorId() {
		return evaluatorId;
	}
	
	public void setEvaluatorId(Long evaluatorId) {
		this.evaluatorId = evaluatorId;
	}
	
	public Long getEvaluatedId() {
		return evaluatedId;
	}
	
	public void setEvaluatedId(Long evaluatedId) {
		this.evaluatedId = evaluatedId;
	}
	
	public Long getEvaluatorOrEvaluatedId() {
		return evaluatorOrEvaluatedId;
	}

	public void setEvaluatorOrEvaluatedId(Long evaluatorOrEvaluatedId) {
		this.evaluatorOrEvaluatedId = evaluatorOrEvaluatedId;
	}

	public Long getLendTransactionId() {
		return lendTransactionId;
	}
	
	public void setLendTransactionId(Long lendTransactionId) {
		this.lendTransactionId = lendTransactionId;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public Boolean getOrderByAscending() {
		return orderByAscending;
	}

	public void setOrderByAscending(Boolean orderByAscending) {
		this.orderByAscending = orderByAscending;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}	
}

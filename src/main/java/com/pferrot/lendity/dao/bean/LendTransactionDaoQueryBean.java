package com.pferrot.lendity.dao.bean;


public class LendTransactionDaoQueryBean {
	
	private Long borrowerId = null;
	private Long lenderId = null;
	private Long borrowerOrLenderId = null;
	private Long[] statusIds = null;
	private Long internalItemId = null;
	private Long lendRequestId;
	private String orderBy = "creationDate";
	private Boolean orderByAscending = null;
	private int firstResult = 0;
	private int maxResults = 0;

	public Long getBorrowerId() {
		return borrowerId;
	}

	public void setBorrowerId(Long borrowerId) {
		this.borrowerId = borrowerId;
	}

	public Long getLenderId() {
		return lenderId;
	}

	public void setLenderId(Long lenderId) {
		this.lenderId = lenderId;
	}

	public Long getBorrowerOrLenderId() {
		return borrowerOrLenderId;
	}

	public void setBorrowerOrLenderId(Long borrowerOrLenderId) {
		this.borrowerOrLenderId = borrowerOrLenderId;
	}

	public Long[] getStatusIds() {
		return statusIds;
	}

	public void setStatusIds(Long[] statusIds) {
		this.statusIds = statusIds;
	}

	public Long getInternalItemId() {
		return internalItemId;
	}

	public void setInternalItemId(Long internalItemId) {
		this.internalItemId = internalItemId;
	}

	public Long getLendRequestId() {
		return lendRequestId;
	}

	public void setLendRequestId(Long lendRequestId) {
		this.lendRequestId = lendRequestId;
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

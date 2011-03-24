package com.pferrot.lendity.dao.bean;

import java.util.Date;


public class LendTransactionDaoQueryBean {
	
	private Long borrowerId = null;
	private Long lenderId = null;
	private Long borrowerOrLenderId = null;
	private Long[] statusIds = null;
	private Long itemId = null;
	private Long lendRequestId;
	private Long toEvaluateByPersonId;
	private Long completedStatusId;
	private String orderBy = "creationDate";
	private Boolean orderByAscending = Boolean.FALSE;
	private Date startDateMin = null;
	private Date startDateMax = null;
	private Date endDateMin = null;
	private Date endDateMax = null;	
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

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Long getLendRequestId() {
		return lendRequestId;
	}

	public void setLendRequestId(Long lendRequestId) {
		this.lendRequestId = lendRequestId;
	}

	public Long getToEvaluateByPersonId() {
		return toEvaluateByPersonId;
	}

	public void setToEvaluateByPersonId(Long toEvaluateByPersonId) {
		this.toEvaluateByPersonId = toEvaluateByPersonId;
	}

	public Long getCompletedStatusId() {
		return completedStatusId;
	}

	public void setCompletedStatusId(Long completedStatusId) {
		this.completedStatusId = completedStatusId;
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

	public Date getStartDateMin() {
		return startDateMin;
	}

	public void setStartDateMin(Date startDateMin) {
		this.startDateMin = startDateMin;
	}

	public Date getStartDateMax() {
		return startDateMax;
	}

	public void setStartDateMax(Date startDateMax) {
		this.startDateMax = startDateMax;
	}

	public Date getEndDateMin() {
		return endDateMin;
	}

	public void setEndDateMin(Date endDateMin) {
		this.endDateMin = endDateMin;
	}

	public Date getEndDateMax() {
		return endDateMax;
	}

	public void setEndDateMax(Date endDateMax) {
		this.endDateMax = endDateMax;
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

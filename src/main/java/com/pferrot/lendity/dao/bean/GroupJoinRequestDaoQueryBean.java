package com.pferrot.lendity.dao.bean;


public class GroupJoinRequestDaoQueryBean {
	
	private Long[] requesterIds;
	private Long[] groupIds;
	private Boolean completed;
	private Long[] responseIds;
	
	private String orderBy = "requestDate";
	private Boolean orderByAscending = Boolean.FALSE;
		
	private int firstResult = 0;
	private int maxResults = 0;
	
	
	public Long[] getRequesterIds() {
		return requesterIds;
	}

	public void setRequesterIds(Long[] requesterIds) {
		this.requesterIds = requesterIds;
	}

	public Long[] getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(Long[] groupIds) {
		this.groupIds = groupIds;
	}

	public Long[] getResponseIds() {
		return responseIds;
	}

	public void setResponseIds(Long[] responseIds) {
		this.responseIds = responseIds;
	}

	public Boolean getCompleted() {
		return completed;
	}
	
	public void setCompleted(Boolean completed) {
		this.completed = completed;
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

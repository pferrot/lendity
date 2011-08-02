package com.pferrot.lendity.dao.bean;


public class PotentialConnectionDaoQueryBean {
	
	private Long personId = null;
	private Boolean personEnabled = null;
	private Boolean connectionEnabled = null;
	private String email = null;
	private String name = null;
	private String source = null;
	private Boolean ignored = null;	
	private Boolean connectionExists = null;
	private Boolean invitationSent = null;
	
	private Boolean alreadyConnected = null;
	private Long[] personConnectionsIds = null; 
	
	private Boolean connectionRequestPending = null;
	private Long[] pendingConnectionRequestConnectionsIds = null;
	
	private Boolean bannedByPerson = null;
	private Long[] bannedByPersonsIds = null;
	
	private String orderBy = "email";
	private Boolean orderByAscending = null;
	private int firstResult = 0;
	private int maxResults = 0;
		
	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public Boolean getPersonEnabled() {
		return personEnabled;
	}

	public void setPersonEnabled(Boolean personEnabled) {
		this.personEnabled = personEnabled;
	}

	public Boolean getConnectionEnabled() {
		return connectionEnabled;
	}

	public void setConnectionEnabled(Boolean connectionEnabled) {
		this.connectionEnabled = connectionEnabled;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Boolean getIgnored() {
		return ignored;
	}

	public void setIgnored(Boolean ignored) {
		this.ignored = ignored;
	}

	public Boolean getConnectionExists() {
		return connectionExists;
	}

	public void setConnectionExists(Boolean connectionExists) {
		this.connectionExists = connectionExists;
	}

	public Boolean getInvitationSent() {
		return invitationSent;
	}

	public void setInvitationSent(Boolean invitationSent) {
		this.invitationSent = invitationSent;
	}

	public Boolean getAlreadyConnected() {
		return alreadyConnected;
	}

	public void setAlreadyConnected(Boolean alreadyConnected) {
		this.alreadyConnected = alreadyConnected;
	}

	public Long[] getPersonConnectionsIds() {
		return personConnectionsIds;
	}

	public void setPersonConnectionsIds(Long[] personConnectionsIds) {
		this.personConnectionsIds = personConnectionsIds;
	}

	public Boolean getConnectionRequestPending() {
		return connectionRequestPending;
	}

	public void setConnectionRequestPending(Boolean connectionRequestPending) {
		this.connectionRequestPending = connectionRequestPending;
	}

	public Long[] getPendingConnectionRequestConnectionsIds() {
		return pendingConnectionRequestConnectionsIds;
	}

	public void setPendingConnectionRequestConnectionsIds(
			Long[] pendingConnectionRequestConnectionsIds) {
		this.pendingConnectionRequestConnectionsIds = pendingConnectionRequestConnectionsIds;
	}

	public Boolean getBannedByPerson() {
		return bannedByPerson;
	}

	public void setBannedByPerson(Boolean bannedByPerson) {
		this.bannedByPerson = bannedByPerson;
	}

	public Long[] getBannedByPersonsIds() {
		return bannedByPersonsIds;
	}

	public void setBannedByPersonsIds(Long[] bannedByPersonsIds) {
		this.bannedByPersonsIds = bannedByPersonsIds;
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

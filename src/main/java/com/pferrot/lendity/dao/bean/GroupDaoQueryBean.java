package com.pferrot.lendity.dao.bean;


public class GroupDaoQueryBean {
	
	private String title = null;
	private String description = null;
	private Long[] ownerIds = null;
	private Long[] administratorsIds = null;
	private Long[] membersIds = null;
	private Long[] ownerOrAdministratorsOrMembersIds = null;
	private Long[] ownerOrAdministratorsIds = null;
	private Long[] bannedPersonsIds = null;
	private Boolean validateMembership = null;
	private Boolean onlyMembersCanSeeComments = null;
	private String orderBy = "title";
	private Boolean orderByAscending = null;
	private int firstResult = 0;
	private int maxResults = 0;
	
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Long[] getOwnerOrAdministratorsOrMembersIds() {
		return ownerOrAdministratorsOrMembersIds;
	}

	public void setOwnerOrAdministratorsOrMembersIds(
			Long[] ownerOrAdministratorsOrMembersIds) {
		this.ownerOrAdministratorsOrMembersIds = ownerOrAdministratorsOrMembersIds;
	}

	public Long[] getOwnerOrAdministratorsIds() {
		return ownerOrAdministratorsIds;
	}

	public void setOwnerOrAdministratorsIds(Long[] ownerOrAdministratorsIds) {
		this.ownerOrAdministratorsIds = ownerOrAdministratorsIds;
	}

	public Long[] getOwnerIds() {
		return ownerIds;
	}
	
	public void setOwnerIds(Long[] ownerIds) {
		this.ownerIds = ownerIds;
	}
	
	public Long[] getAdministratorsIds() {
		return administratorsIds;
	}
	
	public void setAdministratorsIds(Long[] administratorsIds) {
		this.administratorsIds = administratorsIds;
	}
	
	public Long[] getMembersIds() {
		return membersIds;
	}
	
	public void setMembersIds(Long[] membersIds) {
		this.membersIds = membersIds;
	}
	
	public Long[] getBannedPersonsIds() {
		return bannedPersonsIds;
	}
	
	public void setBannedPersonsIds(Long[] bannedPersonsIds) {
		this.bannedPersonsIds = bannedPersonsIds;
	}
	
	public Boolean getValidateMembership() {
		return validateMembership;
	}
	
	public void setValidateMembership(Boolean validateMembership) {
		this.validateMembership = validateMembership;
	}
	
	public Boolean getOnlyMembersCanSeeComments() {
		return onlyMembersCanSeeComments;
	}

	public void setOnlyMembersCanSeeComments(Boolean onlyMembersCanSeeComments) {
		this.onlyMembersCanSeeComments = onlyMembersCanSeeComments;
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

package com.pferrot.lendity.dao.bean;

import java.util.Date;

public abstract class ObjektDaoQueryBean {
	
	private Long[] ownerIds = null;
	private Boolean ownerEnabled = null;
	private String title = null;
	private Long[] categoryIds = null;
	private Long[] visibilityIds = null; 
	private Long[] visibilityIdsToForce = null;
	private Long[] ownerIdsToExcludeForVisibilityIdsToForce = null;
	private Date creationDateMin = null;
	private Double maxDistanceKm = null;
	private Double originLatitude = null;
	private Double originLongitude = null;
	private String orderBy = "title";
	private Boolean orderByAscending = null;
	private int firstResult = 0;
	private int maxResults = 0;
	
	public Long[] getOwnerIds() {
		return ownerIds;
	}
	
	public void setOwnerIds(Long[] ownerIds) {
		this.ownerIds = ownerIds;
	}
	
	public Boolean getOwnerEnabled() {
		return ownerEnabled;
	}
	
	public void setOwnerEnabled(Boolean ownerEnabled) {
		this.ownerEnabled = ownerEnabled;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Long[] getCategoryIds() {
		return categoryIds;
	}
	
	public void setCategoryIds(Long[] categoryIds) {
		this.categoryIds = categoryIds;
	}
	
	public Long[] getVisibilityIds() {
		return visibilityIds;
	}
	
	public void setVisibilityIds(Long[] visibilityIds) {
		this.visibilityIds = visibilityIds;
	}
	
	public Long[] getVisibilityIdsToForce() {
		return visibilityIdsToForce;
	}
	
	public void setVisibilityIdsToForce(Long[] visibilityIdsToForce) {
		this.visibilityIdsToForce = visibilityIdsToForce;
	}
	
	public Long[] getOwnerIdsToExcludeForVisibilityIdsToForce() {
		return ownerIdsToExcludeForVisibilityIdsToForce;
	}
	
	public void setOwnerIdsToExcludeForVisibilityIdsToForce(
			Long[] ownerIdsToExcludeForVisibilityIdsToForce) {
		this.ownerIdsToExcludeForVisibilityIdsToForce = ownerIdsToExcludeForVisibilityIdsToForce;
	}
	
	public Date getCreationDateMin() {
		return creationDateMin;
	}
	
	public void setCreationDateMin(Date creationDateMin) {
		this.creationDateMin = creationDateMin;
	}
	
	public Double getMaxDistanceKm() {
		return maxDistanceKm;
	}
	
	public void setMaxDistanceKm(Double maxDistanceKm) {
		this.maxDistanceKm = maxDistanceKm;
	}
	
	public Double getOriginLatitude() {
		return originLatitude;
	}
	
	public void setOriginLatitude(Double originLatitude) {
		this.originLatitude = originLatitude;
	}
	
	public Double getOriginLongitude() {
		return originLongitude;
	}
	
	public void setOriginLongitude(Double originLongitude) {
		this.originLongitude = originLongitude;
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

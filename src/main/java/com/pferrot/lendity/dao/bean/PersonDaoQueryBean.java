package com.pferrot.lendity.dao.bean;

import java.util.Date;

public class PersonDaoQueryBean {
	
	private Long personId = null;
	private Long personToIgnoreId = null;
	private int connectionLink;
	private String searchString = null;
	// Set to true when a person should be a match only if his
	// full email is the searchString, i.e. when searching for "gmail"
	// Will not return all persons with a gmail address.
	// Also, if this is TRUE, then the search will not happen on the first
	// name and last name fields, but only on display name and full email.
	private Boolean emailExactMatch = null;
	private Boolean enabled = null;
	private Boolean receiveNeedsNotifications = null;
	private Boolean emailSubscriber = null;
	private Date emailSubscriberLastUpdateMax = null;
	private String email = null;
	private String firstName = null;
	private String lastName = null;
	private Double maxDistanceKm = null;
	private Double originLatitude = null;
	private Double originLongitude = null;
	private String orderBy = "displayName";
	private Boolean orderByAscending = Boolean.TRUE;
	private int firstResult = 0;
	private int maxResults = 0;
	
	public Long getPersonId() {
		return personId;
	}
	
	public void setPersonId(Long personId) {
		this.personId = personId;
	}
	
	public Long getPersonToIgnoreId() {
		return personToIgnoreId;
	}

	public void setPersonToIgnoreId(Long personToIgnoreId) {
		this.personToIgnoreId = personToIgnoreId;
	}

	public int getConnectionLink() {
		return connectionLink;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setConnectionLink(int connectionLink) {
		this.connectionLink = connectionLink;
	}
	
	public String getSearchString() {
		return searchString;
	}
	
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	
	public Boolean getEmailExactMatch() {
		return emailExactMatch;
	}
	
	public void setEmailExactMatch(Boolean emailExactMatch) {
		this.emailExactMatch = emailExactMatch;
	}
	
	public Boolean getEnabled() {
		return enabled;
	}
	
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	public Boolean getReceiveNeedsNotifications() {
		return receiveNeedsNotifications;
	}
	
	public void setReceiveNeedsNotifications(Boolean receiveNeedsNotifications) {
		this.receiveNeedsNotifications = receiveNeedsNotifications;
	}
	
	public Boolean getEmailSubscriber() {
		return emailSubscriber;
	}
	
	public void setEmailSubscriber(Boolean emailSubscriber) {
		this.emailSubscriber = emailSubscriber;
	}
	
	public Date getEmailSubscriberLastUpdateMax() {
		return emailSubscriberLastUpdateMax;
	}
	
	public void setEmailSubscriberLastUpdateMax(Date emailSubscriberLastUpdateMax) {
		this.emailSubscriberLastUpdateMax = emailSubscriberLastUpdateMax;
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

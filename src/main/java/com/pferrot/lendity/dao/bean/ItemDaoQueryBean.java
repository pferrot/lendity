package com.pferrot.lendity.dao.bean;


public class ItemDaoQueryBean extends ObjektDaoQueryBean {
	
	private Long[] borrowerIds = null;
	private Boolean borrowerEnabled = null;
	private Boolean borrowed = null;
	private Boolean toGiveForFree = null;
	private Boolean toSell = null;
	private Boolean toRent = null;
	private Boolean toLend = null;
	
	public Long[] getBorrowerIds() {
		return borrowerIds;
	}
	
	public void setBorrowerIds(Long[] borrowerIds) {
		this.borrowerIds = borrowerIds;
	}
	
	public Boolean getBorrowerEnabled() {
		return borrowerEnabled;
	}
	
	public void setBorrowerEnabled(Boolean borrowerEnabled) {
		this.borrowerEnabled = borrowerEnabled;
	}
	
	public Boolean getBorrowed() {
		return borrowed;
	}
	
	public void setBorrowed(Boolean borrowed) {
		this.borrowed = borrowed;
	}

	public Boolean getToGiveForFree() {
		return toGiveForFree;
	}

	public void setToGiveForFree() {
		this.toGiveForFree = Boolean.TRUE;
	}

	public Boolean getToSell() {
		return toSell;
	}

	public void setToSell() {
		this.toSell = Boolean.TRUE;
	}

	public Boolean getToRent() {
		return toRent;
	}

	public void setToRent() {
		this.toRent = Boolean.TRUE;
	}

	public Boolean getToLend() {
		return toLend;
	}

	public void setToLend() {
		this.toLend = Boolean.TRUE;
	}
}

package com.pferrot.lendity.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "LEND_REQUESTS")
public class LendRequest implements Serializable {
	
	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;

	// This is a copy of the item title - especially useful when the item is deleted.
	@Column(name = "TITLE", nullable = false, length = 255)
	private String title;
	
	@ManyToOne(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinColumn(name = "REQUESTER_ID", nullable = false)
	private Person requester;

	// Keep a reference on the owner of the item for performance reason
	// and also because what if someday the system allows selling objects? The owner
	// of the object may not be the same as the one at the time the lend request was made...
	@ManyToOne(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinColumn(name = "OWNER_ID", nullable = false)
	private Person owner;
	
	// Nullable because the item can be deleted.
	@ManyToOne(targetEntity = com.pferrot.lendity.model.Item.class)
	@JoinColumn(name = "ITEM_ID", nullable = true)
	private Item item;
	
	@Column(name = "REQUEST_DATE", nullable = false)
	private Date requestDate;
	
	@Column(name = "RESPONSE_DATE", nullable = true)
	private Date responseDate;
	
	@Column(name = "START_DATE", nullable = true)
	private Date startDate;
	
	@Column(name = "END_DATE", nullable = true)
	private Date endDate;

	@Column(name = "TEXT", nullable = true, length = 3999)
	private String text;
	
	@ManyToOne(targetEntity = com.pferrot.lendity.model.LendRequestResponse.class)
	@JoinColumn(name = "RESPONSE_ID", nullable = true)
	private LendRequestResponse response;
	
	@OneToOne(targetEntity = com.pferrot.lendity.model.LendTransaction.class)
	@JoinColumn(name = "TRANSACTION_ID", nullable = true)
	private LendTransaction transaction;
	
	
    public LendRequest() {
    	super();
    }
	
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Person getRequester() {
		return requester;
	}

	public void setRequester(Person requester) {
		this.requester = requester;
	}

	public Person getOwner() {
		return owner;
	}

	public void setOwner(Person owner) {
		this.owner = owner;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public void setResponse(LendRequestResponse response) {
		this.response = response;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Date getResponseDate() {
		return responseDate;
	}

	public void setResponseDate(Date responseDate) {
		this.responseDate = responseDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LendRequestResponse getResponse() {
		return response;
	}

	public LendTransaction getTransaction() {
		return transaction;
	}

	public void setTransaction(LendTransaction transaction) {
		this.transaction = transaction;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("ID: ");
		sb.append(getId());
		sb.append(", requester: ");
		if (getRequester() != null) {
			sb.append(getRequester().getId());
		} else {
			sb.append("null");
		}
		sb.append(", owner: ");
		if (getOwner() != null) {
			sb.append(getOwner().getId());
		} else {
			sb.append("null");
		}
		sb.append(", item: ");
		if (getItem() != null) {
			sb.append(getItem().getId());
		} else {
			sb.append("null");
		}
		return sb.toString();
	}
}

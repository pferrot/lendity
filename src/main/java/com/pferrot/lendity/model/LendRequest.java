package com.pferrot.lendity.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "LEND_REQUESTS")
public class LendRequest implements Serializable {
	
	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;
	
	@OneToOne(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinColumn(name = "REQUESTER_ID", nullable = false)
	private Person requester;

	// Keep a reference on the owner of the item for performance reason
	// and also because what if someday the system allows selling objects? The owner
	// of the object may not be the same as the one at the time the lend request was made...
	@OneToOne(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinColumn(name = "OWNER_ID", nullable = false)
	private Person owner;
	
	@OneToOne(targetEntity = com.pferrot.lendity.model.InternalItem.class)
	@JoinColumn(name = "ITEM_ID", nullable = false)
	private InternalItem item;
	
	@Column(name = "REQUEST_DATE", nullable = false)
	private Date requestDate;
	
	@Column(name = "RESPONSE_DATE", nullable = true)
	private Date responseDate;
	
	@OneToOne(targetEntity = com.pferrot.lendity.model.LendRequestResponse.class)
	@JoinColumn(name = "RESPONSE_ID", nullable = true)
	private LendRequestResponse response;	
	
    public LendRequest() {
    	super();
    }
	
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
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

	public InternalItem getItem() {
		return item;
	}

	public void setItem(InternalItem item) {
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

	public LendRequestResponse getResponse() {
		return response;
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

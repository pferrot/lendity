package com.pferrot.sharedcalendar.model;

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
@Table(name = "CONNECTION_REQUESTS")
public class ConnectionRequest implements Serializable {
	
	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;
	
	@OneToOne(targetEntity = com.pferrot.sharedcalendar.model.Person.class)
	@JoinColumn(name = "REQUESTER_ID", nullable = false)
	private Person requester;	
	
	@OneToOne(targetEntity = com.pferrot.sharedcalendar.model.Person.class)
	@JoinColumn(name = "CONNECTION_ID", nullable = false)
	private Person connection;
	
	@Column(name = "REQUEST_DATE", nullable = false)
	private Date requestDate;
	
	@Column(name = "RESPONSE_DATE", nullable = true)
	private Date responseDate;
	
	@OneToOne(targetEntity = com.pferrot.sharedcalendar.model.ConnectionRequestResponse.class)
	@JoinColumn(name = "RESPONSE_ID", nullable = true)
	private ConnectionRequestResponse response;	
	
    public ConnectionRequest() {
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

	public Person getConnection() {
		return connection;
	}

	public void setConnection(Person connection) {
		this.connection = connection;
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

	public ConnectionRequestResponse getResponse() {
		return response;
	}

	public void setResponse(ConnectionRequestResponse response) {
		this.response = response;
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
		sb.append(", connection: ");
		if (getConnection() != null) {
			sb.append(getConnection().getId());
		} else {
			sb.append("null");
		}
		return sb.toString();
	}
}

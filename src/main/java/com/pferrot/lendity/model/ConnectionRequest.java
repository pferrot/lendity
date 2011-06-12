package com.pferrot.lendity.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CONNECTION_REQUESTS")
public class ConnectionRequest implements Serializable {
	
	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;
	
	@ManyToOne(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinColumn(name = "REQUESTER_ID", nullable = false)
	private Person requester;	
	
	@ManyToOne(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinColumn(name = "CONNECTION_ID", nullable = false)
	private Person connection;
	
	@Column(name = "TEXT", nullable = true, length = 3999)
	private String text;
	
	@Column(name = "REQUEST_DATE", nullable = false)
	private Date requestDate;
	
	@Column(name = "RESPONSE_DATE", nullable = true)
	private Date responseDate;
	
	@ManyToOne(targetEntity = com.pferrot.lendity.model.ConnectionRequestResponse.class)
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectionRequest other = (ConnectionRequest) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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

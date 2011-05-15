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
@Table(name = "GROUP_MEMBERSHIP_REQUESTS")
public class GroupMembershipRequest implements Serializable {
	
	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;
	
	@OneToOne(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinColumn(name = "REQUESTER_ID", nullable = false)
	private Person requester;	
	
	@OneToOne(targetEntity = com.pferrot.lendity.model.Group.class)
	@JoinColumn(name = "GROUP_ID", nullable = false)
	private Group group;
	
	@Column(name = "REQUEST_DATE", nullable = false)
	private Date requestDate;
	
	@Column(name = "RESPONSE_DATE", nullable = true)
	private Date responseDate;
	
	@OneToOne(targetEntity = com.pferrot.lendity.model.GroupMembershipRequestResponse.class)
	@JoinColumn(name = "RESPONSE_ID", nullable = true)
	private GroupMembershipRequestResponse response;	
	
    public GroupMembershipRequest() {
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

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
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

	public GroupMembershipRequestResponse getResponse() {
		return response;
	}

	public void setResponse(GroupMembershipRequestResponse response) {
		this.response = response;
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
		GroupMembershipRequest other = (GroupMembershipRequest) obj;
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
		sb.append(", group: ");
		if (getGroup() != null) {
			sb.append(getGroup().getId());
		} else {
			sb.append("null");
		}
		return sb.toString();
	}
}

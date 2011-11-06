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
import javax.persistence.Transient;

@Entity
@Table(name = "POTENTIAL_CONNECTIONS")
public class PotentialConnection implements Serializable {
	
	public static String SOURCE_FILE = "FILE";
	public static String SOURCE_TEXTAREA = "TEXTAREA";
	public static String SOURCE_GOOGLE = "GOOGLE";
	public static String SOURCE_FACEBOOK = "FACEBOOK";
	public static String SOURCE_INVITATION = "INVITATION";
	// If someone has been invited, he should be able to easily find
	// who invited him.
	public static String SOURCE_REVERSE_INVITATION = "REVERSE_INVITATION";
	
	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;
	
	@ManyToOne(targetEntity = Person.class)
	@JoinColumn(name = "PERSON_ID", nullable = false)
	private Person person;
	
	@Column(name = "EMAIL", nullable = true, length = 255)
	private String email;
	
	@Column(name = "NAME", nullable = true, length = 255)
	private String name;
	
	@Column(name = "IGNORED", nullable = false)
	private Boolean ignored;
	
	@Column(name = "INVITATION_SEND_DATE", nullable = true)
	private Date invitationSentOn;
	
	@Column(name = "DATE_ADDED", nullable = false)
	private Date dateAdded;
	
	// The connection it corresponds to, if found.
	@ManyToOne(targetEntity = Person.class)
	@JoinColumn(name = "CONNECTION_ID", nullable = true)
	private Person connection;
	
	// The date when the corresponding connection was found, if it was.
	@Column(name = "DATE_FOUND", nullable = true)
	private Date dateFound;
	
	// E.g. "FILE", "GMAIL", ... 
	@Column(name = "SOURCE", nullable = false, length = 20)
	private String source;
	
	@Transient
	private boolean selected = true;
	@Transient
	private Long personId;
	@Transient
	private Long connectionId;
	@Transient
	private Date invitationAlreadySentOn;
	
    public PotentialConnection() {
    	super();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
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

	public Boolean getIgnored() {
		return ignored;
	}

	public void setIgnored(Boolean ignored) {
		this.ignored = ignored;
	}

	public Date getInvitationSentOn() {
		return invitationSentOn;
	}

	public void setInvitationSentOn(Date invitationSentOn) {
		this.invitationSentOn = invitationSentOn;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Person getConnection() {
		return connection;
	}

	public void setConnection(Person connection) {
		this.connection = connection;
	}

	public Date getDateFound() {
		return dateFound;
	}

	public void setDateFound(Date dateFound) {
		this.dateFound = dateFound;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public Long getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(Long connectionId) {
		this.connectionId = connectionId;
	}

	public Date getInvitationAlreadySentOn() {
		return invitationAlreadySentOn;
	}

	public void setInvitationAlreadySentOn(Date invitationAlreadySentOn) {
		this.invitationAlreadySentOn = invitationAlreadySentOn;
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
		PotentialConnection other = (PotentialConnection) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}

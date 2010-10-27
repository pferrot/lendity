package com.pferrot.lendity.model;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.pferrot.core.CoreUtils;
import com.pferrot.security.model.User;

@Entity
@Table(name = "PERSONS")
@Audited
public class Person implements Serializable {

	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;

	@Column(name = "ENABLED", nullable = false)
	private Boolean enabled;

	@OneToOne(targetEntity = Document.class, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JoinColumn(name = "IMAGE_ID", nullable = true)
	@NotAudited
	private Document image;
	
	@OneToOne(targetEntity = Document.class, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JoinColumn(name = "THUMBNAIL_ID", nullable = true)
	@NotAudited
	private Document thumbnail;
	
	@Column(name = "FIRST_NAME", nullable = false, length = 255)
    private String firstName;
	
	@Column(name = "LAST_NAME", nullable = false, length = 255)
    private String lastName;

	@Column(name = "DISPLAY_NAME", nullable = false, length = 255)
    private String displayName;
	
	@Column(name = "EMAIL", unique = true, nullable = false, length = 255)
	private String email;
	
	@Column(name = "PHONE_HOME", length = 100)
	private String phoneHome;
	
	@Column(name = "PHONE_MOBILE", length = 100)
	private String phoneMobile;

	@Column(name = "PHONE_PROFESSIONAL", length = 100)
	private String phoneProfessional;
	
	@OneToOne(targetEntity = com.pferrot.security.model.User.class,
			  cascade = {CascadeType.PERSIST})
	@JoinColumn(name = "USER_ID", nullable = true)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	private User user;
	
	@ManyToOne(targetEntity = com.pferrot.lendity.model.Gender.class)
	@JoinColumn(name = "GENDER_ID", nullable = true)
	private Gender gender;
	
	@ManyToMany(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinTable(
			name = "PERSONS_CONNECTIONS",
			joinColumns = {@JoinColumn(name = "PERSON_ID")},
			inverseJoinColumns = {@JoinColumn(name = "CONNECTION_ID")}
	)
	private Set<Person> connections = new HashSet<Person>();
	
	@ManyToMany(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinTable(
			name = "PERSONS_BANNED",
			joinColumns = {@JoinColumn(name = "PERSON_ID")},
			inverseJoinColumns = {@JoinColumn(name = "BANNED_ID")}
	)
	private Set<Person> bannedPersons = new HashSet<Person>();
	
	@ManyToMany(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinTable(
			name = "PERSONS_BANNED_BY",
			joinColumns = {@JoinColumn(name = "PERSON_ID")},
			inverseJoinColumns = {@JoinColumn(name = "BANNED_BY_ID")}
	)
	private Set<Person> bannedByPersons = new HashSet<Person>();
	
	@Embedded
	private Address address;	
	
	@Column(name = "RECEIVE_NEEDS_NOTIF", nullable = false)
	private Boolean receiveNeedsNotifications;
	
	@Column(name = "EMAIL_SUBSC", nullable = false)
	private Boolean emailSubscriber;
	
	// Receive notifications when comments on own objects are added.
	@Column(name = "RECEIVE_COM_OWN_NOTIF", nullable = false)
	private Boolean receiveCommentsOnOwnNotif;
	
	// Receive notifications when comments on object I commented are added.
	@Column(name = "RECEIVE_COM_COM_NOTIF", nullable = false)
	private Boolean receiveCommentsOnCommentedNotif;
	
	/**
	 * Updated when e-mail is sent OR when there is nothing to
	 * send but the subscriber is "verified".
	 */
	@Column(name = "EMAIL_SUBSC_LAST_UPDATE", nullable = true)
	private Date emailSubscriberLastUpdate;
	
	@Column(name = "NB_INVITATIONS", nullable = false)
    private Integer nbInvitations;

    public Person() {
    	super();
    }

    public Person(Long id, String firstName, String lastName) {
       this.id = id;
       this.firstName = firstName;
       this.lastName = lastName;
    }
   
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public Document getImage() {
		return image;
	}

	public void setImage(Document image) {
		this.image = image;
	}

	public Document getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Document thumbnail) {
		this.thumbnail = thumbnail;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return Boolean.TRUE.equals(getEnabled());
	}

	public String getFirstName() {
        return this.firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        setDisplayName(this.firstName + " " + this.lastName);
    }
    
    public String getLastName() {
        return this.lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
        setDisplayName(this.firstName + " " + this.lastName);
    }

    public String getDisplayName() {
        return this.displayName;
    }

    private void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneHome() {
		return phoneHome;
	}

	public void setPhoneHome(String phoneHome) {
		this.phoneHome = phoneHome;
	}

	public String getPhoneMobile() {
		return phoneMobile;
	}

	public void setPhoneMobile(String phoneMobile) {
		this.phoneMobile = phoneMobile;
	}

	public String getPhoneProfessional() {
		return phoneProfessional;
	}

	public void setPhoneProfessional(String phoneProfessional) {
		this.phoneProfessional = phoneProfessional;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	public Set<Person> getConnections() {
		return connections;
	}

	public void setConnections(final Set<Person> pConnections) {
		this.connections = pConnections;
	}

	public void addConnection(final Person pConnection) {
		CoreUtils.assertNotNull(pConnection);
		connections.add(pConnection);
		pConnection.getConnections().add(this);
	}
		
	public void removeConnection(final Person pConnection) {
		CoreUtils.assertNotNull(pConnection);
		connections.remove(pConnection);
		pConnection.getConnections().remove(this);
	}
	
	public Set<Person> getBannedPersons() {
		return bannedPersons;
	}

	public void setBannedPersons(final Set<Person> pPersons) {
		this.bannedPersons = pPersons;
	}

	public void addBannedPerson(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		bannedPersons.add(pPerson);
		pPerson.getBannedByPersons().add(this);
	}
		
	public void removeBannedPerson(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		bannedPersons.remove(pPerson);
		pPerson.getBannedByPersons().remove(this);
	}
	
	public Set<Person> getBannedByPersons() {
		return bannedByPersons;
	}

	public void setBannedByPersons(final Set<Person> pPersons) {
		this.bannedByPersons = pPersons;
	}

	public void addBannedByPerson(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		bannedByPersons.add(pPerson);
		pPerson.getBannedPersons().add(this);
	}
		
	public void removeBannedByPerson(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		bannedByPersons.remove(pPerson);
		pPerson.getBannedPersons().remove(this);
	}

	public Boolean getReceiveNeedsNotifications() {
		return receiveNeedsNotifications;
	}

	public void setReceiveNeedsNotifications(Boolean receiveNeedsNotifications) {
		this.receiveNeedsNotifications = receiveNeedsNotifications;
	}

	public Boolean getReceiveCommentsOnOwnNotif() {
		return receiveCommentsOnOwnNotif;
	}

	public void setReceiveCommentsOnOwnNotif(Boolean receiveCommentsOnOwnNotif) {
		this.receiveCommentsOnOwnNotif = receiveCommentsOnOwnNotif;
	}

	public Boolean getReceiveCommentsOnCommentedNotif() {
		return receiveCommentsOnCommentedNotif;
	}

	public void setReceiveCommentsOnCommentedNotif(
			Boolean receiveCommentsOnCommentedNotif) {
		this.receiveCommentsOnCommentedNotif = receiveCommentsOnCommentedNotif;
	}

	public Boolean getEmailSubscriber() {
		return emailSubscriber;
	}

	public void setEmailSubscriber(Boolean emailSubscriber) {
		this.emailSubscriber = emailSubscriber;
	}

	public Date getEmailSubscriberLastUpdate() {
		return emailSubscriberLastUpdate;
	}

	public void setEmailSubscriberLastUpdate(Date emailSubscriberLastUpdate) {
		this.emailSubscriberLastUpdate = emailSubscriberLastUpdate;
	}

	public Integer getNbInvitations() {
		return nbInvitations;
	}

	public void setNbInvitations(Integer nbInvitations) {
		this.nbInvitations = nbInvitations;
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
		if (this == obj) {
			return true;
		}
		// This tests if null at the same time.
		else if (!(obj instanceof Person)){
			return false;
		}
		else {
			final Person other = (Person)obj;
			return id != null && id.equals(other.getId());
		}
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("ID: ");
		sb.append(getId());
		sb.append(", email: ");
		sb.append(getEmail());
		return sb.toString();
	}
}



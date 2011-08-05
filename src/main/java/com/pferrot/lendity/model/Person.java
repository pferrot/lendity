package com.pferrot.lendity.model;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.pferrot.core.CoreUtils;
import com.pferrot.security.model.User;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "PERSONS")
@Audited
//@FilterDefs({
//	@FilterDef(name="originLatitudeFilter", parameters=@ParamDef ( name="originLatitude", type="double" )),
//	@FilterDef(name="originLongitudeFilter", parameters=@ParamDef ( name="originLongitude", type="double" ))
//})
public class Person implements Serializable {

	@Id @GeneratedValue
	@Column(name = "ID")
	// Allows accessing the ID even when proxy not loaded.
	@Access(value = AccessType.PROPERTY)
    private Long id;

	@Column(name = "ENABLED", nullable = false)
	private Boolean enabled;

	@OneToOne(targetEntity = Document.class, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JoinColumn(name = "IMAGE_ID", nullable = true)
	@NotAudited
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Document image;
	
	@OneToOne(targetEntity = Document.class, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JoinColumn(name = "THUMBNAIL_ID", nullable = true)
	@NotAudited
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Document thumbnail;
	
	@Column(name = "FIRST_NAME", nullable = false, length = 255)
    private String firstName;
	
	@Column(name = "LAST_NAME", nullable = false, length = 255)
    private String lastName;
	
	@Column(name = "BIRTHDATE", nullable = false)
	private Date birthdate;

	@Column(name = "DISPLAY_NAME", nullable = false, length = 255, unique = true)
    private String displayName;
	
	@Column(name = "EMAIL", unique = true, nullable = false, length = 255)
	private String email;
	
	@Column(name = "WEBSITE", length = 255)
	private String website;
	
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
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private User user;
	
	@ManyToOne(targetEntity = com.pferrot.lendity.model.Gender.class)
	@JoinColumn(name = "GENDER_ID", nullable = true)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Gender gender;
	
	@ManyToMany(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinTable(
			name = "PERSONS_CONNECTIONS",
			joinColumns = {@JoinColumn(name = "PERSON_ID")},
			inverseJoinColumns = {@JoinColumn(name = "CONNECTION_ID")}
	)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<Person> connections = new HashSet<Person>();
	
	@ManyToMany(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinTable(
			name = "PERSONS_BANNED",
			joinColumns = {@JoinColumn(name = "PERSON_ID")},
			inverseJoinColumns = {@JoinColumn(name = "BANNED_ID")}
	)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<Person> bannedPersons = new HashSet<Person>();
	
	@ManyToMany(targetEntity = com.pferrot.lendity.model.Person.class, mappedBy = "bannedPersons")
	private Set<Person> bannedByPersons = new HashSet<Person>();
	
	@ManyToMany(targetEntity = com.pferrot.lendity.model.Group.class, mappedBy = "administrators")
	private Set<Group> groupsAdministrator = new HashSet<Group>();

	@ManyToMany(targetEntity = com.pferrot.lendity.model.Group.class, mappedBy = "members")
	private Set<Group> groupsMember = new HashSet<Group>();

	@ManyToMany(targetEntity = com.pferrot.lendity.model.Group.class, mappedBy = "bannedPersons")
	private Set<Group> groupsBanned = new HashSet<Group>();

	@OneToMany(targetEntity = com.pferrot.lendity.model.Group.class, mappedBy = "owner")
	private Set<Group> groupsOwner = new HashSet<Group>();	
	
	@Column(name = "ADDRESS_HOME", nullable = true, length = 300)
	private String addressHome;
	
	@Column(name = "ADDRESS_HOME_LNG", nullable = true)
	private Double addressHomeLongitude;
	
	@Column(name = "ADDRESS_HOME_LAT", nullable = true)
	private Double addressHomeLatitude;
	
	@Column(name = "RECEIVE_NEEDS_NOTIF", nullable = false)
	private Boolean receiveNeedsNotifications;
	
	@Column(name = "EMAIL_SUBSC", nullable = false)
	private Boolean emailSubscriber;
	
	@Column(name = "RECEIVE_NEWSLETTER", nullable = false)
	private Boolean receiveNewsletter;	
	
	// Receive notifications when comments on own objects are added.
	@Column(name = "RECEIVE_COM_OWN_NOTIF", nullable = false)
	private Boolean receiveCommentsOnOwnNotif;
	
	// Receive notifications when comments on object I commented are added.
	@Column(name = "RECEIVE_COM_COM_NOTIF", nullable = false)
	private Boolean receiveCommentsOnCommentedNotif;
	
	// Receive notifications when comments on groups I administer are added.
	@Column(name = "RECEIVE_COM_GROUPS_ADMIN_NOTIF", nullable = false)
	private Boolean receiveCommentsOnGroupsAdminNotif;
	
	// Receive notifications when comments on groups I am member of are added.
	@Column(name = "RECEIVE_COM_GROUPS_MEMBER_NOTIF", nullable = false)
	private Boolean receiveCommentsOnGroupsMemberNotif;
	
	// Receive notifications when potential connections join Lendity.
	@Column(name = "RECEIVE_POT_CON_NOTIF", nullable = false)
	private Boolean receivePotentialConnectionNotif;
	
	/**
	 * Updated when e-mail is sent OR when there is nothing to
	 * send but the subscriber is "verified".
	 */
	@Column(name = "EMAIL_SUBSC_LAST_UPDATE", nullable = true)
	private Date emailSubscriberLastUpdate;
	
	@ManyToOne(targetEntity = PersonDetailsVisibility.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "DETAILS_VISIBILITY_ID", nullable = false)
	private PersonDetailsVisibility detailsVisibility;
	
	@Column(name = "NB_EVAL_SCORE_1", nullable = false)
	private Integer nbEvalScore1;
	
	@Column(name = "NB_EVAL_SCORE_2", nullable = false)
	private Integer nbEvalScore2;

    public Person() {
    	super();
    }
    
    

//    public Double getDistance() {
//		return distance;
//	}
//
//
//
//	public void setDistance(Double distance) {
//		this.distance = distance;
//	}



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
    }
    
    public String getLastName() {
        return this.lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
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

//	public Address getAddress() {
//		return address;
//	}
//
//	public void setAddress(Address address) {
//		this.address = address;
//	}
	
	public Set<Person> getConnections() {
		return connections;
	}

	public String getAddressHome() {
		return addressHome;
	}

	public void setAddressHome(String addressHome) {
		this.addressHome = addressHome;
	}

	public Double getAddressHomeLongitude() {
		return addressHomeLongitude;
	}
	
	public boolean isAddressHomeDefined() {
		return getAddressHomeLongitude() != null;
	}

	public void setAddressHomeLongitude(Double addressHomeLongitude) {
		this.addressHomeLongitude = addressHomeLongitude;
	}

	public Double getAddressHomeLatitude() {
		return addressHomeLatitude;
	}

	public void setAddressHomeLatitude(Double addressHomeLatitude) {
		this.addressHomeLatitude = addressHomeLatitude;
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
	}
		
	public void removeBannedPerson(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		bannedPersons.remove(pPerson);
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
	}
		
	public void removeBannedByPerson(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		bannedByPersons.remove(pPerson);
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

	public Boolean getReceiveCommentsOnGroupsAdminNotif() {
		return receiveCommentsOnGroupsAdminNotif;
	}

	public void setReceiveCommentsOnGroupsAdminNotif(
			Boolean receiveCommentsOnGroupsAdminNotif) {
		this.receiveCommentsOnGroupsAdminNotif = receiveCommentsOnGroupsAdminNotif;
	}

	public Boolean getReceiveCommentsOnGroupsMemberNotif() {
		return receiveCommentsOnGroupsMemberNotif;
	}

	public void setReceiveCommentsOnGroupsMemberNotif(
			Boolean receiveCommentsOnGroupsMemberNotif) {
		this.receiveCommentsOnGroupsMemberNotif = receiveCommentsOnGroupsMemberNotif;
	}

	public Boolean getReceivePotentialConnectionNotif() {
		return receivePotentialConnectionNotif;
	}
	
	public void setReceivePotentialConnectionNotif(
			Boolean receivePotentialConnectionNotif) {
		this.receivePotentialConnectionNotif = receivePotentialConnectionNotif;
	}

	public Boolean getReceiveNewsletter() {
		return receiveNewsletter;
	}

	public void setReceiveNewsletter(Boolean receiveNewsletter) {
		this.receiveNewsletter = receiveNewsletter;
	}

	public PersonDetailsVisibility getDetailsVisibility() {
		return detailsVisibility;
	}

	public void setDetailsVisibility(PersonDetailsVisibility detailsVisibility) {
		this.detailsVisibility = detailsVisibility;
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

	public Integer getNbEvalScore1() {
		return nbEvalScore1;
	}

	public void setNbEvalScore1(Integer nbEvalScore1) {
		this.nbEvalScore1 = nbEvalScore1;
	}

	public Integer getNbEvalScore2() {
		return nbEvalScore2;
	}

	public void setNbEvalScore2(Integer nbEvalScore2) {
		this.nbEvalScore2 = nbEvalScore2;
	}

	public Set<Group> getGroupsAdministrator() {
		return groupsAdministrator;
	}

	public void setGroupsAdministrator(final Set<Group> pGroup) {
		this.groupsAdministrator = pGroup;
	}

	public void addGroupAdministrator(final Group pGroup) {
		CoreUtils.assertNotNull(pGroup);
		groupsAdministrator.add(pGroup);
	}
		
	public void removeGroupAdministrator(final Group pGroup) {
		CoreUtils.assertNotNull(pGroup);
		groupsAdministrator.remove(pGroup);
	}

	public Set<Group> getGroupsOwner() {
		return groupsOwner;
	}

	public void setGroupsOwner(final Set<Group> pGroup) {
		this.groupsOwner = pGroup;
	}

	public void addGroupOwner(final Group pGroup) {
		CoreUtils.assertNotNull(pGroup);
		groupsOwner.add(pGroup);
	}
		
	public void removeGroupOwner(final Group pGroup) {
		CoreUtils.assertNotNull(pGroup);
		groupsOwner.remove(pGroup);
	}

	public Set<Group> getGroupsMember() {
		return groupsMember;
	}

	public void setGroupsMember(final Set<Group> pGroup) {
		this.groupsMember = pGroup;
	}

	public void addGroupMember(final Group pGroup) {
		CoreUtils.assertNotNull(pGroup);
		groupsMember.add(pGroup);
	}
		
	public void removeGroupMember(final Group pGroup) {
		CoreUtils.assertNotNull(pGroup);
		groupsMember.remove(pGroup);
	}

	public Set<Group> getGroupsBanned() {
		return groupsBanned;
	}

	public void setGroupsBanned(final Set<Group> pGroup) {
		this.groupsBanned = pGroup;
	}

	public void addGroupBanned(final Group pGroup) {
		CoreUtils.assertNotNull(pGroup);
		groupsBanned.add(pGroup);
	}
		
	public void removeGroupBanned(final Group pGroup) {
		CoreUtils.assertNotNull(pGroup);
		groupsBanned.remove(pGroup);
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



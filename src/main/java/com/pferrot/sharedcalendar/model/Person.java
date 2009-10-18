package com.pferrot.sharedcalendar.model;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.envers.Audited;

import com.pferrot.core.CoreUtils;
import com.pferrot.security.model.User;

@Entity
@Table(name = "PERSONS")
public class Person implements Serializable {

	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;
	
	@Column(name = "FIRST_NAME", nullable = false, length = 255)
	@Audited
    private String firstName;
	
	@Column(name = "LAST_NAME", nullable = false, length = 255)
	@Audited
    private String lastName;
	
	@Column(name = "EMAIL", length = 255)
	@Audited
	private String email;
	
	@OneToOne(targetEntity = com.pferrot.security.model.User.class,
			  cascade = {CascadeType.PERSIST})
	@JoinColumn(name = "USER_ID", nullable = true)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	private User user;
	
	@ManyToOne(targetEntity = com.pferrot.sharedcalendar.model.Gender.class)
	@JoinColumn(name = "GENDER_ID", nullable = false)
	private Gender gender;
	
	@ManyToMany(targetEntity = com.pferrot.sharedcalendar.model.PersonSpeciality.class)
	private Set<PersonSpeciality> specialities = new HashSet<PersonSpeciality>();
	
	@ManyToMany(targetEntity = com.pferrot.sharedcalendar.model.Person.class)
	private Set<Person> connections = new HashSet<Person>();
	
	@ManyToMany(targetEntity = com.pferrot.sharedcalendar.model.Person.class)
	private Set<Person> bannedPersons = new HashSet<Person>();
	
	@Embedded
	private Address address;	

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public Set<PersonSpeciality> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(final Set<PersonSpeciality> specialities) {
		this.specialities = specialities;
	}

	public void addSpeciality(final PersonSpeciality pSpeciality) {
		CoreUtils.assertNotNull(pSpeciality);
		specialities.add(pSpeciality);
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
		pPerson.getBannedPersons().add(this);
	}
		
	public void removeBannedPerson(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		bannedPersons.remove(pPerson);
		pPerson.getBannedPersons().remove(this);
	}	
}



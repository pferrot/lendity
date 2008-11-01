package com.pferrot.sharedcalendar.model;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

import com.pferrot.security.model.User;

@Entity
@Table(name = "PERSONS")
public class Person implements Serializable {

	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;
	
	@Column(name = "FIRST_NAME", nullable = false, length = 255)
    private String firstName;
	
	@Column(name = "LAST_NAME", nullable = false, length = 255)
    private String lastName;
	
	@Column(name = "EMAIL", length = 255)
	private String email;
	
	@OneToOne(targetEntity = com.pferrot.security.model.User.class,
			  cascade = {CascadeType.PERSIST})
	@JoinColumn(name = "USER_ID", nullable = false)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	private User user;

    public Person() {
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
}



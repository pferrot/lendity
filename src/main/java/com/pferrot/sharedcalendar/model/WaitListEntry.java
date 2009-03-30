package com.pferrot.sharedcalendar.model;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

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

import com.pferrot.security.model.User;

@Entity
@Table(name = "WAIT_LIST_ENTRIES")
public class WaitListEntry implements Serializable {

	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;
	
	@OneToOne(targetEntity = com.pferrot.security.model.User.class)
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;
	
	@ManyToOne(targetEntity = com.pferrot.sharedcalendar.model.movie.Movie.class)
	@JoinColumn(name = "WAIT_LIST_AWARE_ID", nullable = false)
	private WaitListAware waitListAware;	
	
	@Column(name = "REQUEST_DATE", nullable = false)
	private Date requestDate;
	
	
    public WaitListEntry() {
    	super();
    }
   
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public WaitListAware getWaitListAware() {
		return waitListAware;
	}

	public void setWaitListAware(WaitListAware waitListAware) {
		this.waitListAware = waitListAware;
	}
}



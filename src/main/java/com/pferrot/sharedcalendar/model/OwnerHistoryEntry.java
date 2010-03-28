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

@Entity
@Table(name = "OWNER_HISTORY_ENTRIES")
public class OwnerHistoryEntry implements Serializable {

	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;
	
	@OneToOne(targetEntity = Person.class)
	@JoinColumn(name = "PERSON_ID", nullable = false)
	private Person owner;
	
	@ManyToOne(targetEntity = Item.class)
	@JoinColumn(name = "OWNABLE_ID", nullable = false)
	private Ownable ownable;	
	
	@Column(name = "START_DATE", nullable = false)
	private Date startDate;
	
	@Column(name = "END_DATE")
	private Date endDate;
	
	
    public OwnerHistoryEntry() {
    	super();
    }
   
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

	public Person getOwner() {
		return owner;
	}

	public void setOwner(Person owner) {
		this.owner = owner;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Ownable getOwnable() {
		return ownable;
	}

	public void setOwnable(Ownable ownable) {
		this.ownable = ownable;
	}
}



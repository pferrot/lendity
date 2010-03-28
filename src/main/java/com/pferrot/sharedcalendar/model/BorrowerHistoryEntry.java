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
@Table(name = "BORROWER_HISTORY_ENTRIES")
public class BorrowerHistoryEntry implements Serializable {

	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;
	
	@OneToOne(targetEntity = Person.class)
	@JoinColumn(name = "PERSON_ID", nullable = false)
	private Person borrower;
	
	@ManyToOne(targetEntity = Item.class)
	@JoinColumn(name = "BORROWABLE_ID", nullable = false)
	private Borrowable borrowable;	
	
	@Column(name = "START_DATE", nullable = false)
	private Date startDate;
	
	@Column(name = "END_DATE")
	private Date endDate;
	
	
    public BorrowerHistoryEntry() {
    	super();
    }
   
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

	public Person getBorrower() {
		return borrower;
	}

	public void setBorrower(Person borrower) {
		this.borrower = borrower;
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

	public Borrowable getBorrowable() {
		return borrowable;
	}

	public void setBorrowable(Borrowable borrowable) {
		this.borrowable = borrowable;
	}
}



package com.pferrot.lendity.model;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * This is an item that belongs to a person in the system.
 *
 * @author Patrice
 *
 */
@Entity
@Table(name = "INTERNAL_ITEMS")
public class InternalItem extends Item implements Ownable {
	
	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;

	// Whether the item can be seen by connections or not.
	@Column(name = "VISIBLE", nullable = false)
	private Boolean visible = Boolean.TRUE;

	@OneToOne(targetEntity = Person.class)
	@JoinColumn(name = "OWNER_ID", nullable = false)
	private Person owner;
	
	@Column(name = "NB_REMINDERS_SENT")
    private Integer nbRemindersSent;
	
	@Column(name = "LATEST_REMINDER_DATE")
	private Date latestReminderDate;

    public InternalItem() {
    	super();
    }

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return Boolean.TRUE.equals(getVisible());
	}

	public Person getOwner() {
		return owner;
	}

	public void setOwner(Person owner) {
		this.owner = owner;
	}

	public Integer getNbRemindersSent() {
		return nbRemindersSent;
	}

	public void setNbRemindersSent(Integer nbRemindersSent) {
		this.nbRemindersSent = nbRemindersSent;
	}

	public Date getLatestReminderDate() {
		return latestReminderDate;
	}

	public void setLatestReminderDate(Date latestReminderDate) {
		this.latestReminderDate = latestReminderDate;
	}

	public boolean isAvailable() {
		return !isBorrowed() && isVisible();
	}

	@Override
	public void setLendBack() {
		super.setLendBack();
		setLatestReminderDate(null);
		setNbRemindersSent(null);
	}

	@Override
	public void setBorrowed(Person pBorrower, Date pBorrowDate) {
		super.setBorrowed(pBorrower, pBorrowDate);
		setLatestReminderDate(null);
		setNbRemindersSent(Integer.valueOf(0));
	}

	@Override
	public void setBorrowed(String pBorrowerName, Date pBorrowDate) {
		super.setBorrowed(pBorrowerName, pBorrowDate);
		setLatestReminderDate(null);
		setNbRemindersSent(Integer.valueOf(0));
	}

	/**
	 * Update latestReminderDate and nbRemindersSent.
	 */
	public void reminderSentNow() {
		setLatestReminderDate(new Date());
		if (getNbRemindersSent() == null) {
			setNbRemindersSent(Integer.valueOf(1));
		}
		else {
			setNbRemindersSent(Integer.valueOf(getNbRemindersSent().intValue() + 1));
		}
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
		else if (!(obj instanceof InternalItem)){
			return false;
		}
		else {
			final InternalItem other = (InternalItem)obj;
			return id != null && id.equals(other.getId());
		}
	}
}



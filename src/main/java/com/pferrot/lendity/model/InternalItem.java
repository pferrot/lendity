package com.pferrot.lendity.model;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import com.pferrot.core.CoreUtils;

/**
 * This is an item that belongs to a person in the system.
 *
 * @author Patrice
 *
 */
@Entity
@DiscriminatorValue("Internal")
public class InternalItem extends Item implements Ownable, Commentable {

	// Whether the item can be seen by connections or not.
	@Column(name = "VISIBLE")
	private Boolean visible = Boolean.TRUE;

	@OneToOne(targetEntity = Person.class)
	@JoinColumn(name = "OWNER_ID")
	private Person owner;
	
	@Column(name = "NB_REMINDERS_SENT")
    private Integer nbRemindersSent;
	
	@Column(name = "LATEST_REMINDER_DATE")
	private Date latestReminderDate;
	
	@ManyToMany(targetEntity = com.pferrot.lendity.model.Comment.class)
	@JoinTable(
			name = "ITEMS_COMMENTS",
			joinColumns = {@JoinColumn(name = "ITEM_ID")},
			inverseJoinColumns = {@JoinColumn(name = "COMMENT_ID")}
	)
	private Set<Comment> comments = new HashSet<Comment>();
	
	/**
	 * Persons who get an email when a comment is added.
	 */
	@ManyToMany(targetEntity = com.pferrot.lendity.model.Person.class)
	@JoinTable(
			name = "ITEMS_COMMENTS_RECIPIENTS",
			joinColumns = {@JoinColumn(name = "ITEM_ID")},
			inverseJoinColumns = {@JoinColumn(name = "COMMENT_RECIPIENT_ID")}
	)
	private Set<Person> commentsRecipients = new HashSet<Person>();

    public InternalItem() {
    	super();
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
	
	public Set<Comment> getComments() {
		return comments;
	}

	public void setComments(final Set<Comment> pComments) {
		this.comments = pComments;
	}

	public void addComment(final Comment pComment) {
		CoreUtils.assertNotNull(pComment);
		comments.add(pComment);
	}
		
	public void removeComment(final Comment pComment) {
		CoreUtils.assertNotNull(pComment);
		comments.remove(pComment);
	}

	public Set<Person> getCommentsRecipients() {
		return commentsRecipients;
	}

	public void setCommentsRecipients(final Set<Person> pCommentsRecipients) {
		this.commentsRecipients = pCommentsRecipients;
	}

	public void addCommentRecipient(final Person pCommentRecipient) {
		CoreUtils.assertNotNull(pCommentRecipient);
		commentsRecipients.add(pCommentRecipient);
	}
		
	public void removeCommentRecipient(final Person pCommentRecipient) {
		CoreUtils.assertNotNull(pCommentRecipient);
		commentsRecipients.remove(pCommentRecipient);
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
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
			return getId() != null && getId().equals(other.getId());
		}
	}
}



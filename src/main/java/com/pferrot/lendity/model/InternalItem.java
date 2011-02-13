package com.pferrot.lendity.model;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.envers.Audited;

import com.pferrot.core.CoreUtils;

/**
 * This is an item that belongs to a person in the system.
 *
 * @author Patrice
 *
 */
@Entity
@DiscriminatorValue("Internal")
@Audited
public class InternalItem extends Item implements Ownable, CommentableWithOwner<ItemComment> {
	
	// Information displayed to connections only (if the item is shared accordingly).
	@Column(name = "INFO_CONNECTIONS", nullable = true, length = 3999)
	private String infoConnections;
	
	// Information displayed to non-connections only (if the item is shared accordingly).
	@Column(name = "INFO_PUBLIC", nullable = true, length = 3999)
	private String infoPublic;	
	
	@ManyToOne(targetEntity = ItemVisibility.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "VISIBILITY_ID", nullable = false)
	private ItemVisibility visibility;

	@OneToOne(targetEntity = Person.class)
	@JoinColumn(name = "OWNER_ID")
	private Person owner;
	
	@Column(name = "NB_REMINDERS_SENT")
    private Integer nbRemindersSent;
	
	@Column(name = "LATEST_REMINDER_DATE")
	private Date latestReminderDate;
	
	@OneToMany(targetEntity = com.pferrot.lendity.model.ItemComment.class,
			mappedBy = "item",
			cascade = CascadeType.REMOVE,
			fetch = FetchType.LAZY)
	private Set<ItemComment> comments = new HashSet<ItemComment>();
	
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
	
	@ManyToMany(targetEntity = com.pferrot.lendity.model.Need.class)
	@JoinTable(
			name = "ITEMS_NEEDS",
			joinColumns = {@JoinColumn(name = "ITEM_ID")},
			inverseJoinColumns = {@JoinColumn(name = "NEED_ID")}
	)
	private Set<Need> relatedNeeds = new HashSet<Need>();
	
	@Column(name = "GIVE_FOR_FREE", nullable = false)
	private Boolean toGiveForFree;
	
	@Column(name = "SALE_PRICE", nullable = true)
	private Double salePrice;
	
	@Column(name = "DEPOSIT", nullable = true)
	private Double deposit;
	
	@Column(name = "RENTAL_FEE", nullable = true)
	private Double rentalFee;	

    public InternalItem() {
    	super();
    }

	public String getInfoConnections() {
		return infoConnections;
	}

	public void setInfoConnections(String infoConnections) {
		this.infoConnections = infoConnections;
	}

	public String getInfoPublic() {
		return infoPublic;
	}

	public void setInfoPublic(String infoPublic) {
		this.infoPublic = infoPublic;
	}
    
	public ItemVisibility getVisibility() {
		return visibility;
	}
	
	public void setVisibility(ItemVisibility visibility) {
		this.visibility = visibility;
	}

	public boolean isPublicVisibility() {
		return getVisibility().getLabelCode().equals(ItemVisibility.PUBLIC);
	}

	public boolean isPrivateVisibility() {
		return getVisibility().getLabelCode().equals(ItemVisibility.PRIVATE);
	}
	
	public boolean isConnectionsVisibility() {
		return getVisibility().getLabelCode().equals(ItemVisibility.CONNECTIONS);
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
		return !isBorrowed() && (isConnectionsVisibility() || isPublicVisibility());
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
	
	public Set<ItemComment> getComments() {
		return comments;
	}

	public void setComments(final Set<ItemComment> pComments) {
		this.comments = pComments;
	}

	public void addComment(final ItemComment pComment) {
		CoreUtils.assertNotNull(pComment);
		comments.add(pComment);
	}
		
	public void removeComment(final ItemComment pComment) {
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

	public Set<Need> getRelatedNeeds() {
		return relatedNeeds;
	}

	public void setRelatedNeeds(final Set<Need> pRelatedNeeds) {
		this.relatedNeeds = pRelatedNeeds;
	}

	public void addRelatedNeed(final Need pRelatedNeed) {
		CoreUtils.assertNotNull(pRelatedNeed);
		relatedNeeds.add(pRelatedNeed);
	}
		
	public void removeRelatedNeed(final Need pRelatedNeed) {
		CoreUtils.assertNotNull(pRelatedNeed);
		relatedNeeds.remove(pRelatedNeed);
	}

	public Boolean getToGiveForFree() {
		return toGiveForFree;
	}

	public void setToGiveForFree(Boolean toGiveForFree) {
		this.toGiveForFree = toGiveForFree;
	}

	public Double getDeposit() {
		return deposit;
	}

	public void setDeposit(Double deposit) {
		this.deposit = deposit;
	}

	public Double getRentalFee() {
		return rentalFee;
	}

	public void setRentalFee(Double rentalFee) {
		this.rentalFee = rentalFee;
	}

	public Double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Double salePrice) {
		this.salePrice = salePrice;
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



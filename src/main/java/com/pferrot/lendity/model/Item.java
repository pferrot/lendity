package com.pferrot.lendity.model;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.pferrot.core.CoreUtils;

/**
 * This is an item that belongs to a person in the system.
 *
 * @author Patrice
 *
 */
@Entity
@Table(name = "ITEMS")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Audited
public class Item implements Objekt, Borrowable, CommentableWithOwner<ItemComment> {

	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;
	
	@Column(name = "TITLE", nullable = false, length = 255)
	private String title;

	@Column(name = "DESCRIPTION", nullable = true, length = 3999)
	private String description;
	
	@OneToOne(targetEntity = Document.class, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JoinColumn(name = "IMAGE_1_ID", nullable = true)
	@NotAudited
	private Document image1;
	
	@OneToOne(targetEntity = Document.class, cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JoinColumn(name = "THUMBNAIL_1_ID", nullable = true)
	@NotAudited
	private Document thumbnail1;
	
	@ManyToOne(targetEntity = ItemCategory.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "CATEGORY_ID", nullable = false)
	private ItemCategory category;

	// If the borrower if a user of the system.
	@ManyToOne(targetEntity = Person.class)
	@JoinColumn(name = "BORROWER_ID", nullable = true)
	private Person borrower;

	// If the borrower is not a user of the system.
	@Column(name = "BORROWER_NAME", nullable = true, length = 255)
	private String borrowerName;

	@Column(name = "BORROW_DATE", nullable = true)
	private Date borrowDate;
	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date creationDate;
	
	// When the object is given / sold.
	@Column(name = "ACQUISITION_DATE", nullable = true)
	private Date acquisitionDate;
	
	@ManyToOne(targetEntity = ItemVisibility.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "VISIBILITY_ID", nullable = false)
	private ItemVisibility visibility;

	@ManyToMany(targetEntity = com.pferrot.lendity.model.Group.class)
	@JoinTable(
			name = "ITEMS_GROUPS",
			joinColumns = {@JoinColumn(name = "ITEM_ID")},
			inverseJoinColumns = {@JoinColumn(name = "GROUP_ID")}
	)
	private Set<Group> groupsAuthorized = new HashSet<Group>();

	@ManyToOne(targetEntity = Person.class)
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
	
	@Version
	@Column(name = "OBJ_VERSION")
	private int version;	
	

    public Item() {
    	super();
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

	public Set<Group> getGroupsAuthorized() {
		return groupsAuthorized;
	}

	public void setGroupsAuthorized(final Set<Group> pGroups) {
		this.groupsAuthorized = pGroups;
	}

	public void addGroupAuthorized(final Group pGroup) {
		CoreUtils.assertNotNull(pGroup);
		groupsAuthorized.add(pGroup);
	}
		
	public void removeGroupAuthorized(final Group pGroup) {
		CoreUtils.assertNotNull(pGroup);
		groupsAuthorized.remove(pGroup);
	}
	
	public boolean isToGiveOrSell() {
		return Boolean.TRUE.equals(getToGiveForFree()) ||
			getSalePrice() != null;
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
   	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Document getImage1() {
		return image1;
	}

	public void setImage1(Document image1) {
		this.image1 = image1;
	}

	public Document getThumbnail1() {
		return thumbnail1;
	}

	public void setThumbnail1(Document thumbnail1) {
		this.thumbnail1 = thumbnail1;
	}

	public ItemCategory getCategory() {
		return category;
	}

	public void setCategory(ItemCategory category) {
		this.category = category;
	}

	public Person getBorrower() {
		return borrower;
	}

	public void setBorrower(Person borrower) {
		this.borrower = borrower;
	}

	public String getBorrowerName() {
		return borrowerName;
	}

	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}

	public Date getBorrowDate() {
		return borrowDate;
	}

	public void setBorrowDate(Date borrowDate) {
		this.borrowDate = borrowDate;
	}

	public boolean isBorrowed() {
		return getBorrowDate() != null || getBorrowerName() != null || getBorrower() != null;
	}


	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getAcquisitionDate() {
		return acquisitionDate;
	}

	public void setAcquisitionDate(Date acquisitionDate) {
		this.acquisitionDate = acquisitionDate;
	}

	/**
	 * Reset the lend date and set the borrowerName / borrowerId to null.
	 */
	public void setLendBack() {
		setBorrowDate(null);
		setBorrowerName(null);
		setBorrower(null);
		setLatestReminderDate(null);
		setNbRemindersSent(null);
	}

	/**
	 * 
	 * Set the borrowerName and borrowDate fields and set borrower to null.
	 * 
	 * @param pBorrowerName
	 * @param pBorrowDate
	 */
	public void setBorrowed(final String pBorrowerName, final Date pBorrowDate) {
		CoreUtils.assertNotNull(pBorrowerName);
		CoreUtils.assertNotNull(pBorrowDate);
		setBorrowDate(pBorrowDate);
		setBorrowerName(pBorrowerName);
		setBorrower(null);
		setLatestReminderDate(null);
		setNbRemindersSent(Integer.valueOf(0));

	}

	/**
	 * Set the borrower and borrowDate fields and set borrowerName to null.
	 *
	 * @param pBorrower
	 * @param pBorrowDate
	 */
	public void setBorrowed(final Person pBorrower, final Date pBorrowDate) {
		CoreUtils.assertNotNull(pBorrower);
		CoreUtils.assertNotNull(pBorrowDate);
		setBorrowDate(pBorrowDate);
		setBorrowerName(null);
		setBorrower(pBorrower);
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("Item, ");
		sb.append("ID: ");
		sb.append(getId());
		sb.append(", title: ");
		sb.append(getTitle());
		return sb.toString();
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
		else if (!(obj instanceof Item)){
			return false;
		}
		else {
			final Item other = (Item)obj;
			return getId() != null && getId().equals(other.getId());
		}
	}
}



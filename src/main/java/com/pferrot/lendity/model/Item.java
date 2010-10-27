package com.pferrot.lendity.model;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.pferrot.core.CoreUtils;

/**
 * @author Patrice
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
		name = "ITEM_TYPE",
		discriminatorType = DiscriminatorType.STRING
)
@Table(name = "ITEMS")
@Audited
public abstract class Item implements CategoryEnabled, Borrowable, Serializable {
	
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
	@OneToOne(targetEntity = Person.class)
	@JoinColumn(name = "BORROWER_ID", nullable = true)
	private Person borrower;

	// If the borrower is not a user of the system.
	@Column(name = "BORROWER_NAME", nullable = true, length = 255)
	private String borrowerName;

	@Column(name = "BORROW_DATE", nullable = true)
	private Date borrowDate;
	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date creationDate;
	
	@Version
	@Column(name = "OBJ_VERSION")
	private int version;	
	
    public Item() {
    	super();
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
		return getBorrowDate() != null;
	}


	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Reset the lend date and set the borrowerName / borrowerId to null.
	 */
	public void setLendBack() {
		setBorrowDate(null);
		setBorrowerName(null);
		setBorrower(null);
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
		
		if (this instanceof InternalItem) {
			sb.append("InternalItem, ");
			sb.append("ID: ");
			sb.append(((InternalItem)this).getId());
		}
		else if (this instanceof ExternalItem) {
			sb.append("ExternalItem, ");
			sb.append("ID: ");
			sb.append(((ExternalItem)this).getId());
		}
		sb.append(", title: ");
		sb.append(getTitle());
		return sb.toString();
	}
}



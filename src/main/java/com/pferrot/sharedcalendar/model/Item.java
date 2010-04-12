package com.pferrot.sharedcalendar.model;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.envers.Audited;

import com.pferrot.core.CoreUtils;

/**
 * @author Patrice
 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Item implements Borrowable, Serializable {

	@Column(name = "TITLE", nullable = false, length = 255)
	@Audited
	private String title;

	@Column(name = "DESCRIPTION", nullable = true, length = 3999)
	@Audited
	private String description;
	
	@ManyToMany(targetEntity = ItemCategory.class)
	private Set<ItemCategory> categories = new HashSet<ItemCategory>();

	// If the borrower if a user of the system.
	@OneToOne(targetEntity = Person.class)
	@JoinColumn(name = "BORROWER_ID", nullable = true)
	private Person borrower;

	// If the borrower is not a user of the system.
	@Column(name = "BORROWER_NAME", nullable = true, length = 255)
	private String borrowerName;

	@Column(name = "BORROW_DATE", nullable = true)
	private Date borrowDate;
	
	@Version
	@Column(name = "OBJ_VERSION")
	private int version;	
	
    public Item() {
    	super();
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

	public Set<ItemCategory> getCategories() {
		return categories;
	}

	public void setCategories(Set<ItemCategory> categories) {
		this.categories = categories;
	}

	public void addCategory(final ItemCategory pCategory) {
		CoreUtils.assertNotNull(pCategory);
		categories.add(pCategory);
	}

	public void removeCategory(final ItemCategory pCategory) {
		CoreUtils.assertNotNull(pCategory);
		categories.remove(pCategory);
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



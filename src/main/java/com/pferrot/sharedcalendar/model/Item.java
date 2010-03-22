package com.pferrot.sharedcalendar.model;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.envers.Audited;

import com.pferrot.core.CoreUtils;
import com.pferrot.security.model.User;

/**
 * @author Patrice
 *
 */
@Entity
@Table(name = "ITEMS")
public class Item implements Ownable, Borrowable, Serializable {

	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;

	@Column(name = "TITLE", nullable = false, length = 255)
	@Audited
	private String title;

	@Column(name = "DESCRIPTION", nullable = true, length = 3999)
	@Audited
	private String description;

	@Column(name = "LOCKED", nullable = false)
	private Boolean locked = Boolean.FALSE;
	
	@ManyToMany(targetEntity = ItemCategory.class)
	private Set<ItemCategory> categories = new HashSet<ItemCategory>();

	@OneToOne(targetEntity = User.class)
	@JoinColumn(name = "OWNER_ID", nullable = false)
	private User owner;

	@OneToMany(mappedBy = "ownable", targetEntity = OwnerHistoryEntry.class,
			   cascade = {CascadeType.PERSIST})
	@OrderBy(value = "startDate DESC")
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})	
	private List<OwnerHistoryEntry> ownerHistoryEntries = new ArrayList<OwnerHistoryEntry>();	

	@OneToOne(targetEntity = User.class)
	@JoinColumn(name = "BORROWER_ID", nullable = true)
	private User borrower;

	@Column(name = "BORROW_DATE", nullable = true)
	private Date borrowDate;
	
	@OneToMany(mappedBy = "borrowable", targetEntity = BorrowerHistoryEntry.class,
			   cascade = {CascadeType.PERSIST})
	@OrderBy(value = "startDate DESC")
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})	
	private List<BorrowerHistoryEntry> borrowerHistoryEntries = new ArrayList<BorrowerHistoryEntry>();
	
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

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public boolean isLocked() {
		return Boolean.TRUE.equals(getLocked());
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public List<OwnerHistoryEntry> getOwnerHistoryEntries() {
		return ownerHistoryEntries;
	}

	public void setOwnerHistoryEntries(List<OwnerHistoryEntry> ownerHistoryEntries) {
		this.ownerHistoryEntries = ownerHistoryEntries;
	}

	public User getBorrower() {
		return borrower;
	}

	public void setBorrower(User borrower) {
		this.borrower = borrower;
	}

	public Date getBorrowDate() {
		return borrowDate;
	}

	public void setBorrowDate(Date borrowDate) {
		this.borrowDate = borrowDate;
	}

	public boolean isBorrowed() {
		return getBorrower() != null;
	}

	public List<BorrowerHistoryEntry> getBorrowerHistoryEntries() {
		return borrowerHistoryEntries;
	}

	public void setBorrowerHistoryEntries(List<BorrowerHistoryEntry> borrowerHistoryEntries) {
		this.borrowerHistoryEntries = borrowerHistoryEntries;
	}
	
	public boolean isAvailable() {
		return !isBorrowed() && !isLocked();
	}
}



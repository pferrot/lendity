package com.pferrot.sharedcalendar.model;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

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

	public boolean isAvailable() {
		return !isBorrowed() && isVisible();
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InternalItem other = (InternalItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}



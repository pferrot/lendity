package com.pferrot.lendity.model;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This is an item that does not belong to someone in the system.
 * It will typically be created by a user to list an item that he
 * borrowed from someone not using the system.
 *
 * @author Patrice
 *
 */
@Entity
@Table(name = "EXTERNAL_ITEMS")
public class ExternalItem extends Item {

	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;

	@Column(name = "OWNER_NAME", nullable = false, length = 255)
	private String ownerName;

    public ExternalItem() {
    	super();
    }

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
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
		else if (!(obj instanceof ExternalItem)){
			return false;
		}
		else {
			final ExternalItem other = (ExternalItem)obj;
			return id != null && id.equals(other.getId());
		}
	}
}



package com.pferrot.lendity.model;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

/**
 * This is an item that does not belong to someone in the system.
 * It will typically be created by a user to list an item that he
 * borrowed from someone not using the system.
 *
 * @author Patrice
 *
 */
@Entity
@DiscriminatorValue("External")
@Audited
public class ExternalItem extends Item {

	@Column(name = "OWNER_NAME", length = 255)
	private String ownerName;

    public ExternalItem() {
    	super();
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
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
			return getId() != null && getId().equals(other.getId());
		}
	}
}



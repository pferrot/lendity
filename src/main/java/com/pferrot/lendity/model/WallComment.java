package com.pferrot.lendity.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("WallComment")
@Audited
public class WallComment extends Comment {

	// if null = wall of the owner
	@ManyToOne(targetEntity = Person.class)
	@JoinColumn(name = "WALL_OWNER_ID", nullable = true)
	private Person wallOwner;
	
	// Specify whether only wallOwner and owner can see it or
	// any one connection of wallOwner
	@Column(name = "PRIVATE_COMMENT", nullable = true)
	private Boolean privateComment;	
	
	@Override
	public Commentable getContainer() {
		return null;
	}

	public Person getWallOwner() {
		return wallOwner;
	}

	public void setWallOwner(Person wallOwner) {
		this.wallOwner = wallOwner;
	}

	public Boolean getPrivateComment() {
		return privateComment;
	}

	public void setPrivateComment(Boolean privateComment) {
		this.privateComment = privateComment;
	}
}

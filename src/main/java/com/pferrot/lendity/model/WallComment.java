package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("WallComment")
@Audited
public class WallComment extends Comment {

	@Override
	public Commentable getContainer() {
		return null;
	}
}

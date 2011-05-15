package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("GroupComment")
@Audited
public class GroupComment extends Comment {

	@ManyToOne
	@JoinColumn(name = "GROUP_ID")
	private Group group;

	public Group getGroup() {
		return group;
	}
	
	public void setGroup(Group group) {
		this.group = group;
	}

	@Override
	public Commentable getContainer() {
		return getGroup();
	}
}

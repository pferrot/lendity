package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("ChildComment")
@Audited
public class ChildComment extends Comment {

	@ManyToOne(targetEntity = Comment.class)
	@JoinColumn(name = "PARENT_COMMENT_ID", nullable = false)
	private Comment parentComment;

	public Comment getParentComment() {
		return parentComment;
	}

	public void setParentComment(Comment parentComment) {
		this.parentComment = parentComment;
	}

	@Override
	public Commentable getContainer() {
		return getParentComment().getContainer();
	}
	
	
}

package com.pferrot.lendity.model;


public interface CommentableWithOwner<T> extends Commentable<T> {
	
	Person getOwner();
	void setOwner(final Person pPerson);
	
}

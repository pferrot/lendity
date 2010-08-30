package com.pferrot.lendity.model;

import java.util.Set;

public interface Commentable<T> {
	
	Set<T> getComments();
	void setComments(final Set<T> pComments);
	void addComment(final T pComment);	
	void removeComment(final T pComment);
	
	Set<Person> getCommentsRecipients();
	void setCommentsRecipients(final Set<Person> pCommentsRecipients);
	void addCommentRecipient(final Person pCommentRecipient);	
	void removeCommentRecipient(final Person pCommentRecipient);
}

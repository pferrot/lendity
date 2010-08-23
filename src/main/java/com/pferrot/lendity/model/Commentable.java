package com.pferrot.lendity.model;

import java.util.Set;

public interface Commentable {
	
	Set<Comment> getComments();
	void setComments(final Set<Comment> pComments);
	void addComment(final Comment pComment);	
	void removeComment(final Comment pComment);
	
	Set<Person> getCommentsRecipients();
	void setCommentsRecipients(final Set<Person> pCommentsRecipients);
	void addCommentRecipient(final Person pCommentRecipient);	
	void removeCommentRecipient(final Person pCommentRecipient);
}

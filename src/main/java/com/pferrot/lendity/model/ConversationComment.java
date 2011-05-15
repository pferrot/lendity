package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("ConversationComment")
@Audited
public class ConversationComment extends Comment {

	@ManyToOne
	@JoinColumn(name = "CONVERSATION_ID")
	// TODO
	private Object conversation;

	public Object getConversation() {
		return conversation;
	}
	
	public void setConversation(Object conversation) {
		this.conversation = conversation;
	}

	@Override
	public Commentable getContainer() {
		//return getConversation();
		return null;
	}
}

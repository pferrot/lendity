package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("ItemComment")
@Audited
public class ItemComment extends Comment {

	@ManyToOne
	@JoinColumn(name = "ITEM_ID")
	private InternalItem item;

	public InternalItem getItem() {
		return item;
	}

	public void setItem(InternalItem item) {
		this.item = item;
	}

	@Override
	public Commentable getContainer() {
		return getItem();
	}
}

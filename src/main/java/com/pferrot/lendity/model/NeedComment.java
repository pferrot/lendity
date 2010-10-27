package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("NeedComment")
@Audited
public class NeedComment extends Comment {

	@ManyToOne
	@JoinColumn(name = "NEED_ID")
	private Need need;

	public Need getNeed() {
		return need;
	}

	public void setNeed(Need need) {
		this.need = need;
	}
	
	@Override
	public Commentable getContainer() {
		return getNeed();
	}
}

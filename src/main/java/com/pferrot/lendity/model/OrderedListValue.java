package com.pferrot.lendity.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

@Entity
@Audited
public abstract class OrderedListValue extends ListValue {
	
	// POSITION is nullable otherwise exception when creating a ListValue (parent class).
	@Column(name = "POSITION", nullable = true)	
	private Integer position;
	
	public OrderedListValue() {
		super();
	}	

	public OrderedListValue(final String labelCode, final Integer position) {
		super(labelCode);
		this.position = position;
	}

	public Integer getPosition() {
		return position;
	}
	
	public void setPosition(Integer position) {
		this.position = position;
	}
}

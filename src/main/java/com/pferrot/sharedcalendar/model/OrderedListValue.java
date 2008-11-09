package com.pferrot.sharedcalendar.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public abstract class OrderedListValue extends ListValue {
	
	@Column(name = "POSITION", nullable = false)	
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

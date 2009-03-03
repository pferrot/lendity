package com.pferrot.sharedcalendar.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("TimeZone")
public class TimeZone extends OrderedListValue {
	
	public TimeZone() {
		super();
	}

	public TimeZone(final String labelCode, final Integer position) {
		super(labelCode, position);
	}
}

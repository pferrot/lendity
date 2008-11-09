package com.pferrot.sharedcalendar.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Gender")
public class Gender extends OrderedListValue {
	
	public static final String MALE_LABEL_CODE = "gender_male";
	public static final String FEMALE_LABEL_CODE = "female_male";
	
	public Gender() {
		super();
	}

	public Gender(final String labelCode, final Integer position) {
		super(labelCode, position);
	}
}

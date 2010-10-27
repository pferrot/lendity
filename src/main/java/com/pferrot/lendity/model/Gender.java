package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("Gender")
@Audited
public class Gender extends OrderedListValue {
	
	public static final String MALE_LABEL_CODE = "gender_male";
	public static final String FEMALE_LABEL_CODE = "gender_female";
	
	public Gender() {
		super();
	}

	public Gender(final String labelCode, final Integer position) {
		super(labelCode, position);
	}
}

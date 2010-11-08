package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("Country")
@Audited
public class Country extends ListValue {
	
	public static final String SWITZERLAND_LABEL_CODE = "country_suisse";	
	
	public Country() {
		super();
	}

	public Country(final String labelCode) {
		super(labelCode);
	}
}

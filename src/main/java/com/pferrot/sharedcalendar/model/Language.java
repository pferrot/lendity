package com.pferrot.sharedcalendar.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.pferrot.sharedcalendar.model.ListValue;

@Entity
@DiscriminatorValue("Language")
public class Language extends ListValue {
	
	public static final String ENGLISH_LABEL_CODE = "language_english";
	public static final String FRENCH_LABEL_CODE = "language_french";
	
	public static final String DEFAULT_LABEL_CODE = FRENCH_LABEL_CODE;
	
	public Language() {
		super();
	}

	public Language(final String labelCode) {
		super(labelCode);
	}
}

package com.pferrot.sharedcalendar.model.movie;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.pferrot.sharedcalendar.model.ListValue;

@Entity
@DiscriminatorValue("MOVIE_LANGUAGES")
public class MovieLanguage extends ListValue {
	
	public static final String ENGLISH_LABEL_CODE = "movie_language_english";
	
	public MovieLanguage() {
		super();
	}

	public MovieLanguage(final String labelCode) {
		super(labelCode);
	}
}

package com.pferrot.sharedcalendar.model.movie;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.pferrot.sharedcalendar.model.ListValue;

@Entity
@DiscriminatorValue("MOVIE_SUBTITLES")
public class MovieSubtitle extends ListValue {
	
	public static final String ENGLISH_LABEL_CODE = "movie_subtitle_english";
	
	public MovieSubtitle() {
		super();
	}

	public MovieSubtitle(final String labelCode) {
		super(labelCode);
	}
}

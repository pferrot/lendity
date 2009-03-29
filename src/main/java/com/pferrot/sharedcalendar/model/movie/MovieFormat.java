package com.pferrot.sharedcalendar.model.movie;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.pferrot.sharedcalendar.model.ListValue;

@Entity
@DiscriminatorValue("MovieFormat")
public class MovieFormat extends ListValue {
	
	public static final String DVD_ZONE_2_LABEL_CODE = "movie_format_dvdzone2";
	
	public MovieFormat() {
		super();
	}

	public MovieFormat(final String labelCode) {
		super(labelCode);
	}
}

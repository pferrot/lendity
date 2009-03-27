package com.pferrot.sharedcalendar.model.movie;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.pferrot.sharedcalendar.model.ListValue;

@Entity
@DiscriminatorValue("MOVIE_CATEGORIES")
public class MovieCategory extends ListValue {
	
	public static final String ACTION_LABEL_CODE = "movie_category_action";
	public static final String FICTION_LABEL_CODE = "movie_category_fiction";
	public static final String ADVENTURE_LABEL_CODE = "movie_category_adventure";
	
	public MovieCategory() {
		super();
	}

	public MovieCategory(final String labelCode) {
		super(labelCode);
	}
}

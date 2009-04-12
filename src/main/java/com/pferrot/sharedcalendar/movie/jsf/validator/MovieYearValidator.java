package com.pferrot.sharedcalendar.movie.jsf.validator;

import java.util.Calendar;

import javax.faces.validator.LongRangeValidator;

/**
 * The movie cannot be older than 1900 and more recent than the current year...
 * 
 * @author Patrice
 *
 */
public class MovieYearValidator extends LongRangeValidator {

	@Override
	public long getMaximum() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	@Override
	public long getMinimum() {
		return 1900;
	}


}

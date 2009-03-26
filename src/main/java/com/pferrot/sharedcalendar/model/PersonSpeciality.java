package com.pferrot.sharedcalendar.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("PersonSpeciality")
public class PersonSpeciality extends ListValue {
	
	public static final String ACTOR_LABEL_CODE = "person_speciality_actor";
	public static final String DIRECTOR_LABEL_CODE = "person_speciality_director";	
	
	public PersonSpeciality() {
		super();
	}

	public PersonSpeciality(final String labelCode) {
		super(labelCode);
	}
}

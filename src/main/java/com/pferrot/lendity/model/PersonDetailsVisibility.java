package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("PersonDetailsVisibility")
@Audited
public class PersonDetailsVisibility extends OrderedListValue {
	
	public static final String PRIVATE = "person_detailsVisibilityPrivate";
	public static final String CONNECTIONS = "person_detailsVisibilityConnections";
	public static final String PUBLIC = "person_detailsVisibilityPublic";
	
	public static final String[] LABEL_CODES = new String[]{
		PRIVATE,
		CONNECTIONS,
		PUBLIC
	};
	
	
	public PersonDetailsVisibility() {
		super();
	}

	public PersonDetailsVisibility(final String labelCode, final Integer position) {
		super(labelCode, position);
	}
}

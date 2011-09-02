package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("WallCommentsVisibility")
@Audited
public class WallCommentsVisibility extends OrderedListValue {
	
	public static final String PRIVATE = "person_wallCommentsVisibilityPrivate";
	public static final String CONNECTIONS = "person_wallCommentsVisibilityConnections";
	public static final String PUBLIC = "person_wallCommentsVisibilityPublic";
	
	public static final String[] LABEL_CODES = new String[]{
		PRIVATE,
		CONNECTIONS,
		PUBLIC
	};
	
	
	public WallCommentsVisibility() {
		super();
	}

	public WallCommentsVisibility(final String labelCode, final Integer position) {
		super(labelCode, position);
	}
}

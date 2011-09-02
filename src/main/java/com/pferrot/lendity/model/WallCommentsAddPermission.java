package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("WallCommentsAddPermission")
@Audited
public class WallCommentsAddPermission extends OrderedListValue {
	
	public static final String NO_ONE = "person_wallCommentsAddPermissionNoOne";
	public static final String CONNECTIONS = "person_wallCommentsAddPermissionConnections";
	public static final String EVERYONE = "person_wallCommentsAddPermissionEveryone";
	
	public static final String[] LABEL_CODES = new String[]{
		NO_ONE,
		CONNECTIONS,
		EVERYONE
	};
	
	
	public WallCommentsAddPermission() {
		super();
	}

	public WallCommentsAddPermission(final String labelCode, final Integer position) {
		super(labelCode, position);
	}
}

package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import com.pferrot.lendity.model.ListValue;

@Entity
@DiscriminatorValue("ItemVisibility")
@Audited
public class ItemVisibility extends OrderedListValue {
	
	public static final String PRIVATE = "item_visibilityPrivate";
	public static final String CONNECTIONS = "item_visibilityConnections";
	public static final String PUBLIC = "item_visibilityPublic";
	
	public static final String[] LABEL_CODES = new String[]{
		PRIVATE,
		CONNECTIONS,
		PUBLIC
	};
	
	
	public ItemVisibility() {
		super();
	}

	public ItemVisibility(final String labelCode, final Integer position) {
		super(labelCode, position);
	}
}

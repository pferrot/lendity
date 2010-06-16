package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("LendRequestResponse")
public class LendRequestResponse extends OrderedListValue {
	
	public static final String ACCEPT_LABEL_CODE = "lendRequest_responseAccept";
	public static final String REFUSE_LABEL_CODE = "lendRequest_responseRefuse";
	public static final String IGNORE_LABEL_CODE = "lendRequest_responseIgnore";
	
	public LendRequestResponse() {
		super();
	}

	public LendRequestResponse(final String labelCode, final Integer position) {
		super(labelCode, position);
	}
}

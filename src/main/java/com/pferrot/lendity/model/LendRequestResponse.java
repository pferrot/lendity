package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("LendRequestResponse")
@Audited
public class LendRequestResponse extends OrderedListValue {
	
	public static final String ACCEPT_LABEL_CODE = "lendRequest_responseAccept";
	public static final String REFUSE_LABEL_CODE = "lendRequest_responseRefuse";
	// Not available in the UI for now...
	public static final String IGNORE_LABEL_CODE = "lendRequest_responseIgnore";
	// In case the lend request is canceled by the requester.
	public static final String NA_LABEL_CODE = "lendRequest_responseNA";
	
	public LendRequestResponse() {
		super();
	}

	public LendRequestResponse(final String labelCode, final Integer position) {
		super(labelCode, position);
	}
}

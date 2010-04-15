package com.pferrot.sharedcalendar.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("ConnectionRequestResponse")
public class ConnectionRequestResponse extends OrderedListValue {
	
	public static final String ACCEPT_LABEL_CODE = "connectionRequest_responseAccept";
	public static final String REFUSE_LABEL_CODE = "connectionRequest_responseRefuse";
	public static final String BAN_LABEL_CODE = "connectionRequest_responseBan";
	
	public ConnectionRequestResponse() {
		super();
	}

	public ConnectionRequestResponse(final String labelCode, final Integer position) {
		super(labelCode, position);
	}
}

package com.pferrot.sharedcalendar.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("ConnectionRequestResponse")
public class ConnectionRequestResponse extends OrderedListValue {
	
	public static final String ACCEPT_LABEL_CODE = "connection_request_response_accept";
	public static final String REFUSE_LABEL_CODE = "connection_request_response_refuse";
	public static final String BAN_LABEL_CODE = "connection_request_response_ban";
	
	public ConnectionRequestResponse() {
		super();
	}

	public ConnectionRequestResponse(final String labelCode, final Integer position) {
		super(labelCode, position);
	}
}

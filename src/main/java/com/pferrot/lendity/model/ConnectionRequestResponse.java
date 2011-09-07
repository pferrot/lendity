package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("ConnectionRequestResponse")
@Audited
public class ConnectionRequestResponse extends OrderedListValue {
	
	public static final String ACCEPT_LABEL_CODE = "connectionRequest_responseAccept";
	public static final String REFUSE_LABEL_CODE = "connectionRequest_responseRefuse";
	public static final String BAN_LABEL_CODE = "connectionRequest_responseBan";
	// When the requester cancels the request.
	public static final String CANCEL_LABEL_CODE = "connectionRequest_responseCancel";
	
	public ConnectionRequestResponse() {
		super();
	}

	public ConnectionRequestResponse(final String labelCode, final Integer position) {
		super(labelCode, position);
	}
}

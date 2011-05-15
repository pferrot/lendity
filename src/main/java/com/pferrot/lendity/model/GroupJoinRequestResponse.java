package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("GroupJoinRequestResponse")
@Audited
public class GroupJoinRequestResponse extends OrderedListValue {
	
	public static final String ACCEPT_LABEL_CODE = "groupJoinRequest_responseAccept";
	public static final String REFUSE_LABEL_CODE = "groupJoinRequest_responseRefuse";
	public static final String BAN_LABEL_CODE = "groupJoinRequest_responseBan";
	
	public GroupJoinRequestResponse() {
		super();
	}

	public GroupJoinRequestResponse(final String labelCode, final Integer position) {
		super(labelCode, position);
	}
}

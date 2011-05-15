package com.pferrot.lendity.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("GroupMembershipRequestResponse")
@Audited
public class GroupMembershipRequestResponse extends OrderedListValue {
	
	public static final String ACCEPT_LABEL_CODE = "groupMembershipRequest_responseAccept";
	public static final String REFUSE_LABEL_CODE = "groupMembershipRequest_responseRefuse";
	public static final String BAN_LABEL_CODE = "groupMembershipRequest_responseBan";
	
	public GroupMembershipRequestResponse() {
		super();
	}

	public GroupMembershipRequestResponse(final String labelCode, final Integer position) {
		super(labelCode, position);
	}
}

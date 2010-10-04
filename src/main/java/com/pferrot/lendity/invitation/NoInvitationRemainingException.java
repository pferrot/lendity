package com.pferrot.lendity.invitation;

public class NoInvitationRemainingException extends InvitationException {

	public NoInvitationRemainingException() {
		super();
	}

	public NoInvitationRemainingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public NoInvitationRemainingException(String arg0) {
		super(arg0);
	}

	public NoInvitationRemainingException(Throwable arg0) {
		super(arg0);
	}
}

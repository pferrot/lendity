package com.pferrot.lendity.utils;

/**
 * General class for JSF related exceptions.
 * 
 * @author Patrice
 *
 */
public class JsfException extends RuntimeException {

	public JsfException() {
		super();
	}

	public JsfException(final String pMmessage, Throwable pCause) {
		super(pMmessage, pCause);
	}

	public JsfException(final String pMessage) {
		super(pMessage);
	}

	public JsfException(final Throwable pCause) {
		super(pCause);
	}
}

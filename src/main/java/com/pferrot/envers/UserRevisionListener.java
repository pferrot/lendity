package com.pferrot.envers;

import org.hibernate.envers.RevisionListener;

import com.pferrot.envers.model.UserRevisionEntity;
import com.pferrot.security.SecurityUtils;

/**
 * Sets the current user to the revision entry.
 * 
 * @author Patrice
 *
 */
public class UserRevisionListener implements RevisionListener {

	public void newRevision(final Object pRevisionEntity) {
		UserRevisionEntity userRevEntity = (UserRevisionEntity) pRevisionEntity;
		userRevEntity.setUser(SecurityUtils.getCurrentUser());
	}

}

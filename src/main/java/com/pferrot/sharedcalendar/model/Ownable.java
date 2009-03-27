package com.pferrot.sharedcalendar.model;

import java.util.Set;

import com.pferrot.security.model.User;

public interface Ownable {
	
	User getOwner();
	void setOwner(User owner);

	Set<OwnerHistoryEntry> getOwnerHistoryEntries();
	void setOwnerHistoryEntries(Set<OwnerHistoryEntry> ownerHistoryEntries);

}

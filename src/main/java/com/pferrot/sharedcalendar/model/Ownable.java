package com.pferrot.sharedcalendar.model;

import java.util.List;

import com.pferrot.security.model.User;

public interface Ownable {
	
	User getOwner();
	void setOwner(User owner);

	List<OwnerHistoryEntry> getOwnerHistoryEntries();
	void setOwnerHistoryEntries(List<OwnerHistoryEntry> ownerHistoryEntries);

}

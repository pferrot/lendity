package com.pferrot.sharedcalendar.model;

import java.util.List;

public interface Ownable {
	
	Person getOwner();
	void setOwner(Person owner);

	List<OwnerHistoryEntry> getOwnerHistoryEntries();
	void setOwnerHistoryEntries(List<OwnerHistoryEntry> ownerHistoryEntries);

}

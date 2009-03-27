package com.pferrot.sharedcalendar.model;

import java.util.Set;

public interface WaitListAware {

	Set<WaitListEntry> getWaitListEntries();
	void setWaitListEntries(Set<WaitListEntry> waitListEntries);

}

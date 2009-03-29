package com.pferrot.sharedcalendar.model;

import java.util.List;

public interface WaitListAware {

	List<WaitListEntry> getWaitListEntries();
	void setWaitListEntries(List<WaitListEntry> waitListEntries);

}

package com.pferrot.sharedcalendar.model;

import java.util.Date;
import java.util.List;

import com.pferrot.security.model.User;

public interface Borrowable {
	
	User getBorrower();
	void setBorrower(User borrower);
	
	Date getBorrowDate();
	void setBorrowDate(Date date);

	List<BorrowerHistoryEntry> getBorrowerHistoryEntries();
	void setBorrowerHistoryEntries(List<BorrowerHistoryEntry> borrowerHistoryEntries);

}

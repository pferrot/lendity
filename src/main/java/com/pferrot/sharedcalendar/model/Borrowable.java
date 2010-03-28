package com.pferrot.sharedcalendar.model;

import java.util.Date;
import java.util.List;

public interface Borrowable {
	
	Person getBorrower();
	void setBorrower(Person borrower);
	
	Date getBorrowDate();
	void setBorrowDate(Date date);

	List<BorrowerHistoryEntry> getBorrowerHistoryEntries();
	void setBorrowerHistoryEntries(List<BorrowerHistoryEntry> borrowerHistoryEntries);

}

package com.pferrot.lendity.model;

import java.util.Date;

public interface Borrowable {
	
	Person getBorrower();
	void setBorrower(Person borrower);
	
	// In case the borrower is not a connection / user of the system, it should
	// still be possible to add him.
	String getBorrowerName();
	void setBorrowerName(String borrowerName);
	
	Date getBorrowDate();
	void setBorrowDate(Date date);
}

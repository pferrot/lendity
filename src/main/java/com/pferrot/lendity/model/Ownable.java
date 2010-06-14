package com.pferrot.lendity.model;

import java.util.List;

public interface Ownable {
	
	Person getOwner();
	void setOwner(Person owner);
}

package com.pferrot.sharedcalendar.dao;

import com.pferrot.sharedcalendar.model.Person;

public interface PersonDao {
	
	Integer createPerson(Person person);
	
	Person findPerson(Integer personId);
	
	void updatePerson(Person person);
	
	void deletePerson(Person person);
}

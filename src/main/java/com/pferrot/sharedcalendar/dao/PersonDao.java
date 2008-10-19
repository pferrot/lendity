package com.pferrot.sharedcalendar.dao;

import com.pferrot.sharedcalendar.model.Person;

public interface PersonDao {
	
	Long createPerson(Person person);
	
	Person findPerson(Long personId);
	Person findPersonFromUsername(String username);
	
	void updatePerson(Person person);
	
	void deletePerson(Person person);
}

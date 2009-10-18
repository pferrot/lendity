package com.pferrot.sharedcalendar.dao;

import java.util.List;

import com.pferrot.security.model.User;
import com.pferrot.sharedcalendar.model.Person;

public interface PersonDao {
	
	Long createPerson(Person person);
	
	Person findPerson(Long personId);
	Person findPersonFromUsername(String username);
	Person findPersonFromUser(User user);
	
	// Search a person by username OR firstName OR lastName OR email.
	List<Person> findPersonByAnything(String searchString);	
	List<Person> findPersonByAnything(String searchString, int pFirstResult, int pMaxResults);
	
	void updatePerson(Person person);
	
	void deletePerson(Person person);
}

package com.pferrot.sharedcalendar.dao;

import java.util.List;

import com.pferrot.security.model.User;
import com.pferrot.sharedcalendar.model.Person;

public interface PersonDao {
	
	Long createPerson(Person person);
	
	Person findPerson(Long personId);
	Person findPersonFromUsername(String username);
	Person findPersonFromUser(User user);
	
	// Search a person by displayName OR firstName OR lastName OR email.
	List<Person> findPersonByAnything(String pSearchString);	
	List<Person> findPersonByAnything(String pSearchString, int pFirstResult, int pMaxResults);
	
	List<Person> findConnections(Person pPerson, int pFirstResult, int pMaxResults);
	List<Person> findConnectionsByAnything(String pSearchString, Person pPerson, int pFirstResult, int pMaxResults);

	List<Person> findBannedPersons(Person pPerson, int pFirstResult, int pMaxResults);
	List<Person> findBannedPersonsByAnything(String pSearchString, Person pPerson, int pFirstResult, int pMaxResults);
	
	void updatePerson(Person person);
	
	void deletePerson(Person person);
}

package com.pferrot.sharedcalendar.registration;

import com.pferrot.security.dao.UserDao;
import com.pferrot.security.model.User;
import com.pferrot.sharedcalendar.dao.PersonDao;
import com.pferrot.sharedcalendar.model.Person;

public class RegistrationService {
	
	private PersonDao personDao;
	private UserDao userDao;
	
	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public boolean isUsernameAvailable(final String username) {
		if (username == null) {
			throw new IllegalArgumentException("Parameter 'username' must not be null");
		}
		
		User user = userDao.findUser(username);
		
		return user == null;
	}
	
	public Long createUserAndPerson(Person person) {
		if (person == null) {
			throw new IllegalArgumentException("Parameter 'person' must not be null");
		}
		else if (person.getUser() == null) {
			throw new IllegalArgumentException("The 'person' parameter must contain a new user object");
		}
		// This will also create the user.
		return personDao.createPerson(person);
	}
	
//	public boolean isUsernameValid() {
//		
//	}
	
	
	

}

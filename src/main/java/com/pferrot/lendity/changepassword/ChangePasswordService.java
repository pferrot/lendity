package com.pferrot.lendity.changepassword;

import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.security.dao.UserDao;
import com.pferrot.security.model.User;

public class ChangePasswordService {
	
	UserDao userDao;
	PersonDao personDao;

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}	

	public PersonDao getPersonDao() {
		return personDao;
	}

	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}

	public void updateChangeCurrentUserPassword(final String pNewPassword) {
		final Person currentPerson = personDao.findPerson(PersonUtils.getCurrentPersonId());
		
		final User currentUser = currentPerson.getUser();
		currentUser.setPassword(pNewPassword);
		
		userDao.updateUser(currentUser);
	}
	
	public String getCurrentUserPassword() {
		final Person currentPerson = personDao.findPerson(PersonUtils.getCurrentPersonId());
		return currentPerson.getUser().getPassword();		
	}
}

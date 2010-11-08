package com.pferrot.lendity.changepassword;

import org.springframework.security.providers.encoding.MessageDigestPasswordEncoder;

import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.security.dao.UserDao;
import com.pferrot.security.model.User;

public class ChangePasswordService {
	
	private UserDao userDao;
	private PersonDao personDao;
	private MessageDigestPasswordEncoder passwordEncoder;

	public MessageDigestPasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(MessageDigestPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

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
		currentUser.setPassword(passwordEncoder.encodePassword(pNewPassword, null));
		
		userDao.updateUser(currentUser);
	}
	
	public String getCurrentUserPassword() {
		final Person currentPerson = personDao.findPerson(PersonUtils.getCurrentPersonId());
		return currentPerson.getUser().getPassword();		
	}
}

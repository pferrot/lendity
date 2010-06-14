package com.pferrot.lendity.person.listener;

import javax.faces.context.FacesContext;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.event.authentication.AbstractAuthenticationEvent;
import org.springframework.security.event.authentication.AuthenticationSuccessEvent;

import com.pferrot.security.dao.UserDao;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonConsts;


public class CurrentPersonLoginListener implements ApplicationListener {

	UserDao userDao;
	PersonDao personDao;

	public void setUserDao(final UserDao userDao) {
		this.userDao = userDao;
	}

	public void setPersonDao(final PersonDao personDao) {
		this.personDao = personDao;
	}

	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof AbstractAuthenticationEvent) {

			// Login successful.
			if (event instanceof AuthenticationSuccessEvent) {
				AuthenticationSuccessEvent authenticationSuccessEvent = (AuthenticationSuccessEvent) event;
				String username = authenticationSuccessEvent.getAuthentication().getName();
				final Person currentPerson = personDao.findPersonFromUsername(username);
				FacesContext.getCurrentInstance().getExternalContext().
					getSessionMap().put(PersonConsts.CURRENT_PERSON_ID_SESSION_ATTRIBUTE_NAME, currentPerson.getId());
			}
		}
	}
}

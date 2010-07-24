package com.pferrot.lendity.login.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.Authentication;
import org.springframework.security.ui.rememberme.RememberMeProcessingFilter;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.security.SecurityUtils;

public class CustomRememberMeProcessingFilter extends RememberMeProcessingFilter {

	private PersonDao personDao;
	
	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}
	
	@Override
	protected void onSuccessfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, Authentication authResult) {
		super.onSuccessfulAuthentication(request, response, authResult);
		setCurrentPersonIdInSession(request);
	}
	
	private void setCurrentPersonIdInSession(final HttpServletRequest pRequest) {
		final String currentUsername = SecurityUtils.getCurrentUsername();
		if (! StringUtils.isNullOrEmpty(currentUsername)) {
			final Person currentPerson = personDao.findPersonFromUsername(currentUsername);
			PersonUtils.updatePersonInSession(currentPerson, pRequest);
		}		
	}
}

package com.pferrot.lendity.login.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.Authentication;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.security.SecurityUtils;

/**
 * This custom filter allows setting / removing custom attribute when
 * users log in / out.
 *
 * @author pferrot
 *
 */
public class CustomAuthenticationProcessingFilter extends AuthenticationProcessingFilter {

	private PersonDao personDao;
	
	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}

	@Override
	protected void onSuccessfulAuthentication(final HttpServletRequest pRequest,
			final HttpServletResponse pResponse, final Authentication pAuthResult)
			throws IOException {
		super.onSuccessfulAuthentication(pRequest, pResponse, pAuthResult);
		setCurrentPersonIdInSession(pRequest);
	}	

	private void setCurrentPersonIdInSession(final HttpServletRequest pRequest) {
		final String currentUsername = SecurityUtils.getCurrentUsername();
		if (! StringUtils.isNullOrEmpty(currentUsername)) {
			final Person currentPerson = personDao.findPersonFromUsername(currentUsername);
			PersonUtils.updatePersonInSession(currentPerson, pRequest);
		}		
	}
}

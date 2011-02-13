package com.pferrot.lendity.login.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.security.SecurityUtils;

/**
 * This custom filter allows setting / removing custom attribute when
 * users log in / out.
 * Also, it handles the "authentication failure URL".
 *
 * @author pferrot
 *
 */
public class CustomAuthenticationProcessingFilter extends AuthenticationProcessingFilter {

	public static final String SPRING_SECURITY_REDIRECT_PARAMETER_NAME = "spring-security-redirect";
	public static final String LOGIN_FAILED_PARAMETER_NAME = "loginFailed";
	public static final String LOGIN_FAILED_PARAMETER_VALUE = "true";	
	public static final String REDIRECT_TO_PARAMETER_NAME = "redirectTo";
	
	private PersonDao personDao;
	
	private String redirectTo;
	
	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}	

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request)
			throws AuthenticationException {
		final String redirectTo = request.getParameter(SPRING_SECURITY_REDIRECT_PARAMETER_NAME);
		setRedirectTo(redirectTo);
		return super.attemptAuthentication(request);
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

	@Override
	public String getAuthenticationFailureUrl() {
		
		final StringBuffer result = new StringBuffer(PagesURL.LOGIN);
		result.append("?");
		result.append(LOGIN_FAILED_PARAMETER_NAME);
		result.append("=");
		result.append(LOGIN_FAILED_PARAMETER_VALUE);
		
		final String redirectToValue = getRedirectTo();
		if (!StringUtils.isNullOrEmpty(redirectToValue)) {
			result.append("&");
			result.append(REDIRECT_TO_PARAMETER_NAME);
			result.append("=");
			result.append(redirectToValue);
		}
		
		return result.toString();
	}	

	@Override
	protected String determineFailureUrl(HttpServletRequest request, AuthenticationException failed) {
		return getAuthenticationFailureUrl();
	}

	public String getRedirectTo() {
		return redirectTo;
	}

	public void setRedirectTo(String redirectTo) {
		this.redirectTo = redirectTo;
	}	
}

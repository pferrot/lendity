package com.pferrot.lendity.potentialconnection.jsf;

import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.connectionrequest.exception.ConnectionRequestException;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.jsf.list.AbstractListController;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.model.PotentialConnection;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.potentialconnection.PotentialConnectionConsts;
import com.pferrot.lendity.potentialconnection.PotentialConnectionService;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;
import com.pferrot.security.SecurityUtils;

public abstract class AbstractPotentialConnectionsListController extends AbstractListController {
	
	private final static Log log = LogFactory.getLog(AbstractPotentialConnectionsListController.class);
	
	private final static String REQUEST_CONNECTION_ATTRIUTE_PREFIX = "REQUEST_CONNECTION_AVAILABLE_";
	
	private PotentialConnectionService potentialConnectionService;
	private PersonService personService;
	private ConnectionRequestService connectionRequestService;
	
	public PotentialConnectionService getPotentialConnectionService() {
		return potentialConnectionService;
	}

	public void setPotentialConnectionService(
			PotentialConnectionService potentialConnectionService) {
		this.potentialConnectionService = potentialConnectionService;
	}

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public ConnectionRequestService getConnectionRequestService() {
		return connectionRequestService;
	}

	public void setConnectionRequestService(
			ConnectionRequestService connectionRequestService) {
		this.connectionRequestService = connectionRequestService;
	}

	public AbstractPotentialConnectionsListController() {
		super();
		setRowsPerPage(PotentialConnectionConsts.NB_POTENTIAL_CONNECTIONS_PER_PAGE);
	}
	
	public String getConnectionOverviewHref() {
		final PotentialConnection pc = (PotentialConnection)getTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(pc.getConnection().getId().toString());	
	}
	
	public String getConnectionThumbnailSrc() {
		final PotentialConnection pc = (PotentialConnection)getTable().getRowData();
		return personService.getProfileThumbnailSrc(pc.getConnection(), true);
	}
	
	public boolean isRequestConnectionDisabled() {
		try {
			if (!SecurityUtils.isLoggedIn()) {
				return true;
			}
			
			final PotentialConnection pc = (PotentialConnection)getTable().getRowData();			
			
			// Not sure why this is called 3 times per person !? Avoid hitting DB.
			final HttpServletRequest request = JsfUtils.getRequest();
			final Boolean requestResult = (Boolean)request.getAttribute(REQUEST_CONNECTION_ATTRIUTE_PREFIX + pc.getConnection().getId());
			if (requestResult != null) {
				return requestResult.booleanValue();
			}
			boolean result = !getConnectionRequestService().isConnectionRequestAllowedFromCurrentUser(pc.getConnection());
			request.setAttribute(REQUEST_CONNECTION_ATTRIUTE_PREFIX + pc.getConnection().getId(), Boolean.valueOf(result));
			return result;			
		}
		catch (ConnectionRequestException e) {
			// TODO redirect to error page instead.
			throw new RuntimeException(e);
		}			
	}
	
	public String getRequestConnectionDisabledLabel() {
		if (isUncompletedConnectionRequestAvailable()) {
			final Locale locale = I18nUtils.getDefaultLocale();
			return I18nUtils.getMessageResourceString("person_pendingRequestExists", locale);	
		}
		else if (isBannedByPerson()) {
			final Locale locale = I18nUtils.getDefaultLocale();
			return I18nUtils.getMessageResourceString("person_bannedByPerson", locale);	
		}
		else if (isConnection()) {
			final Locale locale = I18nUtils.getDefaultLocale();
			return I18nUtils.getMessageResourceString("person_alreadyConnection", locale);
		}
		else if (isYourSelf()) {
			final Locale locale = I18nUtils.getDefaultLocale();
			return I18nUtils.getMessageResourceString("person_yourSelf", locale);
		}
		return null;		
	}
	
	public boolean isBannedByPerson() {
		final PotentialConnection pc = (PotentialConnection)getTable().getRowData();
		final Person p = pc.getConnection();
		return SecurityUtils.isLoggedIn() &&
			   getPersonService().isBannedBy(p.getId(), PersonUtils.getCurrentPersonId());
	}
	
	public boolean isConnection() {
		final PotentialConnection pc = (PotentialConnection)getTable().getRowData();
		final Person p = pc.getConnection();
		return SecurityUtils.isLoggedIn() &&
			   getPersonService().isConnection(p.getId(), PersonUtils.getCurrentPersonId());
	}
	
	public boolean isYourSelf() {
		final PotentialConnection pc = (PotentialConnection)getTable().getRowData();
		final Person p = pc.getConnection();
		return SecurityUtils.isLoggedIn() &&
		       p.getId().equals(PersonUtils.getCurrentPersonId());
	}
	
	public boolean isUncompletedConnectionRequestAvailable() {
		final PotentialConnection pc = (PotentialConnection)getTable().getRowData();
		final Person p = pc.getConnection();
		return SecurityUtils.isLoggedIn() &&
			   getConnectionRequestService().isUncompletedConnectionRequestAvailable(p.getId(), PersonUtils.getCurrentPersonId());
	}
	
	public String ignorePotentialConnection() {
		final PotentialConnection pc = (PotentialConnection)getTable().getRowData();
		pc.setIgnored(Boolean.TRUE);
		getPotentialConnectionService().updatePotentialConnection(pc);
		return "success";
	}
	
	public String unignorePotentialConnection() {
		final PotentialConnection pc = (PotentialConnection)getTable().getRowData();
		pc.setIgnored(Boolean.FALSE);
		getPotentialConnectionService().updatePotentialConnection(pc);
		return "success";
	}
	
	public String deletePotentialConnection() {
		final PotentialConnection pc = (PotentialConnection)getTable().getRowData();
		getPotentialConnectionService().deletePotentialConnection(pc);
		return "success";
	}
	
	public String deleteAllPotentialConnections() {
		getPotentialConnectionService().deletePotentialConnectionsForPerson(PersonUtils.getCurrentPersonId());
		return "success";
	}
	
	public boolean isIgnored() {
		final PotentialConnection pc = (PotentialConnection)getTable().getRowData();
		return Boolean.TRUE.equals(pc.getIgnored());
	}
	
	public String getInvitationSentOnLabel() {
		final PotentialConnection pc = (PotentialConnection)getTable().getRowData();
		final Date date = pc.getInvitationSentOn(); 
		if (date == null) {
			return "";
		}
		else {
			return UiUtils.getDateAsString(date, I18nUtils.getDefaultLocale());
		}
		
	}
}
package com.pferrot.lendity.potentialconnection.jsf;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.faces.component.html.HtmlDataTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.connectionrequest.exception.ConnectionRequestException;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.invitation.InvitationException;
import com.pferrot.lendity.invitation.InvitationService;
import com.pferrot.lendity.model.PotentialConnection;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.potentialconnection.PotentialConnectionService;
import com.pferrot.lendity.potentialconnection.exception.PotentialConnectionException;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;
import com.pferrot.security.SecurityUtils;

public class PotentialConnectionsImportController  {
	
	private final static Log log = LogFactory.getLog(PotentialConnectionsImportController.class);

	private PotentialConnectionService potentialConnectionService;
	private PersonService personService;
	private InvitationService invitationService;
	private ConnectionRequestService connectionRequestService;
	
	private UploadedFile uploadFile;
	private String textareaContent;
	private String source;
	
	private Boolean saveResults = Boolean.TRUE;
	
	private Set<PotentialConnection> potential = null;
	private Set<PotentialConnection> banned = null;
	private Set<PotentialConnection> connectionRequestPending = null;
	private Set<PotentialConnection> alreadyConnected = null;
	private Set<PotentialConnection> doNotExist = null;
	
	private HtmlDataTable potentialTable;
	private HtmlDataTable alreadyConnectedTable;
	private HtmlDataTable doNotExistTable;
	
	private String googleToken;
	public final static String GOOGLE_TOKEN_PARAMETER_NAME = "token";
	
	private String facebookCode;
	public final static String FACEBOOK_CODE_PARAMETER_NAME = "code";
	
	int nbConnectionRequestsSent = 0;
	int nbInvitationsSent = 0;

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

	public InvitationService getInvitationService() {
		return invitationService;
	}

	public void setInvitationService(InvitationService invitationService) {
		this.invitationService = invitationService;
	}

	public ConnectionRequestService getConnectionRequestService() {
		return connectionRequestService;
	}

	public void setConnectionRequestService(
			ConnectionRequestService connectionRequestService) {
		this.connectionRequestService = connectionRequestService;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	public boolean isFacebookSource() {
		return PotentialConnection.SOURCE_FACEBOOK.equals(getSource());
	}

	public UploadedFile getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(UploadedFile uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String getTextareaContent() {
		return textareaContent;
	}

	public void setTextareaContent(String textareaContent) {
		this.textareaContent = StringUtils.getNullIfEmpty(textareaContent);
	}

	public String getGoogleToken() {
		return googleToken;
	}

	public void setGoogleToken(String googleToken) {
		this.googleToken = googleToken;
	}

	public String getFacebookCode() {
		return facebookCode;
	}

	public void setFacebookCode(String facebookCode) {
		this.facebookCode = facebookCode;
	}

	public Boolean getSaveResults() {
		return saveResults;
	}

	public void setSaveResults(Boolean saveResults) {
		this.saveResults = saveResults;
	}

	public Set<PotentialConnection> getPotential() {
		return potential;
	}
	
	public List<PotentialConnection> getPotentialAsList() {
		return new ArrayList<PotentialConnection>(getPotential());
	}

	public void setPotential(Set<PotentialConnection> potential) {
		this.potential = potential;
	}

	public Set<PotentialConnection> getAlreadyConnected() {
		return alreadyConnected;
	}
	
	public List<PotentialConnection> getAlreadyConnectedAsList() {
		return new ArrayList<PotentialConnection>(getAlreadyConnected());
	}

	public void setAlreadyConnected(Set<PotentialConnection> alreadyConnected) {
		this.alreadyConnected = alreadyConnected;
	}

	public Set<PotentialConnection> getDoNotExist() {
		return doNotExist;
	}
	
	public List<PotentialConnection> getDoNotExistAsList() {
		return new ArrayList<PotentialConnection>(getDoNotExist());
	}

	public void setDoNotExist(Set<PotentialConnection> doNotExist) {
		this.doNotExist = doNotExist;
	}

	public Set<PotentialConnection> getBanned() {
		return banned;
	}
	
	public List<PotentialConnection> getBannedAsList() {
		return new ArrayList<PotentialConnection>(getBanned());
	}	

	public void setBanned(Set<PotentialConnection> banned) {
		this.banned = banned;
	}	

	public Set<PotentialConnection> getConnectionRequestPending() {
		return connectionRequestPending;
	}
	
	public List<PotentialConnection> getConnectionRequestPendingAsList() {
		return new ArrayList<PotentialConnection>(getConnectionRequestPending());
	}

	public void setConnectionRequestPending(
			Set<PotentialConnection> connectionRequestPending) {
		this.connectionRequestPending = connectionRequestPending;
	}

	public int getNbPotential() {
		return getPotential().size();
	}
	
	public int getNbAlreadyConnected() {
		return getAlreadyConnected().size();
	}
	
	public int getNbDoNotExist() {
		return getDoNotExist().size();
	}
	
	public int getNbBanned() {
		return getBanned().size();
	}
	
	public int getNbConnectionRequestPending() {
		return getConnectionRequestPending().size();
	}
	
	public HtmlDataTable getPotentialTable() {
		return potentialTable;
	}

	public void setPotentialTable(HtmlDataTable potentialTable) {
		this.potentialTable = potentialTable;
	}

	public HtmlDataTable getAlreadyConnectedTable() {
		return alreadyConnectedTable;
	}

	public void setAlreadyConnectedTable(HtmlDataTable alreadyConnectedTable) {
		this.alreadyConnectedTable = alreadyConnectedTable;
	}

	public HtmlDataTable getDoNotExistTable() {
		return doNotExistTable;
	}

	public void setDoNotExistTable(HtmlDataTable doNotExistTable) {
		this.doNotExistTable = doNotExistTable;
	}

	private String getRequestConnectionDefaultText() {
		final String firstName = PersonUtils.getCurrentPersonFirstName();
		final String lastName = PersonUtils.getCurrentPersonLastName();
		
		return I18nUtils.getMessageResourceString("connectionRequest_textDefaultValue",
												  new Object[]{firstName, lastName},
												  I18nUtils.getDefaultLocale());
	}
	
	public void requestConnections() {
		final String text = getRequestConnectionDefaultText();
		try {
			for (PotentialConnection pc: getPotential()) {
				if (pc.isSelected() &&
					getConnectionRequestService().isConnectionRequestAllowedFromCurrentUser(pc.getConnection())) {
					nbConnectionRequestsSent++;
					getConnectionRequestService().createConnectionRequestFromCurrentUser(pc.getConnection(), text);
				}
				getPotentialConnectionService().createOrUpdatePotentialConnection(pc);
			}
		}
		catch (ConnectionRequestException e) {
			throw new RuntimeException(e);
		}
		catch (PotentialConnectionException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void skipRequestConnections() {
		try {
			for (PotentialConnection pc: getPotential()) {				
				getPotentialConnectionService().createOrUpdatePotentialConnection(pc);
			}
		}
		catch (PotentialConnectionException e) {
			throw new RuntimeException(e);
		}
	}

	public void inviteConnections() {
		try {
			for (PotentialConnection pc: getDoNotExist()) {
				if (pc.isSelected()) {
					nbInvitationsSent++;
					getInvitationService().sendInvitation(PersonUtils.getCurrentPersonId(), pc.getEmail());
					pc.setInvitationSentOn(new Date());
					// Make source = invitation so that "reverse invitations" potential connections
					// will be created.
					pc.setSource(PotentialConnection.SOURCE_INVITATION);
				}
				getPotentialConnectionService().createOrUpdatePotentialConnection(pc);
			}
		}
		catch (InvitationException e) {
			throw new RuntimeException(e);
		}
		catch (PotentialConnectionException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void skipInviteConnections() {
		try {
			for (PotentialConnection pc: getDoNotExist()) {
				getPotentialConnectionService().createOrUpdatePotentialConnection(pc);
			}
		}
		catch (PotentialConnectionException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getAlreadyConnectedConnectionHref() {
		final PotentialConnection pc = (PotentialConnection)getAlreadyConnectedTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(pc.getConnection().getId().toString());
	}
	
	public String getPotentialConnectionHref() {
		final PotentialConnection pc = (PotentialConnection)getPotentialTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(pc.getConnection().getId().toString());
	}
	
	public String getInviteContactHref() {
		final PotentialConnection pc = (PotentialConnection)getDoNotExistTable().getRowData();
		return JsfUtils.getFullUrl(PagesURL.INVITE_FRIENDS, PagesURL.INVITE_FRIENDS_PARAM_EMAIL, pc.getEmail());
	}
	
	public boolean isSendInvitationAvailable() {
		final PotentialConnection pc = (PotentialConnection)getDoNotExistTable().getRowData();
		return pc.getInvitationSentOn() == null;
	}
	
	public boolean isRequestConnectionAvailable() {
		try {
			final PotentialConnection pc = (PotentialConnection)getPotentialTable().getRowData();
			return getConnectionRequestService().isConnectionRequestAllowedFromCurrentUser(pc.getConnection());
		}
		catch (ConnectionRequestException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean isBannedByPerson() {
		final PotentialConnection pc = (PotentialConnection)getPotentialTable().getRowData();
		return SecurityUtils.isLoggedIn() &&
			getPersonService().isBannedBy(pc.getConnection().getId(), PersonUtils.getCurrentPersonId());
	}
	
	public boolean isUncompletedConnectionRequestAvailable() {
		final PotentialConnection pc = (PotentialConnection)getPotentialTable().getRowData();
		return SecurityUtils.isLoggedIn() &&
			   connectionRequestService.isUncompletedConnectionRequestAvailable(pc.getConnection(), personService.getCurrentPerson());
	}
	
	public String getCurrentPersonEmail() {
		return getPersonService().getCurrentPerson().getEmail();
	}

	public int getNbConnectionRequestsSent() {
		return nbConnectionRequestsSent;
	}

	public void setNbConnectionRequestsSent(int nbConnectionRequestsSent) {
		this.nbConnectionRequestsSent = nbConnectionRequestsSent;
	}

	public int getNbInvitationsSent() {
		return nbInvitationsSent;
	}

	public void setNbInvitationsSent(int nbInvitationsSent) {
		this.nbInvitationsSent = nbInvitationsSent;
	}
	
	public String getInvitationAlreadySentOnLabel() {
		final PotentialConnection pc = (PotentialConnection)getDoNotExistTable().getRowData();
		final Date date = pc.getInvitationAlreadySentOn();
		if (date == null) {
			return "";
		}
		else {
			return UiUtils.getDateAsString(date, I18nUtils.getDefaultLocale());
		}
	}
}
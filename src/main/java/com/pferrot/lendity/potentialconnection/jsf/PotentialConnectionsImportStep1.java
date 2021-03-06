package com.pferrot.lendity.potentialconnection.jsf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.PreRenderView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.client.http.AuthSubUtil;
import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.model.PotentialConnection;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.potentialconnection.PotentialConnectionConsts;
import com.pferrot.lendity.potentialconnection.PotentialConnectionEmailCaseInsensitiveComparator;
import com.pferrot.lendity.potentialconnection.PotentialConnectionService;
import com.pferrot.lendity.potentialconnection.bean.PotentialConnectionContactBean;
import com.pferrot.lendity.potentialconnection.contactsreader.BluewinPotentialConnectionContactsReader;
import com.pferrot.lendity.potentialconnection.contactsreader.FacebookPotentialConnectionContactsReader;
import com.pferrot.lendity.potentialconnection.contactsreader.FilePotentialConnectionContactsReader;
import com.pferrot.lendity.potentialconnection.contactsreader.GooglePotentialConnectionContactsReader;
import com.pferrot.lendity.potentialconnection.contactsreader.PotentialConnectionContactsReader;
import com.pferrot.lendity.potentialconnection.contactsreader.TextareaPotentialConnectionContactsReader;
import com.pferrot.lendity.potentialconnection.exception.PotentialConnectionException;
import com.pferrot.lendity.social.facebook.FacebookUtils;
import com.pferrot.lendity.social.facebook.exception.FacebookException;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

@ViewController(viewIds={"/auth/potentialconnection/potentialConnectionsImport.jspx"})
public class PotentialConnectionsImportStep1 extends AbstractPotentialConnectionsImportStep {
	
	private final static Log log = LogFactory.getLog(PotentialConnectionsImportStep1.class);
	
	private final static String TEXT_PLAIN_MIME_TYPE = "text/plain";
	private final static String CSV_MIME_TYPE = "text/csv";
	private final static String CSV_MIME_TYPE_IE = "application/vnd.ms-excel";
	
	private PotentialConnectionService potentialConnectionService;
	private PersonService personService;
	private ConnectionRequestService connectionRequestService;
	
	private UIComponent fileUIComponent;
	
	private String fileTooLargeErrorMessage;
	
	private boolean googleTokenSet;
	private boolean facebookCodeSet;	
		
	
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

	public String getFileTooLargeErrorMessage() {
		return fileTooLargeErrorMessage;
	}

	public void setFileTooLargeErrorMessage(String fileTooLargeErrorMessage) {
		this.fileTooLargeErrorMessage = fileTooLargeErrorMessage;
	}

	@InitView
	public void initView() {
		final String gToken = JsfUtils.getRequestParameter(PotentialConnectionsImportController.GOOGLE_TOKEN_PARAMETER_NAME);
		if (!StringUtils.isNullOrEmpty(gToken)) {
			getPotentialConnectionsImportController().setGoogleToken(gToken);
			setGoogleTokenSet(true);
		}
		final String facebookCode = JsfUtils.getRequestParameter(PotentialConnectionsImportController.FACEBOOK_CODE_PARAMETER_NAME);
		if (!StringUtils.isNullOrEmpty(facebookCode)) {
			getPotentialConnectionsImportController().setFacebookCode(facebookCode);
			setFacebookCodeSet(true);
		}
	}

	@PreRenderView
	public void checkRequestForFileError() {
		final HttpServletRequest request = JsfUtils.getHttpServletRequest();
		final Object fileUploadException = request.getAttribute("org.apache.myfaces.custom.fileupload.exception");
		if (log.isDebugEnabled()) {
			log.debug("File upload exception: " + fileUploadException);
		}
		if (fileUploadException != null) {
			setFileTooLargeErrorMessage(UiUtils.getFileTooLargeErrorMessageFromResource(I18nUtils.getDefaultLocale()));
		}
		else {
			setFileTooLargeErrorMessage(null);
		}
	}

	private String submitInternal(final Set<PotentialConnectionContactBean> pContacts, final String pSource, final boolean pAllowNullEmail) {
		getPotentialConnectionsImportController().setSource(pSource);
		
		Set<PotentialConnection> potential = new TreeSet<PotentialConnection>(new PotentialConnectionEmailCaseInsensitiveComparator());
		Set<PotentialConnection> alreadyConnected = new TreeSet<PotentialConnection>(new PotentialConnectionEmailCaseInsensitiveComparator());
		Set<PotentialConnection> doNotExist = new TreeSet<PotentialConnection>(new PotentialConnectionEmailCaseInsensitiveComparator());
		Set<PotentialConnection> banned = new TreeSet<PotentialConnection>(new PotentialConnectionEmailCaseInsensitiveComparator());
		Set<PotentialConnection> connectionRequestPending = new TreeSet<PotentialConnection>(new PotentialConnectionEmailCaseInsensitiveComparator());
		
		final Person currentPerson = getPersonService().getCurrentPerson();
		final Date now = new Date();
		for (PotentialConnectionContactBean contactBean: pContacts) {
			String email = contactBean.getEmail();
			String name = contactBean.getName();
			if (!pAllowNullEmail && 
				   (StringUtils.isNullOrEmpty(email) ||
					!email.contains("@") ||
					email.equalsIgnoreCase(getPotentialConnectionsImportController().getCurrentPersonEmail())||
					email.length() > PotentialConnectionConsts.POTENTIAL_CONNECTION_EMAIL_MAX_SIZE)) {
				continue;
			}
			else if (email != null) {
				email = email.trim();
			}
			
			if (StringUtils.isNullOrEmpty(name)) {
				name = null;
			}
			else {
				name = name.trim();
			}
			
			if (name != null && name.length() > PotentialConnectionConsts.POTENTIAL_CONNECTION_NAME_MAX_SIZE) {
				name = name.substring(0, PotentialConnectionConsts.POTENTIAL_CONNECTION_NAME_MAX_SIZE);
			}
			
			List<Person> connections = null;
			if (email != null) {
				connections = new ArrayList<Person>();
				final Person p = getPersonService().findEnabledPersonByEmail(email);
				if (p != null) {
					connections.add(p);
				}
			}
			else if (name != null) {
				connections = getPersonService().findEnabledPersonByName(name);
			}
			else {
				continue;
			}
			
			// No connection found.
			if (connections.isEmpty()) {
				final PotentialConnection pc = new PotentialConnection();
				pc.setPerson(currentPerson);
				pc.setPersonId(currentPerson.getId());
				pc.setEmail(email);
				pc.setName(name);
				pc.setDateAdded(now);
				pc.setIgnored(Boolean.FALSE);
				pc.setSource(pSource);
				// Only consider if email is available. Otherwise it will not be possible to invite anyway.
				if (email != null) {
					final Date invitationAlreadySentOn = getPotentialConnectionService().getInvitationSentOnDate(PersonUtils.getCurrentPersonId(), email);
					// Try to not spam people and not resend invitations to the same persons.
					if (invitationAlreadySentOn != null) {
						pc.setSelected(false);
						pc.setInvitationAlreadySentOn(invitationAlreadySentOn);
					}
					else {
						pc.setSelected(true);
					}
				}
				doNotExist.add(pc);
			}
			// For each connection (there can be several when working with name (but email is unique). 
			for (Person connection: connections) {				
				final PotentialConnection pc = new PotentialConnection();
				pc.setPerson(currentPerson);
				pc.setPersonId(currentPerson.getId());
				pc.setEmail(email);
				pc.setName(name);
				pc.setDateAdded(now);
				pc.setIgnored(Boolean.FALSE);
				pc.setSource(pSource);
				pc.setConnection(connection);
				pc.setConnectionId(connection.getId());
				pc.setDateFound(now);
				if (getPersonService().isConnection(PersonUtils.getCurrentPersonId(), connection.getId())) {
					alreadyConnected.add(pc);
				}
				else if (getPersonService().isBannedBy(connection.getId(), PersonUtils.getCurrentPersonId())) {
					banned.add(pc);
				}
				else if (getConnectionRequestService().isUncompletedConnectionRequestAvailable(connection.getId(), PersonUtils.getCurrentPersonId())) {
					connectionRequestPending.add(pc);
				}
				else {
					potential.add(pc);
				}
			}
		}
		
		getPotentialConnectionsImportController().setPotential(potential);
		getPotentialConnectionsImportController().setDoNotExist(doNotExist);
		getPotentialConnectionsImportController().setAlreadyConnected(alreadyConnected);
		getPotentialConnectionsImportController().setBanned(banned);
		getPotentialConnectionsImportController().setConnectionRequestPending(connectionRequestPending);
		
		return "success";
	}
	
	public String submitFile() {
		try {
			if (log.isDebugEnabled()) {
				log.debug("File type: " + getPotentialConnectionsImportController().getUploadFile().getContentType());
				log.debug("File name: " + getPotentialConnectionsImportController().getUploadFile().getName());
				log.debug("File size: " + getPotentialConnectionsImportController().getUploadFile().getSize() + " bytes");
			}
			
			final String mimeType = getPotentialConnectionsImportController().getUploadFile().getContentType();
			if (! TEXT_PLAIN_MIME_TYPE.equals(mimeType)) {
				notTextFile();
				return "error";
			}
		
			PotentialConnectionContactsReader reader = new FilePotentialConnectionContactsReader();
		
			final Set<PotentialConnectionContactBean> contacts = reader.getContacts(getPotentialConnectionsImportController().getUploadFile().getInputStream()); 
			
			return submitInternal(contacts, PotentialConnection.SOURCE_FILE, false);
			
		} 
		catch (IOException e) {
			ioException();
			return "error";
		}
		catch (PotentialConnectionException e) {
			if (e.getCause() != null &&
				e.getCause() instanceof IOException) {
				ioException();
			}
			else {
				internalError();
			}
			return "error";
		}				
    }
	
	public String submitBluewinFile() {
		try {
			if (log.isDebugEnabled()) {
				log.debug("File type: " + getPotentialConnectionsImportController().getUploadFile().getContentType());
				log.debug("File name: " + getPotentialConnectionsImportController().getUploadFile().getName());
				log.debug("File size: " + getPotentialConnectionsImportController().getUploadFile().getSize() + " bytes");
			}
			
			final String mimeType = getPotentialConnectionsImportController().getUploadFile().getContentType();
			if (! (CSV_MIME_TYPE.equals(mimeType) || CSV_MIME_TYPE_IE.equals(mimeType)) ) {
				notCsvFile();
				return "error";
			}
		
			PotentialConnectionContactsReader reader = new BluewinPotentialConnectionContactsReader();
		
			final Set<PotentialConnectionContactBean> contacts = reader.getContacts(getPotentialConnectionsImportController().getUploadFile().getInputStream()); 
			
			return submitInternal(contacts, PotentialConnection.SOURCE_FILE, false);
			
		} 
		catch (IOException e) {
			ioException();
			return "error";
		}
		catch (PotentialConnectionException e) {
			if (e.getCause() != null &&
				e.getCause() instanceof IOException) {
				ioException();
			}
			else {
				internalError();
			}
			return "error";
		}				
    }
	
	public String submitGoogle() {
		try {				
			ContactsService cs = new ContactsService("lendityText");
			cs.setAuthSubToken(getPotentialConnectionsImportController().getGoogleToken());
			PotentialConnectionContactsReader reader = new GooglePotentialConnectionContactsReader();
			
			final Set<PotentialConnectionContactBean> contacts = reader.getContacts(cs); 
			
			return submitInternal(contacts, PotentialConnection.SOURCE_GOOGLE, false);			
		} 
		catch (PotentialConnectionException e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}
			return "error";
		}				
    }
	
	public String submitFacebook() {
		InputStreamReader is = null;
		BufferedReader br = null;
		try {	        
	        final String next = JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.POTENTIAL_CONNECTIONS_IMPORT);
	        final String accessToken = FacebookUtils.getFacebookAccessToken(getPotentialConnectionsImportController().getFacebookCode(), next);
	        final String jsonFriendsList = FacebookUtils.getFacebookFriendsListJson(accessToken);	        
	        PotentialConnectionContactsReader reader = new FacebookPotentialConnectionContactsReader();
			final Set<PotentialConnectionContactBean> contacts = reader.getContacts(jsonFriendsList);
			
			return submitInternal(contacts, PotentialConnection.SOURCE_FACEBOOK, true);
		} 
		catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}
			return "error";
		}				
		finally {
			if (br != null) {
				try {
					br.close();
				}
				catch (IOException e) {
					if (log.isErrorEnabled()) {
			        	log.error("Could not close BufferedReader");
			        }
				}
			}
			if (is != null) {
				try {
					is.close();
				} 
				catch (IOException e) {
					if (log.isErrorEnabled()) {
			        	log.error("Could not close InputStreamReader");
			        }
				}
			}
		}
    }
	
	public String submitTextarea() {
		try {				
			PotentialConnectionContactsReader reader = new TextareaPotentialConnectionContactsReader();
			
			final Set<PotentialConnectionContactBean> contacts = reader.getContacts(getPotentialConnectionsImportController().getTextareaContent()); 
			
			return submitInternal(contacts, PotentialConnection.SOURCE_TEXTAREA, false);			
		} 
		catch (PotentialConnectionException e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}
			return "error";
		}				
	}
	
	public UIComponent getFileUIComponent() {
		return fileUIComponent;
	}

	public void setFileUIComponent(UIComponent fileUIComponent) {
		this.fileUIComponent = fileUIComponent;
	}

	// TODO change to internal error
	private void internalError() {
		String message = "";
		FacesContext context = FacesContext.getCurrentInstance();
		final Locale locale = I18nUtils.getDefaultLocale();
		message = I18nUtils.getMessageResourceString("validation_internalError", locale);
		context.addMessage(getFileUIComponent().getClientId(context), new FacesMessage(message));
	}
	
	private void ioException() {
		String message = "";
		FacesContext context = FacesContext.getCurrentInstance();
		final Locale locale = I18nUtils.getDefaultLocale();
		message = I18nUtils.getMessageResourceString("validation_importIoError", locale);
		context.addMessage(getFileUIComponent().getClientId(context), new FacesMessage(message));
	}
	
	private void notTextFile() {
		String message = "";
		FacesContext context = FacesContext.getCurrentInstance();
		final Locale locale = I18nUtils.getDefaultLocale();
		message = I18nUtils.getMessageResourceString("validation_importIoError", locale);
		context.addMessage(getFileUIComponent().getClientId(context), new FacesMessage(message));
	}
	
	private void notCsvFile() {
		String message = "";
		FacesContext context = FacesContext.getCurrentInstance();
		final Locale locale = I18nUtils.getDefaultLocale();
		message = I18nUtils.getMessageResourceString("validation_notCsvFile", locale);
		context.addMessage(getFileUIComponent().getClientId(context), new FacesMessage(message));
	}
	
//	private void tooManyEntriesInFile(final int pMaxNbEntries) {
//		String message = "";
//		FacesContext context = FacesContext.getCurrentInstance();
//		final Locale locale = I18nUtils.getDefaultLocale();
//		message = I18nUtils.getMessageResourceString("validation_maxNbPotentialConnectionsToImportExceeded",
//				new Object[]{String.valueOf(pMaxNbEntries)}, locale);
//		context.addMessage(getFileUIComponent().getClientId(context), new FacesMessage(message));
//		
//	}
	
	public void validateFile(FacesContext context, UIComponent toValidate, Object value) {
		setFileUIComponent(toValidate);
	}
	
	/**
	 * See http://code.google.com/intl/fr/apis/contacts/docs/3.0/developers_guide_java.html#auth_sub
	 * 
	 * @return
	 */
	public String getGoogleLink() {
		String next = JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.POTENTIAL_CONNECTIONS_IMPORT);
		String scope = "https://www.google.com/m8/feeds/";
		boolean secure = false;
		boolean session = true;
		String authSubLogin = AuthSubUtil.getRequestUrl(next, scope, secure, session);
		return authSubLogin;
	}	
	
	/**
	 * See https://developers.facebook.com/docs/authentication/
	 * 
	 * @return
	 */
	public String getFacebookLink() {
		try {
			final String next = JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.POTENTIAL_CONNECTIONS_IMPORT);
			return FacebookUtils.getFacebookOAuthLink(next);
		}
		catch (FacebookException e) {
			throw new RuntimeException(e);
		}
	}	
	
	public void setGoogleTokenSet(boolean googleTokenSet) {
		this.googleTokenSet = googleTokenSet;
	}

	public boolean isGoogleTokenSet() {
		return googleTokenSet;
	}
	
	public boolean isFacebookCodeSet() {
		return facebookCodeSet;
	}

	public void setFacebookCodeSet(boolean facebookCodeSet) {
		this.facebookCodeSet = facebookCodeSet;
	}
}

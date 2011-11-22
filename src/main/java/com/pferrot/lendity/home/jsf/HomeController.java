package com.pferrot.lendity.home.jsf;

import java.util.List;
import java.util.Locale;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.connectionrequest.exception.ConnectionRequestException;
import com.pferrot.lendity.groupjoinrequest.GroupJoinRequestService;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.lendtransaction.LendTransactionService;
import com.pferrot.lendity.login.jsf.AbstractHomeController;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.model.PotentialConnection;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.personconfiguration.PersonConfigurationConsts;
import com.pferrot.lendity.personconfiguration.PersonConfigurationService;
import com.pferrot.lendity.potentialconnection.PotentialConnectionService;
import com.pferrot.lendity.social.facebook.FacebookUtils;
import com.pferrot.lendity.social.facebook.exception.FacebookException;
import com.pferrot.lendity.utils.HtmlUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.security.SecurityUtils;

@ViewController(viewIds={"/auth/home.jspx"})
public class HomeController extends AbstractHomeController {
	
	private final static Log log = LogFactory.getLog(HomeController.class);
	
	private final static String POTENTIAL_CONNECTIONS_LIST_LOADED_ATTRIBUTE_NAME = "potentialConnectionsListLoaded";
	private final static String REQUEST_CONNECTION_ATTRIUTE_PREFIX = "REQUEST_CONNECTION_AVAILABLE_";

	private List potentialConnectionsList;
	
	private HtmlDataTable potentialConnectionsTable;
	
	private ConnectionRequestService connectionRequestService;
	private GroupJoinRequestService groupJoinRequestService;
	private LendTransactionService lendTransactionService;
	private PotentialConnectionService potentialConnectionService;
	private PersonConfigurationService personConfigurationService;
	
	// Keep variables to not hit the DB every time.
	private long nbGroupJoinRequestsPending = -1;
	private long nbPendingConnectionRequests = -1;
	private long nbTransactionsAsLenderWaitingForMyInput = -1;
	private long nbTransactionsAsBorrowerWaitingForMyInput = -1;
	
	private Boolean showPersonalWall = Boolean.FALSE;
	
	@InitView
	public void initView() {
		final String personalWall = JsfUtils.getRequestParameter(PagesURL.HOME_PARAM_PERSONAL_WALL);
		if ("true".equals(personalWall)) {
			setShowPersonalWall(Boolean.TRUE);
		}
		else {
			setShowPersonalWall(Boolean.FALSE);
		}
	}
	
	public String getShowPersonalWallURL() {
		return JsfUtils.getFullUrl(PagesURL.HOME, PagesURL.HOME_PARAM_PERSONAL_WALL, "true");
	}
	
	public String getShowStatusUpdatesURL() {
		return JsfUtils.getFullUrl(PagesURL.HOME, PagesURL.HOME_PARAM_PERSONAL_WALL, "false");
	}
	
	public Long getPersonId() {
		return PersonUtils.getCurrentPersonId();
	}
	
	public Boolean getShowPersonalWall() {
		return showPersonalWall;
	}
	
	public void setShowPersonalWall(Boolean showPersonalWall) {
		this.showPersonalWall = showPersonalWall;
	}

	public PersonConfigurationService getPersonConfigurationService() {
		return personConfigurationService;
	}

	public void setPersonConfigurationService(
			PersonConfigurationService personConfigurationService) {
		this.personConfigurationService = personConfigurationService;
	}

	public LendTransactionService getLendTransactionService() {
		return lendTransactionService;
	}

	public void setLendTransactionService(
			LendTransactionService lendTransactionService) {
		this.lendTransactionService = lendTransactionService;
	}

	public ConnectionRequestService getConnectionRequestService() {
		return connectionRequestService;
	}
	
	public void setConnectionRequestService(
			ConnectionRequestService connectionRequestService) {
		this.connectionRequestService = connectionRequestService;
	}

	public GroupJoinRequestService getGroupJoinRequestService() {
		return groupJoinRequestService;
	}

	public void setGroupJoinRequestService(
			GroupJoinRequestService groupJoinRequestService) {
		this.groupJoinRequestService = groupJoinRequestService;
	}

	public List getPotentialConnectionsList() {
		HttpServletRequest request = JsfUtils.getRequest();
		if (FacesContext.getCurrentInstance().getRenderResponse()
			&& ! LIST_LOADED_ATTRIBUTE_VALUE.equals(
					request.getAttribute(POTENTIAL_CONNECTIONS_LIST_LOADED_ATTRIBUTE_NAME))) {
				potentialConnectionsList =  getPotentialConnectionService().findCurrentPersonPotentialConnectonsList(0, 3);	
				request.setAttribute(POTENTIAL_CONNECTIONS_LIST_LOADED_ATTRIBUTE_NAME, LIST_LOADED_ATTRIBUTE_VALUE);
		}
        return potentialConnectionsList;
	}

	public HtmlDataTable getPotentialConnectionsTable() {
		return potentialConnectionsTable;
	}

	public void setPotentialConnectionsTable(HtmlDataTable potentialConnectionsTable) {
		this.potentialConnectionsTable = potentialConnectionsTable;
	}

	public PotentialConnectionService getPotentialConnectionService() {
		return potentialConnectionService;
	}

	public void setPotentialConnectionService(
			PotentialConnectionService potentialConnectionService) {
		this.potentialConnectionService = potentialConnectionService;
	}

	public long getNbPendingConnectionRequests() {
		if (nbPendingConnectionRequests < 0) {
			nbPendingConnectionRequests = getConnectionRequestService().countCurrentUserPendingConnectionRequests(); 
		}
		return nbPendingConnectionRequests;
	}
	
	public long getNbGroupJoinRequestsPending() {
		if (nbGroupJoinRequestsPending < 0) {
			nbGroupJoinRequestsPending = getGroupJoinRequestService().countCurrentUserPendingGroupJoinRequests();
		}
		return nbGroupJoinRequestsPending;
	}
	
	public long getNbTransactionsAsLenderWaitingForMyInput() {
		if (nbTransactionsAsLenderWaitingForMyInput < 0) {
			nbTransactionsAsLenderWaitingForMyInput = getLendTransactionService().countCurrentPersonLendTransactionsAsLenderWaitingForInput();
		}
		return nbTransactionsAsLenderWaitingForMyInput;
	}
	
	public long getNbTransactionsAsBorrowerWaitingForMyInput() {
		if (nbTransactionsAsBorrowerWaitingForMyInput < 0) {
			nbTransactionsAsBorrowerWaitingForMyInput = getLendTransactionService().countCurrentPersonLendTransactionsAsBorrowerWaitingForInput();
		}
		return nbTransactionsAsBorrowerWaitingForMyInput;
	}
	
	public boolean isEmptyHomepage() {
		final Long currentPersonId = PersonUtils.getCurrentPersonId();
		return getPersonService().countConnections(currentPersonId, null) == 0 &&
			   getItemService().countAllMyItems() == 0 &&
			   getNbGroupJoinRequestsPending() == 0 &&
			   getNbTransactionsAsLenderWaitingForMyInput() == 0 &&
			   getNbTransactionsAsBorrowerWaitingForMyInput() == 0 &&
			   getNbPendingConnectionRequests() == 0;
	}

	@Override
	protected Double getLocationLatitude() {
		return PersonUtils.getCurrentPersonAddressHomeLatitude();
	}

	@Override
	protected Double getLocationLongitude() {
		return PersonUtils.getCurrentPersonAddressHomeLongitude();
	}

	@Override
	public boolean isLocationAvailable() {
		return PersonUtils.isCurrentPersonIsAddressDefined();
	}
	
	public String getAddressLabel() {
		final Person person = getPersonService().findPerson(PersonUtils.getCurrentPersonId());
		final String address = person.getAddressHome();
		if (address != null) {
			return HtmlUtils.escapeHtmlAndReplaceCr(address);
		}
		return "";
	}
	
	public String getPotentialConnectionOverviewHref() {
		final PotentialConnection pc = (PotentialConnection)getPotentialConnectionsTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(pc.getConnection().getId().toString()); 
	}
	
	public String getPotentialConnectionThumbnail1Src() {
		final PotentialConnection pc = (PotentialConnection)getPotentialConnectionsTable().getRowData();
		return getPersonService().getProfileThumbnailSrc(pc.getConnection(), true);
	}

	public boolean isShowWallCommentHelpAutomatically() {
		return PersonConfigurationConsts.SHOW_HELP_VALUE.
			equals(getPersonConfigurationService().findPersonConfigurationValue(PersonUtils.getCurrentPersonId(), getShowWallCommentHelpConfigKey()));
	}
	
	public String getShowWallCommentHelpConfigKey() {
		return PersonConfigurationConsts.SHOW_HELP_KEY_PREFIX + "wallComment"; 
	}
	
	@Override
	public List getNeedsList() {
		HttpServletRequest request = JsfUtils.getRequest();
		if (FacesContext.getCurrentInstance().getRenderResponse()
			&& ! LIST_LOADED_ATTRIBUTE_VALUE.equals(
					request.getAttribute(NEEDS_LIST_LOADED_ATTRIBUTE_NAME))) {
				setNeedsList(
					getNeedService().findLatestNeedsHomepage(
						getLocationLatitude(),
						getLocationLongitude()));	
				request.setAttribute(NEEDS_LIST_LOADED_ATTRIBUTE_NAME, LIST_LOADED_ATTRIBUTE_VALUE);
		}
        return getNeedsListInternal();
	}
	
	public boolean isRequestConnectionDisabled() {
		try {
			if (!SecurityUtils.isLoggedIn()) {
				return true;
			}
			
			final PotentialConnection pc = (PotentialConnection)getPotentialConnectionsTable().getRowData();			
			
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
	
	public boolean isUncompletedConnectionRequestAvailable() {
		final PotentialConnection pc = (PotentialConnection)getPotentialConnectionsTable().getRowData();
		final Person p = pc.getConnection();
		return SecurityUtils.isLoggedIn() &&
			   getConnectionRequestService().isUncompletedConnectionRequestAvailable(p.getId(), PersonUtils.getCurrentPersonId());
	}
	
	public boolean isBannedByPerson() {
		final PotentialConnection pc = (PotentialConnection)getPotentialConnectionsTable().getRowData();
		final Person p = pc.getConnection();
		return SecurityUtils.isLoggedIn() &&
			   getPersonService().isBannedBy(p.getId(), PersonUtils.getCurrentPersonId());
	}
	
	public boolean isConnection() {
		final PotentialConnection pc = (PotentialConnection)getPotentialConnectionsTable().getRowData();
		final Person p = pc.getConnection();
		return SecurityUtils.isLoggedIn() &&
			   getPersonService().isConnection(p.getId(), PersonUtils.getCurrentPersonId());
	}
	
	public boolean isYourSelf() {
		final PotentialConnection pc = (PotentialConnection)getPotentialConnectionsTable().getRowData();
		final Person p = pc.getConnection();
		return SecurityUtils.isLoggedIn() &&
		       p.getId().equals(PersonUtils.getCurrentPersonId());
	}
	
	public String ignorePotentialConnection() {
		final PotentialConnection pc = (PotentialConnection)getPotentialConnectionsTable().getRowData();
		pc.setIgnored(Boolean.TRUE);
		getPotentialConnectionService().updatePotentialConnection(pc);
		return "success";
	}
	
	public boolean isProfilePictureSet() {
		final Person currentPerson = getPersonService().getCurrentPerson();
		return currentPerson != null && currentPerson.getImage() != null;
	}
	
	public String getChangeProfilePictureURL() {
		return JsfUtils.getFullUrl(PagesURL.PERSON_EDIT_PICTURE, PagesURL.PERSON_EDIT_PICTURE_PARAM_PERSON_ID, PersonUtils.getCurrentPersonId().toString());
	}
	
	public String getChangeProfilePictureFromFacebookURL() {
		try {
			final String next = JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.PERSON_IMPORT_FACEBOOK_PICTURE);
			return FacebookUtils.getFacebookOAuthLink(next);
		}
		catch (FacebookException e) {
			throw new RuntimeException(e);
		}
	}
}

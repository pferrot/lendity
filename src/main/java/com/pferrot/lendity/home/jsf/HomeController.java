package com.pferrot.lendity.home.jsf;

import java.util.List;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.groupjoinrequest.GroupJoinRequestService;
import com.pferrot.lendity.lendtransaction.LendTransactionService;
import com.pferrot.lendity.login.jsf.AbstractHomeController;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.model.PotentialConnection;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.personconfiguration.PersonConfigurationConsts;
import com.pferrot.lendity.personconfiguration.PersonConfigurationService;
import com.pferrot.lendity.potentialconnection.PotentialConnectionService;
import com.pferrot.lendity.utils.HtmlUtils;
import com.pferrot.lendity.utils.JsfUtils;

public class HomeController extends AbstractHomeController {
	
	private final static Log log = LogFactory.getLog(HomeController.class);
	
	private final static String POTENTIAL_CONNECTIONS_LIST_LOADED_ATTRIBUTE_NAME = "potentialConnectionsListLoaded";

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
	private long nbTransactionsWaitingForMyInput = -1;
	
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
	
	public long getNbTransactionsWaitingForMyInput() {
		if (nbTransactionsWaitingForMyInput < 0) {
			nbTransactionsWaitingForMyInput = getLendTransactionService().countCurrentPersonLendTransactionsWaitingForInput();
		}
		return nbTransactionsWaitingForMyInput;
	}
	
	public boolean isEmptyHomepage() {
		final Long currentPersonId = PersonUtils.getCurrentPersonId();
		return getPersonService().countConnections(currentPersonId, null) == 0 &&
			   getItemService().countAllMyItems() == 0 &&
			   getNbGroupJoinRequestsPending() == 0 &&
			   getNbTransactionsWaitingForMyInput() == 0 &&
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
		return !PersonConfigurationConsts.HIDE_HELP_VALUE.
			equals(getPersonConfigurationService().findPersonConfigurationValue(PersonUtils.getCurrentPersonId(), getShowWallCommentHelpConfigKey()));
	}
	
	public String getShowWallCommentHelpConfigKey() {
		return PersonConfigurationConsts.SHOW_HELP_KEY_PREFIX + "wallComment"; 
	}
}

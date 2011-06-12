package com.pferrot.lendity.home.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.groupjoinrequest.GroupJoinRequestService;
import com.pferrot.lendity.lendtransaction.LendTransactionService;
import com.pferrot.lendity.login.jsf.AbstractHomeController;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.HtmlUtils;

public class HomeController extends AbstractHomeController {
	
	private final static Log log = LogFactory.getLog(HomeController.class);

	private ConnectionRequestService connectionRequestService;
	private GroupJoinRequestService groupJoinRequestService;
	private LendTransactionService lendTransactionService;
	
	// Keep variables to not hit the DB every time.
	private long nbGroupJoinRequestsPending = -1;
	private long nbPendingConnectionRequests = -1;
	private long nbTransactionsWaitingForMyInput = -1;
	
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
}

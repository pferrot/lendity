package com.pferrot.lendity.home.jsf;

import java.util.List;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.item.jsf.AbstractItemsListController;
import com.pferrot.lendity.lendrequest.LendRequestService;
import com.pferrot.lendity.model.ConnectionRequest;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

public class HomeController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(HomeController.class);
	
	private final static String CONNECTIONS_UPDATES_LIST_LOADED_ATTRIBUTE_VALUE = "connUpListAttrValue";
	private final static String CONNECTIONS_UPDATES_LIST_LOADED_ATTRIBUTE_PREFIX_NAME = "connUpListAttrPrefix";
	
	private ConnectionRequestService connectionRequestService;
	private LendRequestService lendRequestService;
	private ItemService itemService;
	
	// Keep variables to not hit the DB every time.
	private long nbPendingLendRequests = -1;
	private long nbPendingConnectionRequests = -1;
	
	private List connectionsUpdatesList;
	private HtmlDataTable connectionsUpdatesTable;
	
	public ConnectionRequestService getConnectionRequestService() {
		return connectionRequestService;
	}
	public void setConnectionRequestService(
			ConnectionRequestService connectionRequestService) {
		this.connectionRequestService = connectionRequestService;
	}
	public LendRequestService getLendRequestService() {
		return lendRequestService;
	}
	public void setLendRequestService(LendRequestService lendRequestService) {
		this.lendRequestService = lendRequestService;
	}
	public ItemService getItemService() {
		return itemService;
	}
	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public long getNbPendingConnectionRequests() {
		if (nbPendingConnectionRequests < 0) {
			nbPendingConnectionRequests = connectionRequestService.countCurrentUserPendingConnectionRequests(); 
		}
		return nbPendingConnectionRequests;
	}
	
	public long getNbPendingLendRequests() {
		if (nbPendingLendRequests < 0) {
			nbPendingLendRequests = lendRequestService.countCurrentUserPendingLendRequests();
		}
		return nbPendingLendRequests;
	}

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return itemService.findMyLatestAvailableConnectionsItems();
	}
	
	public String getCreationDateLabel() {
		final Item item = (Item)getTable().getRowData();
		return UiUtils.getDateAsString(item.getCreationDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}
	
	public List getConnectionsUpdatesList() {
        // See http://balusc.blogspot.com/2006/06/using-datatables.html
		// Doing this since using session bean.
		
		HttpServletRequest request = JsfUtils.getRequest();
		if (FacesContext.getCurrentInstance().getRenderResponse()
			&& ! CONNECTIONS_UPDATES_LIST_LOADED_ATTRIBUTE_VALUE.equals(
					request.getAttribute(CONNECTIONS_UPDATES_LIST_LOADED_ATTRIBUTE_PREFIX_NAME + this.getClass().getName()))) {
	    		// Reload to get most recent data.
				final ListWithRowCount listWithRowCount = getConnectionsUpdatesListWithRowCount();
				connectionsUpdatesList = listWithRowCount.getList();
				// Flag the request so that the list is only loaded once. 
				// The "FacesContext.getCurrentInstance().getRenderResponse()" is not enough since
				// the getList method is called several times during the same phase - because of the tableControls
				// for instance.
				request.setAttribute(CONNECTIONS_UPDATES_LIST_LOADED_ATTRIBUTE_PREFIX_NAME + this.getClass().getName(), CONNECTIONS_UPDATES_LIST_LOADED_ATTRIBUTE_VALUE);
		}
        return connectionsUpdatesList;
	}
	
	public void setConnectionsUpdatesList(List connectionsUpdatesList) {
		this.connectionsUpdatesList = connectionsUpdatesList;
	}
	
	public HtmlDataTable getConnectionsUpdatesTable() {
		return connectionsUpdatesTable;
	}
	
	public void setConnectionsUpdatesTable(HtmlDataTable connectionsUpdatesTable) {
		this.connectionsUpdatesTable = connectionsUpdatesTable;
	}	
	
	private ListWithRowCount getConnectionsUpdatesListWithRowCount() {
		return connectionRequestService.findCurrentUserConnectionsUpdates(0, 5);
	}
	
	public String getRequesterOverviewHref() {
		final ConnectionRequest connectionRequest = (ConnectionRequest)getConnectionsUpdatesTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(connectionRequest.getRequester().getId().toString());
	}

	public String getConnectionOverviewHref() {
		final ConnectionRequest connectionRequest = (ConnectionRequest)getConnectionsUpdatesTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(connectionRequest.getConnection().getId().toString());
	}
	
	public String getConnectionUpdateDateLabel() {
		final ConnectionRequest connectionRequest = (ConnectionRequest)getConnectionsUpdatesTable().getRowData();
		return UiUtils.getDateAsString(connectionRequest.getResponseDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}
	
	public boolean isEmptyHomepage() {
		return getConnectionsUpdatesList().isEmpty() && 
			getList().isEmpty() && 
			getNbPendingConnectionRequests() == 0 && 
			getNbPendingLendRequests() == 0;
	}
}

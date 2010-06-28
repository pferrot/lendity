package com.pferrot.lendity.home.jsf;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.item.jsf.AbstractItemsListController;
import com.pferrot.lendity.lendrequest.LendRequestService;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.utils.UiUtils;

public class HomeController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(HomeController.class);
	
	private ConnectionRequestService connectionRequestService;
	private LendRequestService lendRequestService;
	private ItemService itemService;
	
	// Keep variables to not hit the DB every time.
	private long nbPendingLendRequests = -1;
	private long nbPendingConnectionRequests = -1;
	
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
}

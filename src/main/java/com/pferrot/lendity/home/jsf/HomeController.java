package com.pferrot.lendity.home.jsf;

import java.util.List;
import java.util.Locale;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.jsf.AbstractItemsListController;
import com.pferrot.lendity.model.CategoryEnabled;
import com.pferrot.lendity.model.ConnectionRequest;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.need.NeedService;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

public class HomeController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(HomeController.class);
	
	private final static String CONNECTIONS_UPDATES_LIST_LOADED_ATTRIBUTE_VALUE = "connUpListAttrValue";
	private final static String CONNECTIONS_UPDATES_LIST_LOADED_ATTRIBUTE_PREFIX_NAME = "connUpListAttrPrefix";
	
	private final static String CONNECTIONS_NEEDS_LIST_LOADED_ATTRIBUTE_VALUE = "connNeListAttrValue";
	private final static String CONNECTIONS_NEEDS_LIST_LOADED_ATTRIBUTE_PREFIX_NAME = "connNeListAttrPrefix";
	
	private ConnectionRequestService connectionRequestService;
	private NeedService needService;
	private PersonService personService;
	
	// Keep variables to not hit the DB every time.
	private long nbPendingLendRequests = -1;
	private long nbPendingConnectionRequests = -1;
	
	private List<ConnectionRequest> connectionsUpdatesList;
	private HtmlDataTable connectionsUpdatesTable;
	
	private List<Need> connectionsNeedsList;
	private HtmlDataTable connectionsNeedsTable;
	
	public ConnectionRequestService getConnectionRequestService() {
		return connectionRequestService;
	}
	
	public void setConnectionRequestService(
			ConnectionRequestService connectionRequestService) {
		this.connectionRequestService = connectionRequestService;
	}

	public NeedService getNeedService() {
		return needService;
	}
	
	public void setNeedService(NeedService needService) {
		this.needService = needService;
	}
	
	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public long getNbPendingConnectionRequests() {
		if (nbPendingConnectionRequests < 0) {
			nbPendingConnectionRequests = connectionRequestService.countCurrentUserPendingConnectionRequests(); 
		}
		return nbPendingConnectionRequests;
	}
	
	public long getNbPendingLendRequests() {
		if (nbPendingLendRequests < 0) {
			nbPendingLendRequests = getLendRequestService().countCurrentUserPendingLendRequests();
		}
		return nbPendingLendRequests;
	}

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getItemService().findMyLatestConnectionsItems();
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

	
	
	public List<Need> getConnectionsNeedsList() {
		// See http://balusc.blogspot.com/2006/06/using-datatables.html
		// Doing this since using session bean.
		
		HttpServletRequest request = JsfUtils.getRequest();
		if (FacesContext.getCurrentInstance().getRenderResponse()
			&& ! CONNECTIONS_NEEDS_LIST_LOADED_ATTRIBUTE_VALUE.equals(
					request.getAttribute(CONNECTIONS_NEEDS_LIST_LOADED_ATTRIBUTE_PREFIX_NAME + this.getClass().getName()))) {
	    		// Reload to get most recent data.
				final ListWithRowCount listWithRowCount = getConnectionsNeedsListWithRowCount();
				connectionsNeedsList = listWithRowCount.getList();
				// Flag the request so that the list is only loaded once. 
				// The "FacesContext.getCurrentInstance().getRenderResponse()" is not enough since
				// the getList method is called several times during the same phase - because of the tableControls
				// for instance.
				request.setAttribute(CONNECTIONS_NEEDS_LIST_LOADED_ATTRIBUTE_PREFIX_NAME + this.getClass().getName(), CONNECTIONS_NEEDS_LIST_LOADED_ATTRIBUTE_VALUE);
		}
        return connectionsNeedsList;
	}
	public void setConnectionsNeedsList(List<Need> connectionsNeedsList) {
		this.connectionsNeedsList = connectionsNeedsList;
	}
	public HtmlDataTable getConnectionsNeedsTable() {
		return connectionsNeedsTable;
	}
	public void setConnectionsNeedsTable(HtmlDataTable connectionsNeedsTable) {
		this.connectionsNeedsTable = connectionsNeedsTable;
	}
	
	public String getNeedCategoryLabel() {
		final CategoryEnabled ce = (CategoryEnabled)getConnectionsNeedsTable().getRowData();
		if (ce != null && ce.getCategory() != null) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			return I18nUtils.getMessageResourceString(ce.getCategory().getLabelCode(), locale);
		}
		else {
			return "";
		}
	}

	public String getNeedCreationDateLabel() {
		final Need need = (Need)getConnectionsNeedsTable().getRowData();
		return UiUtils.getDateAsString(need.getCreationDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}
	
	public String getNeedOverviewHref() {
		final Need need = (Need)getConnectionsNeedsTable().getRowData();
		return JsfUtils.getFullUrl(PagesURL.NEED_OVERVIEW, PagesURL.NEED_OVERVIEW_PARAM_NEED_ID, need.getId().toString());
	}

	public String getNeedOwnerHref() {
		final Need need = (Need)getConnectionsNeedsTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(need.getOwner().getId().toString());		
	}
	
	public String getNeedGotItHref() {
		final Need need = (Need)getConnectionsNeedsTable().getRowData();
		return JsfUtils.getFullUrl(PagesURL.ITEM_ADD, 
				PagesURL.ITEM_ADD_PARAM_NEED_ID,
				need.getId().toString());
	}
	
	private ListWithRowCount getConnectionsNeedsListWithRowCount() {
		return getNeedService().findMyLatestConnectionsNeeds();
	}
	
	public String getRequesterOverviewHref() {
		final ConnectionRequest connectionRequest = (ConnectionRequest)getConnectionsUpdatesTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(connectionRequest.getRequester().getId().toString());
	}

	public String getConnectionOverviewHref() {
		final ConnectionRequest connectionRequest = (ConnectionRequest)getConnectionsUpdatesTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(connectionRequest.getConnection().getId().toString());
	}

	public String getRequesterThumbnailSrc() {
		final ConnectionRequest connectionRequest = (ConnectionRequest)getConnectionsUpdatesTable().getRowData();
		if (FacesContext.getCurrentInstance().getRenderResponse()) {
			return personService.getProfileThumbnailSrc(connectionRequest.getRequester(), true);
		}
		return null;
	}

	public String getConnectionThumbnailSrc() {
		final ConnectionRequest connectionRequest = (ConnectionRequest)getConnectionsUpdatesTable().getRowData();
		if (FacesContext.getCurrentInstance().getRenderResponse()) {
			return personService.getProfileThumbnailSrc(connectionRequest.getConnection(), true);
		}
		return null;
	}
	
	public String getConnectionUpdateDateLabel() {
		final ConnectionRequest connectionRequest = (ConnectionRequest)getConnectionsUpdatesTable().getRowData();
		return UiUtils.getDateAsString(connectionRequest.getResponseDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}
	
	public boolean isEmptyHomepage() {
		return getConnectionsUpdatesList().isEmpty() && 
			getList().isEmpty() && 
			getConnectionsNeedsList().isEmpty() &&
			getNbPendingConnectionRequests() == 0 && 
			getNbPendingLendRequests() == 0;
	}
}

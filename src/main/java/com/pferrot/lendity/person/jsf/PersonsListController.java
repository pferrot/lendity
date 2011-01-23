package com.pferrot.lendity.person.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.connectionrequest.exception.ConnectionRequestException;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

public class PersonsListController extends AbstractPersonsListController {
	
	private final static Log log = LogFactory.getLog(PersonsListController.class);
	
	private final static String REQUEST_CONNECTION_ATTRIUTE_PREFIX = "REQUEST_CONNECTION_AVAILABLE_";
	
	private List<SelectItem> maxDistanceSelectItems;
	private Long maxDistance;
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
	    Double maxDistanceDouble = null;
	    if (getMaxDistance() != null) {
	    	maxDistanceDouble = Double.valueOf(getMaxDistance());
	    }
		return getPersonService().findEnabledPersons(getSearchString(), maxDistanceDouble, getFirstRow(), getRowsPerPage());
	}
	
	public boolean isSearchByDistanceAvailable() {
		return PersonUtils.isCurrentPersonIsAddressDefined();
	}

	public String requestConnection() {
		try {
			final Person person = (Person)getTable().getRowData();
			getConnectionRequestService().createConnectionRequestFromCurrentUser(person);
			return "requestConnection";
		}
		catch (ConnectionRequestException e) {
			// TODO redirect to error page instead.
			throw new RuntimeException(e);
		}		
	}

	public boolean isRequestConnectionDisabled() {
		try {
			final Person person = (Person)getTable().getRowData();			
			
			// Not sure why this is called 3 times per person !? Avoid hitting DB.
			final HttpServletRequest request = JsfUtils.getRequest();
			final Boolean requestResult = (Boolean)request.getAttribute(REQUEST_CONNECTION_ATTRIUTE_PREFIX + person.getId());
			if (requestResult != null) {
				return requestResult.booleanValue();
			}
			boolean result = !getConnectionRequestService().isConnectionRequestAllowedFromCurrentUser(person);
			request.setAttribute(REQUEST_CONNECTION_ATTRIUTE_PREFIX + person.getId(), Boolean.valueOf(result));
			return result;			
		}
		catch (ConnectionRequestException e) {
			// TODO redirect to error page instead.
			throw new RuntimeException(e);
		}			
	}

	public Long getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(Long maxDistance) {
		this.maxDistance = UiUtils.getPositiveLongOrNull(maxDistance);
	}
	
	public List<SelectItem> getMaxDistanceSelectItems() {
		if (maxDistanceSelectItems == null) {
			maxDistanceSelectItems = new ArrayList<SelectItem>();
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			// Add all categories first.
			maxDistanceSelectItems.add(getNoMaxDistanceSelectItem(locale));
			maxDistanceSelectItems.add(new SelectItem(Long.valueOf(1), "1 km"));
			maxDistanceSelectItems.add(new SelectItem(Long.valueOf(5), "5 km"));
			maxDistanceSelectItems.add(new SelectItem(Long.valueOf(10), "10 km"));
			maxDistanceSelectItems.add(new SelectItem(Long.valueOf(20), "20 km"));
			maxDistanceSelectItems.add(new SelectItem(Long.valueOf(50), "50 km"));
			maxDistanceSelectItems.add(new SelectItem(Long.valueOf(100), "100 km"));
			maxDistanceSelectItems.add(new SelectItem(Long.valueOf(500), "500 km"));
		}		
		return maxDistanceSelectItems;	
	}

	private SelectItem getNoMaxDistanceSelectItem(final Locale pLocale) {
		final String label = I18nUtils.getMessageResourceString("geolocation_noMaxDistance", pLocale);
		final SelectItem si = new SelectItem(null, label);
		return si;
	}
	
	public void maxDistance(final ValueChangeEvent pEevent) {
    	final Long maxDistance = (Long) ((HtmlSelectOneMenu) pEevent.getComponent()).getValue();
    	setMaxDistance(maxDistance);
        // loadDataList() is not called by getList() when the page is submitted with the onchange
        // event on the h:selectOneMenu. Not sure why!?
        reloadList();
    }
	
	public String clearMaxDistance() {
		setMaxDistance(null);
		return "clearMaxDistance";
	}

	@Override
	public boolean isFilteredList() {
		boolean tempResult = getMaxDistance() != null; 
		return tempResult || super.isFilteredList();
	}
}

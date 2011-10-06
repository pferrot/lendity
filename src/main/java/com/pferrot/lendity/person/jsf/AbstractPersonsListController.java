package com.pferrot.lendity.person.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.geolocation.GeoLocationUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.jsf.list.AbstractListController;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonConsts;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.UiUtils;

public abstract class AbstractPersonsListController extends AbstractListController {
	private final static Log log = LogFactory.getLog(AbstractPersonsListController.class);
	
	private List<SelectItem> orderBySelectItems;
	// null = by display name ascending
	// 1 = by distance descending
	private Long orderBy;
	
	private PersonService personService;
	private ConnectionRequestService connectionRequestService;

	public AbstractPersonsListController() {
		super();
		setRowsPerPage(PersonConsts.NB_PERSONS_PER_PAGE);
	}

	public void setPersonService(final PersonService pPersonService) {
		this.personService = pPersonService;
	}
	
	public PersonService getPersonService() {
		return personService;
	}

	public ConnectionRequestService getConnectionRequestService() {
		return connectionRequestService;
	}

	public void setConnectionRequestService(final ConnectionRequestService pConnectionRequestService) {
		this.connectionRequestService = pConnectionRequestService;
	}

	public String getPersonOverviewHref() {
		final Person person = (Person)getTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(person.getId().toString());
	}

	public String getProfilePictureSrc() {
		final Person person = (Person)getTable().getRowData();
		return personService.getProfilePictureSrc(person, true);
	}
	
	public String getProfileThumbnailSrc() {
		final Person person = (Person)getTable().getRowData();
		return personService.getProfileThumbnailSrc(person, true);
	}
	
	public boolean isFilteredList() {
		return !StringUtils.isNullOrEmpty(getSearchString());
	}

	public String getDistanceLabel() {
		final Person person = (Person)getTable().getRowData();
		return GeoLocationUtils.getApproxDistanceKm(PersonUtils.getCurrentPersonAddressHomeLatitude(),
													PersonUtils.getCurrentPersonAddressHomeLongitude(),
													person.getAddressHomeLatitude(),
													person.getAddressHomeLongitude());
	}
	
	public List<SelectItem> getOrderBySelectItems() {
		if (orderBySelectItems == null) {
			final List<SelectItem> result = new ArrayList<SelectItem>();
			final Locale locale = I18nUtils.getDefaultLocale();
			
			result.add(new SelectItem(null, I18nUtils.getMessageResourceString("person_orderByDisplayNameAsc", locale)));
			if (PersonUtils.isCurrentPersonIsAddressDefined()) {
				result.add(new SelectItem(new Long(1), I18nUtils.getMessageResourceString("geolocation_orderByDistanceAsc", locale)));
			}
			result.add(new SelectItem(new Long(2), I18nUtils.getMessageResourceString("person_orderByJoinDateDesc", locale)));
			
			orderBySelectItems = result;
		}		
		return orderBySelectItems;	
	}
	
	public Long getOrderBy() {
		return orderBy;
	}
	
	public void setOrderBy(Long orderBy) {
		this.orderBy = UiUtils.getPositiveLongOrNull(orderBy);
	}

	public void orderBy(final ValueChangeEvent pEevent) {
    	final Long orderBy = (Long) ((HtmlSelectOneMenu) pEevent.getComponent()).getValue();
        setOrderBy(orderBy);
        // loadDataList() is not called by getList() when the page is submitted with the onchange
        // event on the h:selectOneMenu. Not sure why!?
        reloadList();
    }
	
	protected String getOrderByField() {
		final Long orderByLong = getOrderBy();
		if (orderByLong == null) {
			return "displayName";
		}
		// Not a field - this is calculated. See in DAO implementation for details.
		else if (orderByLong.longValue() == 1) {
			if (!PersonUtils.isCurrentPersonIsAddressDefined()) {
				setOrderBy(null);
				return "displayName";
			}
			return "distance";
		}
		else if (orderByLong.longValue() == 2) {
			return "joinDate";
		}
		throw new RuntimeException("Unsupported orderBy value: " + orderByLong);
	}
	
	protected Boolean getOrderByAscending() {
		final Long orderByLong = getOrderBy();
		if (orderByLong == null) {
			return Boolean.TRUE;
		}
		else if (orderByLong.longValue() == 1) {
			if (!PersonUtils.isCurrentPersonIsAddressDefined()) {
				setOrderBy(null);
				return Boolean.TRUE;
			}
			return Boolean.TRUE;
		}
		else if (orderByLong.longValue() == 2) {
			return Boolean.FALSE;
		}
		throw new RuntimeException("Unsupported orderBy value: " + orderByLong);
	}
}

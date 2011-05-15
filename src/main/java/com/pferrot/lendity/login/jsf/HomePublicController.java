package com.pferrot.lendity.login.jsf;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.geolocation.bean.Coordinate;
import com.pferrot.lendity.geolocation.exception.GeolocalisationException;
import com.pferrot.lendity.geolocation.googlemaps.GoogleMapsUtils;
import com.pferrot.lendity.group.GroupService;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.need.NeedService;
import com.pferrot.lendity.person.PersonConsts;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.CookieUtils;
import com.pferrot.lendity.utils.JsfUtils;

public class HomePublicController implements Serializable {
	
	private final static Log log = LogFactory.getLog(HomePublicController.class);
	
	private final static String COOKIE_LOCATION_LABEL = "locationLabel";
	private final static String COOKIE_LOCATION_LATITUDE = "locationLatitude";
	private final static String COOKIE_LOCATION_LONGITUDE  = "locationLongitude";
	
	private final static String PERSONS_LIST_LOADED_ATTRIBUTE_NAME = "usersListLoaded";
	private final static String ITEMS_LIST_LOADED_ATTRIBUTE_NAME = "itemsListLoaded";
	private final static String NEEDS_LIST_LOADED_ATTRIBUTE_NAME = "needsListLoaded";
	private final static String GROUPS_LIST_LOADED_ATTRIBUTE_NAME = "groupsListLoaded";
	
	private final static String LIST_LOADED_ATTRIBUTE_VALUE = "true";
	
	// City, NPA,...
	private String location;
	
	private GroupService groupService;
	private ItemService itemService;
	private PersonService personService;
	private NeedService needService;
	
	private List personsList;
	private List itemsList;
	private List needsList;
	private List groupsList;
	
	private HtmlDataTable personsTable;
	private HtmlDataTable itemsTable;
	private HtmlDataTable needsTable;
	private HtmlDataTable groupsTable;

	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public ItemService getItemService() {
		return itemService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public NeedService getNeedService() {
		return needService;
	}

	public void setNeedService(NeedService needService) {
		this.needService = needService;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
		// Done in validator.
//		if (!StringUtils.isNullOrEmpty(location)) {
//			try {
//				final Coordinate coordinate = GoogleMapsUtils.getCoordinate(location);
//				createLocationLabelCookie(location);
//				createLocationLatitudeCookie(coordinate.getLatitude().toString());
//				createLocationLongitudeCookie(coordinate.getLongitude().toString());
//			}
//			catch (GeolocalisationException e) {
//				deleteLocationLabelCookie();
//				deleteLocationLatitudeCookie();
//				deleteLocationLongitudeCookie();
//			}
//		}
		JsfUtils.redirect(PagesURL.LOGIN);
	}

	public void validateLocation(FacesContext context, UIComponent toValidate, Object value) {
		
		String message = "";
		String location = (String) value;
		if (!StringUtils.isNullOrEmpty(location)) {			
			try {
				final Coordinate c = GoogleMapsUtils.getCoordinate(location);
				createLocationLabelCookie(location);
				createLocationLatitudeCookie(c.getLatitude().toString());
				createLocationLongitudeCookie(c.getLongitude().toString());
			}
			catch (GeolocalisationException e) {
				deleteLocationLabelCookie();
				deleteLocationLatitudeCookie();
				deleteLocationLongitudeCookie();
				
				((UIInput)toValidate).setValid(false);
				final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				message = I18nUtils.getMessageResourceString("validation_geolocationNotFound", locale);
				context.addMessage(toValidate.getClientId(context), new FacesMessage(message));					
			}
			
		}
		else {
			deleteLocationLabelCookie();
			deleteLocationLatitudeCookie();
			deleteLocationLongitudeCookie();
		}
	}

	public String resetLocation() {
		deleteLocationLabelCookie();
		deleteLocationLatitudeCookie();
		deleteLocationLongitudeCookie();
		
		JsfUtils.redirect(PagesURL.LOGIN);
		return null;
	}

	public boolean isLocationLabelCookieAvailable() {
		return getLocationLabelCookieValue() != null;
	}

	public String getLocationLabelCookieValue() {
		return CookieUtils.getCookieValue(COOKIE_LOCATION_LABEL);		
	}
	
	public String getLocationLatitudeCookieValue() {
		return CookieUtils.getCookieValue(COOKIE_LOCATION_LATITUDE);		
	}
	
	public String getLocationLongitudeCookieValue() {
		return CookieUtils.getCookieValue(COOKIE_LOCATION_LONGITUDE);		
	}
	
	public void createLocationLabelCookie(final String pValue) {
		CookieUtils.createCookie(COOKIE_LOCATION_LABEL, pValue);		
	}
	
	public void createLocationLongitudeCookie(final String pValue) {
		CookieUtils.createCookie(COOKIE_LOCATION_LONGITUDE, pValue);		
	}
	
	public void createLocationLatitudeCookie(final String pValue) {
		CookieUtils.createCookie(COOKIE_LOCATION_LATITUDE, pValue);		
	}

	public void deleteLocationLabelCookie() {
		CookieUtils.deleteCookie(COOKIE_LOCATION_LABEL);		
	}
	
	public void deleteLocationLongitudeCookie() {
		CookieUtils.deleteCookie(COOKIE_LOCATION_LONGITUDE);		
	}
	
	public void deleteLocationLatitudeCookie() {
		CookieUtils.deleteCookie(COOKIE_LOCATION_LATITUDE);		
	}

	public HtmlDataTable getPersonsTable() {
		return personsTable;
	}

	public void setPersonsTable(HtmlDataTable personsTable) {
		this.personsTable = personsTable;
	}

	public HtmlDataTable getItemsTable() {
		return itemsTable;
	}

	public void setItemsTable(HtmlDataTable itemsTable) {
		this.itemsTable = itemsTable;
	}

	public HtmlDataTable getNeedsTable() {
		return needsTable;
	}

	public void setNeedsTable(HtmlDataTable needsTable) {
		this.needsTable = needsTable;
	}

	public HtmlDataTable getGroupsTable() {
		return groupsTable;
	}

	public void setGroupsTable(HtmlDataTable groupsTable) {
		this.groupsTable = groupsTable;
	}	

	public List getPersonsList() {		
		HttpServletRequest request = JsfUtils.getRequest();
		if (FacesContext.getCurrentInstance().getRenderResponse()
			&& ! LIST_LOADED_ATTRIBUTE_VALUE.equals(
					request.getAttribute(PERSONS_LIST_LOADED_ATTRIBUTE_NAME))) {
				if (isLocationLabelCookieAvailable()) {
					personsList =  getPersonService().findRandomPersonsHomepage(
							Double.valueOf(getLocationLatitudeCookieValue()),
							Double.valueOf(getLocationLongitudeCookieValue()));	
				}
				else {
					personsList =  getPersonService().findRandomPersonsHomepage();
				}	
				request.setAttribute(PERSONS_LIST_LOADED_ATTRIBUTE_NAME, LIST_LOADED_ATTRIBUTE_VALUE);
		}
        return personsList;
	}

	public List getItemsList() {
		HttpServletRequest request = JsfUtils.getRequest();
		if (FacesContext.getCurrentInstance().getRenderResponse()
			&& ! LIST_LOADED_ATTRIBUTE_VALUE.equals(
					request.getAttribute(ITEMS_LIST_LOADED_ATTRIBUTE_NAME))) {
				if (isLocationLabelCookieAvailable()) {
					itemsList =  getItemService().findRandomItemsHomepage(
							Double.valueOf(getLocationLatitudeCookieValue()),
							Double.valueOf(getLocationLongitudeCookieValue()));	
				}
				else {
					itemsList =  getItemService().findRandomItemsHomepage();
				}				
				request.setAttribute(ITEMS_LIST_LOADED_ATTRIBUTE_NAME, LIST_LOADED_ATTRIBUTE_VALUE);
		}
        return itemsList;
	}

	public List getNeedsList() {
		HttpServletRequest request = JsfUtils.getRequest();
		if (FacesContext.getCurrentInstance().getRenderResponse()
			&& ! LIST_LOADED_ATTRIBUTE_VALUE.equals(
					request.getAttribute(NEEDS_LIST_LOADED_ATTRIBUTE_NAME))) {
				if (isLocationLabelCookieAvailable()) {
					needsList =  getNeedService().findRandomNeedsHomepage(
							Double.valueOf(getLocationLatitudeCookieValue()),
							Double.valueOf(getLocationLongitudeCookieValue()));	
				}
				else {
					needsList =  getNeedService().findRandomNeedsHomepage();
				}
				request.setAttribute(NEEDS_LIST_LOADED_ATTRIBUTE_NAME, LIST_LOADED_ATTRIBUTE_VALUE);
		}
        return needsList;
	}

	public List getGroupsList() {		
		HttpServletRequest request = JsfUtils.getRequest();
		if (FacesContext.getCurrentInstance().getRenderResponse()
			&& ! LIST_LOADED_ATTRIBUTE_VALUE.equals(
					request.getAttribute(GROUPS_LIST_LOADED_ATTRIBUTE_NAME))) {
//				if (isLocationLabelCookieAvailable()) {
//					groupsList =  getGroupService().findRandomGroupsHomepage(
//							Double.valueOf(getLocationLatitudeCookieValue()),
//							Double.valueOf(getLocationLongitudeCookieValue()));	
//				}
//				else {
					groupsList =  getGroupService().findRandomGroupsHomepage();
//				}
				request.setAttribute(GROUPS_LIST_LOADED_ATTRIBUTE_NAME, LIST_LOADED_ATTRIBUTE_VALUE);
		}
        return groupsList;
	}
	
	public String getItemOverviewHref() {
		final Item item = (Item)getItemsTable().getRowData();
		return JsfUtils.getFullUrl(PagesURL.ITEM_OVERVIEW, PagesURL.ITEM_OVERVIEW_PARAM_ITEM_ID, item.getId().toString());
	}

	public String getNeedOverviewHref() {
		final Need need = (Need)getNeedsTable().getRowData();
		return JsfUtils.getFullUrl(PagesURL.NEED_OVERVIEW, PagesURL.NEED_OVERVIEW_PARAM_NEED_ID, need.getId().toString());
	}
	
	public String getPersonOverviewHref() {
		final Person person = (Person)getPersonsTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(person.getId().toString()); 
	}
	
	public String getGroupOverviewHref() {
		final Group group = (Group)getGroupsTable().getRowData();
		return JsfUtils.getFullUrl(PagesURL.GROUP_OVERVIEW, PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID, group.getId().toString());	
	}
}

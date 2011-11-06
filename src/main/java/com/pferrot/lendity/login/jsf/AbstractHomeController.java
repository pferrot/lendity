package com.pferrot.lendity.login.jsf;

import java.io.Serializable;
import java.util.List;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.geolocation.GeoLocationUtils;
import com.pferrot.lendity.group.GroupService;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.model.Objekt;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.need.NeedService;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

public abstract class AbstractHomeController implements Serializable {
	
	private final static Log log = LogFactory.getLog(AbstractHomeController.class);
	
	protected final static String PERSONS_LIST_LOADED_ATTRIBUTE_NAME = "usersListLoaded";
	protected final static String ITEMS_LIST_LOADED_ATTRIBUTE_NAME = "itemsListLoaded";
	protected final static String NEEDS_LIST_LOADED_ATTRIBUTE_NAME = "needsListLoaded";
	protected final static String GROUPS_LIST_LOADED_ATTRIBUTE_NAME = "groupsListLoaded";
	
	protected final static String LIST_LOADED_ATTRIBUTE_VALUE = "true";
	
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
				if (isLocationAvailable()) {
					personsList =  getPersonService().findRandomPersonsHomepage(
							getLocationLatitude(),
							getLocationLongitude());	
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
				if (isLocationAvailable()) {
					itemsList =  getItemService().findRandomItemsHomepage(
							getLocationLatitude(),
							getLocationLongitude());	
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
				if (isLocationAvailable()) {
					needsList =  getNeedService().findRandomNeedsHomepage(
							getLocationLatitude(),
							getLocationLongitude());	
				}
				else {
					needsList =  getNeedService().findRandomNeedsHomepage();
				}
				request.setAttribute(NEEDS_LIST_LOADED_ATTRIBUTE_NAME, LIST_LOADED_ATTRIBUTE_VALUE);
		}
        return needsList;
	}

	public void setPersonsList(List personsList) {
		this.personsList = personsList;
	}

	public void setItemsList(List itemsList) {
		this.itemsList = itemsList;
	}

	public void setNeedsList(List needsList) {
		this.needsList = needsList;
	}
	
	public List getNeedsListInternal() {
		return needsList;
	}

	public void setGroupsList(List groupsList) {
		this.groupsList = groupsList;
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

	public String getItemDistanceLabel() {
		final Objekt objekt = (Objekt)getItemsTable().getRowData();
		return GeoLocationUtils.getApproxDistanceKm(PersonUtils.getCurrentPersonAddressHomeLatitude(),
													PersonUtils.getCurrentPersonAddressHomeLongitude(),
													objekt.getOwner().getAddressHomeLatitude(),
													objekt.getOwner().getAddressHomeLongitude());
	}

	public String getNeedDistanceLabel() {
		final Objekt objekt = (Objekt)getNeedsTable().getRowData();
		return GeoLocationUtils.getApproxDistanceKm(PersonUtils.getCurrentPersonAddressHomeLatitude(),
													PersonUtils.getCurrentPersonAddressHomeLongitude(),
													objekt.getOwner().getAddressHomeLatitude(),
													objekt.getOwner().getAddressHomeLongitude());
	}

	public String getPersonDistanceLabel() {
		final Person person = (Person)getPersonsTable().getRowData();
		return GeoLocationUtils.getApproxDistanceKm(PersonUtils.getCurrentPersonAddressHomeLatitude(),
													PersonUtils.getCurrentPersonAddressHomeLongitude(),
													person.getAddressHomeLatitude(),
													person.getAddressHomeLongitude());
	}

	public String getItemThumbnail1Src() {
		final Item item = (Item)getItemsTable().getRowData();
		return getItemService().getThumbnail1Src(item, true);
	}
	
	public String getNeedThumbnail1Src() {
		final Need need = (Need)getNeedsTable().getRowData();
		return getNeedService().getThumbnail1Src(need, true);
	}

	public String getPersonThumbnail1Src() {
		final Person person = (Person)getPersonsTable().getRowData();
		return getPersonService().getProfileThumbnailSrc(person, true);
	}

	public String getGroupThumbnail1Src() {
		final Group group = (Group)getGroupsTable().getRowData();
		return getGroupService().getGroupThumbnail1Src(group, true);
	}
	
	protected abstract boolean isLocationAvailable();
	protected abstract Double getLocationLatitude();
	protected abstract Double getLocationLongitude();
}

package com.pferrot.lendity.person.jsf;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.springframework.security.AccessDeniedException;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.connectionrequest.exception.ConnectionRequestException;
import com.pferrot.lendity.geolocation.GeoLocationUtils;
import com.pferrot.lendity.geolocation.googlemaps.GoogleMapsUtils;
import com.pferrot.lendity.group.GroupService;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.HtmlUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;
import com.pferrot.security.SecurityUtils;

@ViewController(viewIds={"/public/person/personOverview.jspx"})
public class PersonOverviewController implements Serializable { 
	
	private final static Log log = LogFactory.getLog(PersonOverviewController.class);
	
	private final static String CONNECTIONS_LIST_LOADED_ATTRIBUTE_NAME = "usersListLoaded";
	private final static String GROUPS_LIST_LOADED_ATTRIBUTE_NAME = "groupsListLoaded";
	
	protected final static String LIST_LOADED_ATTRIBUTE_VALUE = "true";
	
	private PersonService personService;
	private GroupService groupService;
	private ConnectionRequestService connectionRequestService;
	private Long personId;
	private Person person;
	
	private List connectionsList;
	private HtmlDataTable connectionsTable;
	
	private List groupsList;
	private HtmlDataTable groupsTable;
	
	@InitView
	public void initView() {
		// Read the person ID from the request parameter and load the correct person.
		try {
			final String personIdString = getPersonIdString();
			Person person = null;
			if (personIdString != null) {
				personId = Long.parseLong(personIdString);
				person = personService.findPerson(personId);
				// Access control check.
				if (!personService.isCurrentUserAuthorizedToView(person)) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (person view): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), person = " + personIdString);
					}
					return;
				}
				setPerson(person);
			}
		}
		catch (AccessDeniedException ade) {
			throw ade;
		}
		catch (Exception e) {
			//TODO display standard error page instead.
			JsfUtils.redirect(PagesURL.PERSONS_LIST);
		}		
	}
	
	protected String getPersonIdString() {
		return JsfUtils.getRequestParameter(PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID);
	}
	
	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(final PersonService pPersonService) {
		this.personService = pPersonService;
	}

	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}	

	public void setConnectionRequestService(
			ConnectionRequestService connectionRequestService) {
		this.connectionRequestService = connectionRequestService;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(final Person pPerson) {
		this.person = pPerson;
	}
	
	public String getMemberSinceLabel() {
		return UiUtils.getDateAsString(person.getUser().getCreationDate(), I18nUtils.getDefaultLocale());
	}

	public String getPersonEditHref() {		
		return PersonUtils.getPersonEditPageUrl(person.getId().toString());
	}
	
	public String getPersonEditPictureHref() {		
		return PersonUtils.getPersonEditPicturePageUrl(person.getId().toString());
	}

	public String getChangePasswordHref() {		
		return JsfUtils.getFullUrl(PagesURL.CHANGE_PASSWORD);
	}

	public boolean isEditAvailable() {
		return personService.isCurrentUserAuthorizedToEdit(person);
	}
	
	public boolean isDetailsAvailable() {
		return personService.isCurrentUserAuthorizedToViewDetails(person);
	}
	
	public String getDetailsVisibilityLabel() {
		if (getPerson() != null && getPerson().getDetailsVisibility() != null) {
			final Locale locale = I18nUtils.getDefaultLocale();
			return I18nUtils.getMessageResourceString(getPerson().getDetailsVisibility().getLabelCode(), locale);
		}
		else {
			return "";
		}
	}	
	
	public boolean isRequestConnectionDisabled() {
		try {
			return !connectionRequestService.isConnectionRequestAllowedFromCurrentUser(person);
		}
		catch (ConnectionRequestException e) {
			throw new RuntimeException(e);
		}			
	}
	
	public String getPageTitle() {
		if (isEditAvailable()) {
			final Locale locale = I18nUtils.getDefaultLocale();
			return I18nUtils.getMessageResourceString("menu_profile", locale);
		}
		else {
			return getPerson().getDisplayName();
		}		
	}
	
	public String getProfilePictureSrc() {
		if (FacesContext.getCurrentInstance().getRenderResponse()) {
			return personService.getProfilePictureSrc(getPerson(), true);
		}
		return null;
	}
	
	public String getProfileThumbnailSrc() {
		if (FacesContext.getCurrentInstance().getRenderResponse()) {
			return personService.getProfileThumbnailSrc(getPerson(), true);
		}
		return null;
	}

	public String getAddressHome() {
		final String address = person.getAddressHome();
		if (address != null) {
			return HtmlUtils.escapeHtmlAndReplaceCr(address);
		}
		return "";
	}

	public boolean isAddressHomeAvailable() {
		return person.isAddressHomeDefined();
	}

	public String getAddressHomeGoogleMapsUrl() {
		try {
			return GoogleMapsUtils.getLocationUrl(person.getAddressHome());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getPersonItemsUrl() {
		return JsfUtils.getFullUrl(
				PagesURL.PERSON_ITEMS_LIST,
				PagesURL.PERSON_ITEMS_LIST_PARAM_PERSON_ID,
				getPerson().getId().toString());
	}
	
	public String getPersonNeedsUrl() {
		return JsfUtils.getFullUrl(
				PagesURL.PERSON_NEEDS_LIST,
				PagesURL.PERSON_NEEDS_LIST_PARAM_PERSON_ID,
				getPerson().getId().toString());
	}
	
	public boolean isShowLinksToObjekts() {
		return true;
	}

	public String getDistanceLabel() {
		return GeoLocationUtils.getApproxDistanceKm(PersonUtils.getCurrentPersonAddressHomeLatitude(),
													PersonUtils.getCurrentPersonAddressHomeLongitude(),
													getPerson().getAddressHomeLatitude(),
													getPerson().getAddressHomeLongitude());
	}
	
	public boolean isOwnProfile() {		
		return SecurityUtils.isLoggedIn() && getPerson().getId().equals(PersonUtils.getCurrentPersonId());
	}
	
	public boolean isUncompletedConnectionRequestAvailable() {
		return SecurityUtils.isLoggedIn() &&
			   connectionRequestService.isUncompletedConnectionRequestAvailable(getPerson(), personService.getCurrentPerson());
	}
	
	public boolean isBannedByPerson() {
		return SecurityUtils.isLoggedIn() &&
		       getPerson().getBannedPersons().contains(personService.getCurrentPerson());
	}
	
	public String getBirthdateLabel() {
		return I18nUtils.getSimpleDateFormat().format(getPerson().getBirthdate());
	}

	public HtmlDataTable getConnectionsTable() {
		return connectionsTable;
	}

	public void setConnectionsTable(HtmlDataTable connectionsTable) {
		this.connectionsTable = connectionsTable;
	}
	
	public List getConnectionsList() {		
		HttpServletRequest request = JsfUtils.getRequest();
		if (FacesContext.getCurrentInstance().getRenderResponse()
			&& ! LIST_LOADED_ATTRIBUTE_VALUE.equals(
					request.getAttribute(CONNECTIONS_LIST_LOADED_ATTRIBUTE_NAME))) {	
				connectionsList = getPersonService().findConnectionsList(personId, null, 0, 0);	
				request.setAttribute(CONNECTIONS_LIST_LOADED_ATTRIBUTE_NAME, LIST_LOADED_ATTRIBUTE_VALUE);
		}
        return connectionsList;
	}
	
	public String getConnectionOverviewHref() {
		final Person person = (Person)getConnectionsTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(person.getId().toString()); 
	}
	
	public String getConnectionThumbnail1Src() {
		final Person person = (Person)getConnectionsTable().getRowData();
		return getPersonService().getProfileThumbnailSrc(person, true);
	}

	public HtmlDataTable getGroupsTable() {
		return groupsTable;
	}

	public void setGroupsTable(HtmlDataTable groupsTable) {
		this.groupsTable = groupsTable;
	}
	
	public List getGroupsList() {		
		HttpServletRequest request = JsfUtils.getRequest();
		if (FacesContext.getCurrentInstance().getRenderResponse()
			&& ! LIST_LOADED_ATTRIBUTE_VALUE.equals(
					request.getAttribute(GROUPS_LIST_LOADED_ATTRIBUTE_NAME))) {	
				groupsList =  getGroupService().findPersonGroupsWhereOwnerOrAdministratorOrMemberList(personId, null, 0, 0);	
				request.setAttribute(GROUPS_LIST_LOADED_ATTRIBUTE_NAME, LIST_LOADED_ATTRIBUTE_VALUE);
		}
        return groupsList;
	}
	
	public String getGroupOverviewHref() {
		final Group group = (Group)getGroupsTable().getRowData();
		return JsfUtils.getFullUrl(PagesURL.GROUP_OVERVIEW, PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID, group.getId().toString());	
	}
	
	public String getGroupThumbnail1Src() {
		final Group group = (Group)getGroupsTable().getRowData();
		return getGroupService().getGroupThumbnail1Src(group, true);
	}
	
	public boolean isShowConnectionsAndGroups() {
		return true;
	}
	
}

package com.pferrot.lendity.need;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.dao.NeedDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.dao.bean.NeedDaoQueryBean;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemConsts;
import com.pferrot.lendity.item.ObjektService;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.model.ItemCategory;
import com.pferrot.lendity.model.ItemVisibility;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.model.Objekt;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.need.exception.NeedException;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.HtmlUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.ListValueUtils;
import com.pferrot.security.SecurityUtils;

public class NeedService extends ObjektService {
	
	private final static Log log = LogFactory.getLog(NeedService.class);
	
	private NeedDao needDao;
	
	public NeedDao getNeedDao() {
		return needDao;
	}

	public void setNeedDao(NeedDao needDao) {
		this.needDao = needDao;
	}

	public Need findNeed(final Long pNeedId) {
		return needDao.findNeed(pNeedId);
	}

	/**
	 * Returns 5 random public needs in the area of pOriginLatitude/pOriginLongitude.
	 * 
	 * @return
	 */
	public List<Need> findRandomNeedsHomepage() {
		final NeedDaoQueryBean needQuery = new NeedDaoQueryBean();
		needQuery.setOwnerEnabled(Boolean.TRUE);
		needQuery.setOrderBy("random");
		needQuery.setMaxResults(5);
		needQuery.setFulfilled(Boolean.FALSE);
		if (SecurityUtils.isLoggedIn()) {
			// All connections.
			final Long[] connectionsIds = getPersonService().getCurrentPersonConnectionIds(null);
			needQuery.setOwnerIds(connectionsIds);
			// Exclude myself.
			needQuery.setOwnerIdsToExcludeForVisibilityIdsToForce(ListValueUtils.getIdsArray(PersonUtils.getCurrentPersonId()));
			needQuery.setVisibilityIds(getConnectionsAndPublicVisibilityIds());
			needQuery.setVisibilityIdsToForce(ListValueUtils.getIdsArray(getPublicVisibilityId()));
			needQuery.setGroupIds(getGroupService().getCurrentPersonGroupIds());
		}
		// When not logged in - only show public items.
		else {
			needQuery.setVisibilityIds(new Long[]{getPublicVisibilityId()});
		}
		return needDao.findNeedsList(needQuery);
	}
	
	public List<Need> findRandomNeedsHomepage(final Double pOriginLatitude, final Double pOriginLongitude) {
		return findNeedsHomepage(pOriginLatitude, pOriginLongitude, "random", Boolean.TRUE);
	}
	
	public List<Need> findLatestNeedsHomepage(final Double pOriginLatitude, final Double pOriginLongitude) {
		return findNeedsHomepage(pOriginLatitude, pOriginLongitude, "creationDate", Boolean.FALSE);
	}

	/**
	 * Returns 5 needs in the area of pOriginLatitude/pOriginLongitude.
	 * First we look in the a distance of 2 kilometers to search for really close needs.
	 * If there is less that 5 needs, then we look up to 20 km, then up to 100 km and finally distance.
	 * 
	 * @param pOriginLatitude
	 * @param pOriginLongitude
	 * @param pOrderBy
	 * @param pOrderByAscending
	 * @return
	 */
	private List<Need> findNeedsHomepage(final Double pOriginLatitude, final Double pOriginLongitude, final String pOrderBy, final Boolean pOrderByAscending) {
		CoreUtils.assertNotNull(pOriginLatitude);
		CoreUtils.assertNotNull(pOriginLongitude);
		
		final int maxResults = 5;
		
		final NeedDaoQueryBean needQuery = new NeedDaoQueryBean();
		needQuery.setOwnerEnabled(Boolean.TRUE);
		needQuery.setOrderBy(pOrderBy);
		needQuery.setOrderByAscending(pOrderByAscending);
		needQuery.setMaxResults(maxResults);
		needQuery.setFulfilled(Boolean.FALSE);
		if (SecurityUtils.isLoggedIn()) {
			// All connections.
			final Long[] connectionsIds = getPersonService().getCurrentPersonConnectionIds(null);
			needQuery.setOwnerIds(connectionsIds);
			// Exclude myself.
			needQuery.setOwnerIdsToExcludeForVisibilityIdsToForce(ListValueUtils.getIdsArray(PersonUtils.getCurrentPersonId()));
			needQuery.setVisibilityIds(getConnectionsAndPublicVisibilityIds());
			needQuery.setVisibilityIdsToForce(ListValueUtils.getIdsArray(getPublicVisibilityId()));
			needQuery.setGroupIds(getGroupService().getCurrentPersonGroupIds());
		}
		// When not logged in - only show public items.
		else {
			needQuery.setVisibilityIds(new Long[]{getPublicVisibilityId()});
		}
		
		// Try 2km, then 20, then 100, then unlimited.
		needQuery.setMaxDistanceKm(new Double(2));
		needQuery.setOriginLatitude(pOriginLatitude);
		needQuery.setOriginLongitude(pOriginLongitude);
		ListWithRowCount lwrc = needDao.findNeeds(needQuery);
		
		// Try 20km.
		if (lwrc.getRowCount() < maxResults) {
			needQuery.setMaxDistanceKm(new Double(20));
			lwrc = needDao.findNeeds(needQuery);
		}
		else {
			return lwrc.getList();
		}
		
		// Try 100km.
		if (lwrc.getRowCount() < maxResults) {
			needQuery.setMaxDistanceKm(new Double(100));
			lwrc = needDao.findNeeds(needQuery);
		}
		else {
			return lwrc.getList();
		}
		
		// Unlimited distance.
		if (lwrc.getRowCount() < maxResults) {
			needQuery.setMaxDistanceKm(null);
			lwrc = needDao.findNeeds(needQuery);
		}
		else {
			return lwrc.getList();
		}
		
		return lwrc.getList();
	}
	
	public ListWithRowCount findMyNeeds(final String pTitle, final Long pCategoryId, final Long pVisibilityId, final Boolean pFulfilled,
			final String pOrderBy, final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
		final Long currentPersonId = PersonUtils.getCurrentPersonId();
		CoreUtils.assertNotNull(currentPersonId);
		final Long[] personIds = new Long[]{currentPersonId};
		
		final NeedDaoQueryBean needDaoQueryBean = new NeedDaoQueryBean();
		needDaoQueryBean.setFulfilled(pFulfilled);
		needDaoQueryBean.setOwnerIds(personIds);
		needDaoQueryBean.setOwnerEnabled(Boolean.TRUE);
		needDaoQueryBean.setTitle(pTitle);
		needDaoQueryBean.setVisibilityIds(getVisibilityIds(pVisibilityId));
		needDaoQueryBean.setCategoryIds(getCategoryIds(pCategoryId));
		needDaoQueryBean.setOrderBy(pOrderBy);
		needDaoQueryBean.setOrderByAscending(pOrderByAscending);
		needDaoQueryBean.setFirstResult(pFirstResult);
		needDaoQueryBean.setMaxResults(pMaxResults);
		
		return needDao.findNeeds(needDaoQueryBean);
	}

	public ListWithRowCount findNeeds(final String pTitle, final Long pCategoryId, final Long pOwnerType,
			final Double pMaxDistanceInKm, final Boolean pFulfilled, final String pOrderBy, final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
				
		final NeedDaoQueryBean needDaoQueryBean = new NeedDaoQueryBean();
		needDaoQueryBean.setFulfilled(pFulfilled);
		if (SecurityUtils.isLoggedIn()) {
			// All connections.
			final Long[] connectionsIds = getPersonService().getCurrentPersonConnectionIds(null);
			needDaoQueryBean.setOwnerIds(connectionsIds);
			// Exclude myself.
			needDaoQueryBean.setOwnerIdsToExcludeForVisibilityIdsToForce(ListValueUtils.getIdsArray(PersonUtils.getCurrentPersonId()));
			needDaoQueryBean.setVisibilityIds(getConnectionsAndPublicVisibilityIds());
			if (!ItemConsts.OWNER_TYPE_CONNECTIONS.equals(pOwnerType)) {
				needDaoQueryBean.setVisibilityIdsToForce(ListValueUtils.getIdsArray(getPublicVisibilityId()));
				needDaoQueryBean.setGroupIds(getGroupService().getCurrentPersonGroupIds());
			}
		}
		// When not logged in - only show public items.
		else {
			needDaoQueryBean.setVisibilityIds(new Long[]{getPublicVisibilityId()});
		}
		needDaoQueryBean.setOwnerEnabled(Boolean.TRUE);
		needDaoQueryBean.setTitle(pTitle);
		needDaoQueryBean.setCategoryIds(getCategoryIds(pCategoryId));
		
		final Double latitude = PersonUtils.getCurrentPersonAddressHomeLatitude();
		final Double longitude = PersonUtils.getCurrentPersonAddressHomeLongitude();	
		needDaoQueryBean.setOriginLatitude(latitude);
		needDaoQueryBean.setOriginLongitude(longitude);
		needDaoQueryBean.setMaxDistanceKm(pMaxDistanceInKm);
		if (pMaxDistanceInKm != null &&
				(latitude == null || longitude == null)) {
			throw new RuntimeException("Can only search by distance if geolocation is available.");			
		}
		
		needDaoQueryBean.setOrderBy(pOrderBy);
		needDaoQueryBean.setOrderByAscending(pOrderByAscending);
		needDaoQueryBean.setFirstResult(pFirstResult);
		needDaoQueryBean.setMaxResults(pMaxResults);
		
		return needDao.findNeeds(needDaoQueryBean);
	}

public ListWithRowCount findPersonNeeds(final Long pOwnerId, final String pTitle, final Long pCategoryId, final Boolean pFulfilled,
			final String pOrderBy, final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
		
		CoreUtils.assertNotNull(pOwnerId);
		
		final NeedDaoQueryBean needQuery = new NeedDaoQueryBean();
		
		needQuery.setFulfilled(pFulfilled);
		needQuery.setOwnerIds(getIds(pOwnerId));
		needQuery.setOwnerEnabled(Boolean.TRUE);
		needQuery.setTitle(pTitle);
		needQuery.setCategoryIds(getCategoryIds(pCategoryId));
		
		if (SecurityUtils.isLoggedIn() &&
		    getPersonService().isConnection(pOwnerId, PersonUtils.getCurrentPersonId())) {		
			needQuery.setVisibilityIds(getConnectionsAndPublicVisibilityIds());
		}
		// When not logged in - only show public items.
		else {
			needQuery.setVisibilityIds(new Long[]{getPublicVisibilityId()});
		}		
		needQuery.setOrderBy(pOrderBy);
		needQuery.setOrderByAscending(pOrderByAscending);
		needQuery.setFirstResult(pFirstResult);
		needQuery.setMaxResults(pMaxResults);
		
		return needDao.findNeeds(needQuery);
	}

	public ListWithRowCount findGroupNeeds(final Long pGroupId, final String pTitle, final Long pCategoryId, final Boolean pFulfilled, 
			final String pOrderBy, final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
		
		CoreUtils.assertNotNull(pGroupId);
		
		final NeedDaoQueryBean needQuery = new NeedDaoQueryBean();
		needQuery.setFulfilled(pFulfilled);
		
		needQuery.setGroupIds(getIds(pGroupId));
		needQuery.setOwnerEnabled(Boolean.TRUE);
		needQuery.setTitle(pTitle);
		needQuery.setCategoryIds(getCategoryIds(pCategoryId));
		
		needQuery.setOrderBy(pOrderBy);
		needQuery.setOrderByAscending(pOrderByAscending);
		needQuery.setFirstResult(pFirstResult);
		needQuery.setMaxResults(pMaxResults);
		
		return needDao.findNeeds(needQuery);
	}

	public ListWithRowCount findMyLatestConnectionsNeeds() {
		Long[] connectionsIds = getPersonService().getCurrentPersonConnectionIds(null);
		if (connectionsIds == null || connectionsIds.length == 0) {
			return ListWithRowCount.emptyListWithRowCount();
		}
		
		final NeedDaoQueryBean needDaoQueryBean = new NeedDaoQueryBean();
		needDaoQueryBean.setOwnerIds(connectionsIds);
		needDaoQueryBean.setOwnerEnabled(Boolean.TRUE);
		needDaoQueryBean.setOrderBy("creationDate");
		needDaoQueryBean.setOrderByAscending(Boolean.FALSE);
		needDaoQueryBean.setFirstResult(0);
		needDaoQueryBean.setMaxResults(5);
		
		return needDao.findNeeds(needDaoQueryBean);
	}

	/**
	 * Returns new needs in the area (20km) of the user.
	 * 
	 * @param pPerson
	 * @param pDate
	 * @return
	 */
	public ListWithRowCount findPersonNeedsUpdatesSince(final Person pPerson, final Date pDate) {
		Long[] connectionsIds = getPersonService().getPersonConnectionIds(pPerson, null);
		Long[] groupIds = getGroupService().getPersonGroupsIds(pPerson, null);
		
		final NeedDaoQueryBean needDaoQueryBean = new NeedDaoQueryBean();
		needDaoQueryBean.setOwnerIds(connectionsIds);
		needDaoQueryBean.setOwnerEnabled(Boolean.TRUE);
		
		// Exclude myself.
		needDaoQueryBean.setOwnerIdsToExcludeForVisibilityIdsToForce(ListValueUtils.getIdsArray(pPerson.getId()));
		needDaoQueryBean.setVisibilityIds(getConnectionsAndPublicVisibilityIds());
		needDaoQueryBean.setVisibilityIdsToForce(ListValueUtils.getIdsArray(getPublicVisibilityId()));
		needDaoQueryBean.setGroupIds(groupIds);
		
		if (pPerson.isAddressHomeDefined()) {
			needDaoQueryBean.setMaxDistanceKm(Double.valueOf(20.0));
			needDaoQueryBean.setOriginLatitude(pPerson.getAddressHomeLatitude());
			needDaoQueryBean.setOriginLongitude(pPerson.getAddressHomeLongitude());
		}
		
		needDaoQueryBean.setCreationDateMin(pDate);
		needDaoQueryBean.setOrderBy("creationDate");
		needDaoQueryBean.setOrderByAscending(Boolean.FALSE);
		needDaoQueryBean.setFirstResult(0);
		needDaoQueryBean.setMaxResults(5);
		
		return needDao.findNeeds(needDaoQueryBean);
	}
	
	public void deleteNeed(final Long pNeedId) {
		deleteNeed(needDao.findNeed(pNeedId));
	}
	
	public void deleteNeed(final Need pNeed) {
		needDao.deleteNeed(pNeed);
	}
	
	public Long createNeed(final Need pNeed, final boolean pSendEmail) {
		try {
			pNeed.setCreationDate(new Date());
			final Long result = needDao.createNeed(pNeed);
			final Long visibilityId = pNeed.getVisibility().getId();
			// Only send notifications if they can see.
			if (pSendEmail &&
					(visibilityId.equals(getPublicVisibilityId()) ||
				     visibilityId.equals(getConnectionsVisibilityId()))) {
				sendNotificationToAllConnections(pNeed);
			}
			return result;
		}
		catch (NeedException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void sendNotificationToAllConnections(final Need pNeed) throws NeedException {
		final List<Person> connections = getPersonService().findConnectionsRecevingNeedsNotificationsList(pNeed.getOwner().getId(), null, 0, 0);
		for (Person connection: connections) {
			sendNotificationToOneConnection(pNeed, connection);
		}
	}
	
	private void sendNotificationToOneConnection(final Need pNeed, final Person pConnection) throws NeedException {
		CoreUtils.assertNotNull(pNeed);
		CoreUtils.assertNotNull(pConnection);
		try {	
			// Send email (will actually create a JMS message, i.e. it is async).
			Map<String, String> objects = new HashMap<String, String>();
			objects.put("connectionFirstName", pConnection.getFirstName());
			objects.put("connectionLastName", pConnection.getLastName());
			objects.put("connectionDisplayName", pConnection.getDisplayName());
			objects.put("requesterFirstName", pNeed.getOwner().getFirstName());
			objects.put("requesterLastName", pNeed.getOwner().getLastName());
			objects.put("requesterDisplayName", pNeed.getOwner().getDisplayName());
			objects.put("needTitle", pNeed.getTitle());
			objects.put("needUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(),
					PagesURL.NEED_OVERVIEW,
					PagesURL.NEED_OVERVIEW_PARAM_NEED_ID,
					pNeed.getId().toString()));
			objects.put("itemAddUrl",  JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(),
					PagesURL.ITEM_ADD,
					PagesURL.ITEM_ADD_PARAM_NEED_ID,
					pNeed.getId().toString()));
			objects.put("signature", Configuration.getSiteName());
			objects.put("siteName", Configuration.getSiteName());
			objects.put("siteUrl", Configuration.getRootURL());
			objects.put("profileUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.MY_PROFILE));
			
			// TODO: localization
			final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/need/notification/fr";
			
			Map<String, String> to = new HashMap<String, String>();
			to.put(pConnection.getEmail(), pConnection.getEmail());
			
			Map<String, String> inlineResources = new HashMap<String, String>();
			inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.png");
			
			getMailManager().send(Configuration.getNoReplySenderName(), 
					         Configuration.getNoReplyEmailAddress(),
					         to,
					         null, 
					         null,
					         Configuration.getSiteName() + ": recherch� par un ami",
					         objects, 
					         velocityTemplateLocation,
					         inlineResources);		
		} 
		catch (Exception e) {
			throw new NeedException(e);
		}
		
	}

	public Long createNeed(final Need pNeed, final List<Long> pCategoriesIds, final Long pVisibilityId, final boolean pSendEmail) {
		pNeed.setVisibility((ItemVisibility) ListValueUtils.getListValueFromId(pVisibilityId, getListValueDao()));
		return createNeed(pNeed, pCategoriesIds, pSendEmail);
	}

	public Long createNeed(final Need pNeed, final List<Long> pCategoriesIds, final Long pVisibilityId,
			final List<Long> pAuthorizedGroupsIds, final boolean pSendEmail) {
		if (pAuthorizedGroupsIds != null) {
			Set<Group> groups = new HashSet<Group>();
			for (Long groupId: pAuthorizedGroupsIds) {
				final Group group = getGroupService().findGroup(groupId);
				// AC check.
				getGroupService().assertCurrentUserOwnerOrAdministratorOrMemberOfGroup(group);
				groups.add(group);
			}
			pNeed.setGroupsAuthorized(groups);
		}
		return createNeed(pNeed, pCategoriesIds, pVisibilityId, pSendEmail);
	}
	
	public Long createNeed(final Need pNeed, final List<Long> pCategoriesIds, final boolean pSendEmail) {
		Set<ItemCategory> categories = new HashSet<ItemCategory>();
		if (pCategoriesIds != null) {			
			for (Long categoryId: pCategoriesIds) {
				final ItemCategory catgory = (ItemCategory) ListValueUtils.getListValueFromId(categoryId, getListValueDao());
				categories.add(catgory);
			}					
		}
		pNeed.setCategories(categories);
		return createNeed(pNeed, pSendEmail);
	}
	
	public void updateNeed(final Need pNeed) {
		needDao.updateNeed(pNeed);
	}

	public void updateNeed(final Need pNeed, final List<Long> pCategoriesIds) {
		assertCurrentUserAuthorizedToEdit(pNeed);
		Set<ItemCategory> categories = new HashSet<ItemCategory>();
		if (pCategoriesIds != null) {			
			for (Long categoryId: pCategoriesIds) {
				final ItemCategory catgory = (ItemCategory) ListValueUtils.getListValueFromId(categoryId, getListValueDao());
				categories.add(catgory);
			}					
		}
		pNeed.setCategories(categories);
		updateNeed(pNeed);
	}

	public void updateNeed(final Need pNeed, final List<Long> pCategoriesIds, final Long pVisibilityId) {
		assertCurrentUserAuthorizedToEdit(pNeed);
		pNeed.setVisibility((ItemVisibility) ListValueUtils.getListValueFromId(pVisibilityId, getListValueDao()));
		updateNeed(pNeed, pCategoriesIds);
	}
	
	public void updateNeed(final Need pNeed, final List<Long> pCategoriesIds, final Long pVisibilityId, final List<Long> pAuthorizedGroupsIds) {
		assertCurrentUserAuthorizedToEdit(pNeed);
		if (pAuthorizedGroupsIds != null) {
			Set<Group> groups = new HashSet<Group>();
			for (Long groupId: pAuthorizedGroupsIds) {
				final Group group = getGroupService().findGroup(groupId);
				// AC check.
				getGroupService().assertCurrentUserOwnerOrAdministratorOrMemberOfGroup(group);
				groups.add(group);
			}
			pNeed.setGroupsAuthorized(groups);
		}
		updateNeed(pNeed, pCategoriesIds, pVisibilityId);
	}

	@Override
	public String getFacebookLikeImageSrc(final Objekt pObjekt,
			 final boolean pAuthorizeDocumentAccess, final HttpSession pSession, final String pUrlPrefix) {
		return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, NeedConsts.FACEBOOK_LIKE_DEFAULT_IMAGE_URL);
	}
	
	public List<Need> findAllNeedsForPerson(final Long pPersonId) {
		CoreUtils.assertNotNull(pPersonId);
		
		final NeedDaoQueryBean queryBean = new NeedDaoQueryBean();
		queryBean.setOwnerIds(new Long[]{pPersonId});
		queryBean.setFirstResult(0);
		queryBean.setMaxResults(0);
		
		return needDao.findNeedsList(queryBean);		
	}
	
	public void updateNeedsRemoveGroupAuthorized(final Long pNeedOwnerId, final Long pGroupId) {
		CoreUtils.assertNotNull(pNeedOwnerId);
		CoreUtils.assertNotNull(pGroupId);
		
		final Group group = getGroupService().findGroup(pGroupId);
		
		final List<Need> needs = findAllNeedsForPerson(pNeedOwnerId);
		
		for (Need need: needs) {
			if (need.getGroupsAuthorized().contains(group)) {
				assertCurrentUserAuthorizedToEdit(need);
				need.removeGroupAuthorized(group);
				updateNeed(need);
			}
		}		
	}
	
	public void updateNeedsRemoveGroupAuthorized(final Long pGroupId) {
		CoreUtils.assertNotNull(pGroupId);
		
		final Group group = getGroupService().findGroup(pGroupId);
		
		getGroupService().assertCurrentUserAuthorizedToDelete(group);
		
		final Set<Need> needs = group.getNeeds();
		
		if (needs != null) {
			for (Need need: needs) {
				if (need.getGroupsAuthorized().contains(group)) {
					need.removeGroupAuthorized(group);
					updateNeed(need);
				}
			}		
		}
	}
	
	/**
	 * Replaces all occurrences of strings like {n123} with an href link to the
	 * corresponding object, e.g.:
	 * 
	 * <a href="http://www.lendity.ch/need/needOverview.faces?needID=123" target="_blank">The need title</a>
	 * 
	 * If pPerson is not authorized to view the need, a standard error text is used instead.
	 * 
	 * @param pText
	 * @param pPerson
	 * @return
	 */
	public String processNeedHref(final String pText, final Person pPerson) {
		return processNeedHrefInternal(pText, pPerson, true);
	}
	
	public String processNeedNoHref(final String pText, final Person pPerson) {
		return processNeedHrefInternal(pText, pPerson, false);
	}
	
	private String processNeedHrefInternal(final String pText, final Person pPerson, final boolean pWithHref) {
		if (StringUtils.isNullOrEmpty(pText)) {
			return pText;
		}
		
		final String regex = "\\{n[0-9]+\\}";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(pText);

		final StringBuffer result = new StringBuffer();
		while (m.find()) {
			try {
				final String text = m.group();
				final Long needId = Long.parseLong(text.substring(2, text.length() - 1));
				final Need need = findNeed(needId);
				assertUserAuthorizedToView(pPerson, need);
				if (pWithHref) {
					m.appendReplacement(result, getHrefLinkToNeed(need, true));
				}
				else {
					m.appendReplacement(result, need.getTitle());
				}
			}
			catch (Exception e) {
				final Locale locale = I18nUtils.getDefaultLocale();
				final String s = I18nUtils.getMessageResourceString("comment_replacementError", locale);
				m.appendReplacement(result, s);	
			}
			
			
		}
		m.appendTail(result);	
		return result.toString();
	}
	
	private String getHrefLinkToNeed(final Need pNeed, final boolean pOpenInNewWindow) {
		return "<a href=\"" + 
			JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.NEED_OVERVIEW, PagesURL.NEED_OVERVIEW_PARAM_NEED_ID, pNeed.getId().toString()) +
			"\"" +
			(pOpenInNewWindow?" target=\"_blank\"":"") +
			">" + 
			HtmlUtils.escapeHtmlAndReplaceCr(pNeed.getTitle()) + 
			"</a>";
	}
	
	@Override
	public String getThumbnail1Src(final Objekt pObjekt,
			 final boolean pAuthorizeDocumentAccess, final HttpSession pSession, final String pUrlPrefix, final Long pPreferredCategoryId) {
		final Need need = (Need)pObjekt;
		if (Boolean.TRUE.equals(need.getFulfilled())) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, NeedConsts.NEED_FULFILLED_SMALL_IMAGE_URL);
		}
		else {
			return super.getThumbnail1Src(pObjekt, pAuthorizeDocumentAccess, pSession, pUrlPrefix, pPreferredCategoryId);
		}
	}	
}

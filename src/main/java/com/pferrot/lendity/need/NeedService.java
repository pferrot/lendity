package com.pferrot.lendity.need;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.connectionrequest.exception.ConnectionRequestException;
import com.pferrot.lendity.dao.NeedDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.item.ObjectService;
import com.pferrot.lendity.lendrequest.exception.LendRequestException;
import com.pferrot.lendity.model.ItemCategory;
import com.pferrot.lendity.model.LendRequest;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.need.exception.NeedException;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.ListValueUtils;

public class NeedService extends ObjectService {
	
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
	
	public ListWithRowCount findMyNeeds(final String pTitle, final Long pCategoryId, final String pOrderBy,
			final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
		final Long currentPersonId = PersonUtils.getCurrentPersonId();
		CoreUtils.assertNotNull(currentPersonId);
		final Long[] personIds = new Long[]{currentPersonId};
		
		return needDao.findNeeds(personIds, Boolean.TRUE, pTitle, getCategoryIds(pCategoryId), null, pOrderBy, pOrderByAscending, pFirstResult, pMaxResults);
	}

	public ListWithRowCount findMyConnectionsNeeds(final Long pConnectionId, final String pTitle, final Long pCategoryId, final String pOrderBy,
			final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
		Long[] connectionsIds = getPersonService().getCurrentPersonConnectionIds(pConnectionId);
		if (connectionsIds == null || connectionsIds.length == 0) {
			return ListWithRowCount.emptyListWithRowCount();
		}
		return needDao.findNeeds(connectionsIds, Boolean.TRUE, pTitle, getCategoryIds(pCategoryId), null, pOrderBy, pOrderByAscending, pFirstResult, pMaxResults);
	}

	public ListWithRowCount findMyLatestConnectionsNeeds() {
		Long[] connectionsIds = getPersonService().getCurrentPersonConnectionIds(null);
		if (connectionsIds == null || connectionsIds.length == 0) {
			return ListWithRowCount.emptyListWithRowCount();
		}
		return needDao.findNeeds(connectionsIds, Boolean.TRUE, null, null, null, "creationDate", Boolean.FALSE, 0, 5);
	}

	public ListWithRowCount findPersonLatestConnectionsNeedsSince(final Person pPerson, final Date pDate) {
		Long[] connectionsIds = getPersonService().getPersonConnectionIds(pPerson, null);
		if (connectionsIds == null || connectionsIds.length == 0) {
			return ListWithRowCount.emptyListWithRowCount();
		}
		return needDao.findNeeds(connectionsIds, Boolean.TRUE, null, null, pDate, "creationDate", Boolean.FALSE, 0, 5);
	}
	
	public void deleteNeed(final Long pNeedId) {
		deleteNeed(needDao.findNeed(pNeedId));
	}
	
	public void deleteNeed(final Need pNeed) {
		needDao.deleteNeed(pNeed);
	}
	
	public Long createNeed(final Need pNeed) {
		try {
			pNeed.setCreationDate(new Date());
			final Long result = needDao.createNeed(pNeed);
			sendNotificationToAllConnections(pNeed);
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
			objects.put("requesterFirstName", pNeed.getOwner().getFirstName());
			objects.put("requesterLastName", pNeed.getOwner().getLastName());
			objects.put("needTitle", pNeed.getTitle());
			objects.put("needUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(),
					PagesURL.NEED_OVERVIEW,
					PagesURL.NEED_OVERVIEW_PARAM_NEED_ID,
					pNeed.getId().toString()));
			objects.put("itemAddUrl",  JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(),
					PagesURL.INTERNAL_ITEM_ADD,
					PagesURL.INTERNAL_ITEM_ADD_PARAM_NEED_ID,
					pNeed.getId().toString()));
			objects.put("signature", Configuration.getSiteName());
			objects.put("siteName", Configuration.getSiteName());
			objects.put("siteUrl", Configuration.getRootURL());
			
			// TODO: localization
			final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/need/notification/fr";
			
			Map<String, String> to = new HashMap<String, String>();
			to.put(pConnection.getEmail(), pConnection.getEmail());
			
			Map<String, String> inlineResources = new HashMap<String, String>();
			inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.gif");
			
			getMailManager().send(Configuration.getNoReplySenderName(), 
					         Configuration.getNoReplyEmailAddress(),
					         to,
					         null, 
					         null,
					         Configuration.getSiteName() + ": recherché par un ami",
					         objects, 
					         velocityTemplateLocation,
					         inlineResources);		
		} 
		catch (Exception e) {
			throw new NeedException(e);
		}
		
	}

	public Long createNeedWithCategory(final Need pNeed, final Long pCategoryId) {
		pNeed.setCategory((ItemCategory) ListValueUtils.getListValueFromId(pCategoryId, getListValueDao()));
		return createNeed(pNeed);
	}
	
	public void updateNeed(final Need pNeed) {
		needDao.updateNeed(pNeed);
	}

	public void updateNeedWithCategory(final Need pNeed, final Long pCategoryId) {
		assertCurrentUserAuthorizedToEdit(pNeed);
		pNeed.setCategory((ItemCategory) ListValueUtils.getListValueFromId(pCategoryId, getListValueDao()));
		updateNeed(pNeed);
	}
	
	/////////////////////////////////////////////////////////
	// Access control
	
	public boolean isCurrentUserAuthorizedToView(final Need pNeed) {
		CoreUtils.assertNotNull(pNeed);
		if (isCurrentUserAuthorizedToEdit(pNeed)) {
			return true;
		}
		return pNeed.getOwner() != null &&
			pNeed.getOwner().getConnections() != null &&
			pNeed.getOwner().getConnections().contains(getCurrentPerson());
	}
	
	public void assertCurrentUserAuthorizedToView(final Need pNeed) {
		if (!isCurrentUserAuthorizedToView(pNeed)) {
			throw new SecurityException("Current user is not authorized to view need");
		}
	}

	public boolean isCurrentUserAuthorizedToEdit(final Need pNeed) {
		CoreUtils.assertNotNull(pNeed);
		final Person currentPerson = getCurrentPerson();
		if (currentPerson == null) {
			return false;
		}
		if (currentPerson.getUser() != null &&
		    currentPerson.getUser().isAdmin()) {
			return true;
		}
		return currentPerson.equals(pNeed.getOwner());
	}

	public void assertCurrentUserAuthorizedToEdit(final Need pNeed) {
		if (!isCurrentUserAuthorizedToEdit(pNeed)) {
			throw new SecurityException("Current user is not authorized to edit need");
		}
	}

	public boolean isCurrentUserAuthorizedToAdd() {
		final Person currentPerson = getCurrentPerson();
		if (currentPerson != null && currentPerson.getUser() != null) {
			return true;
		}
		return false;
	}

	public void assertCurrentUserAuthorizedToAdd() {
		if (!isCurrentUserAuthorizedToAdd()) {
			throw new SecurityException("Current user is not authorized to add need");
		}
	}

	public boolean isCurrentUserAuthorizedToDelete(final Need pNeed) {
		return isCurrentUserAuthorizedToEdit(pNeed);
	}

	public void assertCurrentUserAuthorizedToDelete(final Need pNeed) {
		if (!isCurrentUserAuthorizedToDelete(pNeed)) {
			throw new SecurityException("Current user is not authorized to delete need");
		}
	}
	
	public boolean isCurrentUserOwner(final Need pNeed) {
		final Person currentPerson = getCurrentPerson();
		if (currentPerson == null) {
			return false;
		}
		return currentPerson.equals(pNeed.getOwner());
	}

}

package com.pferrot.lendity.groupjoinrequest;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.dao.GroupJoinRequestDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.dao.bean.GroupJoinRequestDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.group.GroupService;
import com.pferrot.lendity.groupjoinrequest.exception.GroupJoinRequestException;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.model.GroupJoinRequest;
import com.pferrot.lendity.model.GroupJoinRequestResponse;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.security.SecurityUtils;

public class GroupJoinRequestService {
	
	private final static Log log = LogFactory.getLog(GroupJoinRequestService.class);
	
	private GroupJoinRequestDao groupJoinRequestDao;
	private GroupService groupService;
	private PersonService personService;
	private MailManager mailManager;
	private ListValueDao listValueDao;

	public GroupJoinRequestDao getGroupJoinRequestDao() {
		return groupJoinRequestDao;
	}

	public void setGroupJoinRequestDao(GroupJoinRequestDao groupJoinRequestDao) {
		this.groupJoinRequestDao = groupJoinRequestDao;
	}

	public ListValueDao getListValueDao() {
		return listValueDao;
	}

	public void setListValueDao(ListValueDao listValueDao) {
		this.listValueDao = listValueDao;
	}

	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public MailManager getMailManager() {
		return mailManager;
	}

	public void setMailManager(MailManager mailManager) {
		this.mailManager = mailManager;
	}

	/**
	 * Create a group join request using the current user as requester.
	 *
	 * @param pGroup
	 * @return
	 * @throws GroupJoinRequestException
	 */
	public Long createGroupJoinRequestFromCurrentUser(final Group pGroup, final String pPassword) throws GroupJoinRequestException {
		if (pGroup.isPasswordProtected() && !pGroup.getPassword().equals(pPassword)) {
			throw new SecurityException("Password is not correct");
		}
		return createGroupJoinRequest(pGroup, getPersonService().getCurrentPerson());
	}

	/**
	 * Create a group join request using the current user as requester.
	 *
	 * @param pGroupId
	 * @param pPassword
	 * @return
	 * @throws GroupJoinRequestException
	 */
	public Long createGroupJoinRequestFromCurrentUser(final Long pGroupId, final String pPassword) throws GroupJoinRequestException {		
		return createGroupJoinRequestFromCurrentUser(getGroupService().findGroup(pGroupId), pPassword);
	}
	
	/**
	 * This operation will send an email to the owner and all admins of the group
	 * informing them that the user wants to join the group.

	 * @param pGroup
	 * @param pRequester
	 * @return
	 * @throws GroupJoinRequestException 
	 */
	public Long createGroupJoinRequest(final Group pGroup, final Person pRequester) throws GroupJoinRequestException {
		try {
			if (! isGroupJoinRequestAllowed(pGroup, pRequester)) {
				throw new GroupJoinRequestException("Group join request not allowed.");
			}

			final GroupJoinRequest groupJoinRequest = new GroupJoinRequest();
			groupJoinRequest.setGroup(pGroup);
			groupJoinRequest.setRequester(pRequester);
			groupJoinRequest.setRequestDate(new Date());
			
			Long groupJoinRequestId = getGroupJoinRequestDao().createGroupJoinRequest(groupJoinRequest);
				
			// Send email (will actually create a JMS message, i.e. it is async).
			Map<String, String> objects = new HashMap<String, String>();
			objects.put("groupTitle", pGroup.getTitle());
			objects.put("requesterDisplayName", pRequester.getDisplayName());
			objects.put("requesterFirstName", pRequester.getFirstName());
			objects.put("requesterLastName", pRequester.getLastName());
			objects.put("signature", Configuration.getSiteName());
			objects.put("siteName", Configuration.getSiteName());
			objects.put("siteUrl", Configuration.getRootURL());
			objects.put("groupRequestsUrl", Configuration.getRootURL() + PagesURL.MY_PENDING_GROUP_JOIN_REQUESTS_LIST);
			
			// TODO: localization
			final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/groupjoinrequest/ask/fr";
			
			Map<String, String> to = new HashMap<String, String>();
			if (pGroup.getOwner() != null && pGroup.getOwner().isEnabled()) {
				to.put(pGroup.getOwner().getEmail(), pGroup.getOwner().getEmail());
			}
			final Set<Person> admins = pGroup.getAdministrators();
			for (Person admin: admins) {
				if (admin.isEnabled()) {
					to.put(admin.getEmail(), admin.getEmail());
				}
			}
			
			Map<String, String> inlineResources = new HashMap<String, String>();
			inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.png");
			
			
			// Multiple recipients is not implemented. Plus it is good to not show all admin email to all recipients anyway.
			for(String adminMail: to.keySet()) {
				Map<String, String> to2 = new HashMap<String, String>();
				to2.put(adminMail, adminMail);
				
				mailManager.send(Configuration.getNoReplySenderName(), 
						         Configuration.getNoReplyEmailAddress(),
						         to2,
						         null, 
						         null,
						         Configuration.getSiteName() + ": demande d'adhésion au groupe " + pGroup.getTitle(),
						         objects, 
						         velocityTemplateLocation,
						         inlineResources);		
			}
			
			return groupJoinRequestId;
		} 
		catch (Exception e) {
			throw new GroupJoinRequestException(e);
		}
	}
	
	public boolean isGroupJoinRequestAllowedFromCurrentUser(final Group pGroup) throws GroupJoinRequestException {
		return isGroupJoinRequestAllowed(pGroup, getPersonService().getCurrentPerson());		
	}
	
	public boolean isGroupJoinRequestAllowed(final Group pGroup, final Person pRequester) throws GroupJoinRequestException {
		// Requester must be active applications user.
		if (! PersonUtils.isActiveApplicationUser(pRequester)) {
			if (log.isDebugEnabled()) {
				log.debug("Requester is not an active app user: " + pRequester);
			}
			return false;
		}
		
		// Group must not be "free to join".
		if (! Boolean.TRUE.equals(pGroup.getValidateMembership())) {
			if (log.isDebugEnabled()) {
				log.debug("Group is free to join: " + pGroup);
			}
			return false;			
		}
				
		// Requester is banned.
		if (getGroupService().isUserBannedByGroup(pRequester, pGroup)) {
			if (log.isDebugEnabled()) {
				log.debug("Requester (" + pRequester + ") is banned by group (" + pGroup+ ") .");
			}			
			return false;
		}
		
		// Requester already owner or admin or member of group.
		if (getGroupService().isUserOwnerOrAdministratorOrMemberOfGroup(pRequester, pGroup)) {
			if (log.isDebugEnabled()) {
				log.debug("Requester (" + pRequester + ") is already owner or admin or member of group (" + pGroup+ ") .");
			}			
			return false;
		}
		
		// Already a request pending.
		if (isUncompletedGroupJoinRequestAvailable(pGroup, pRequester)) {
			if (log.isDebugEnabled()) {
				log.debug("Already a group join request pending for group (" + pGroup+ ") and requester (" + pRequester + ").");
			}	
			return false;
		}
		
		return true;			
	}
	
	public boolean isUncompletedGroupJoinRequestAvailableFromCurrentPerson(final Group pGroup) {
		return isUncompletedGroupJoinRequestAvailable(pGroup, getPersonService().getCurrentPerson());
	}
	
	/**
	 * Returns true if a there is already an group join request pending.
	 *
	 * @param pGroup
	 * @param pRequester
	 * @return
	 */
	public boolean isUncompletedGroupJoinRequestAvailable(final Group pGroup, final Person pRequester) {
		CoreUtils.assertNotNull(pGroup);
		CoreUtils.assertNotNull(pRequester);

		final Long groupId = pGroup.getId();
		final Long requesterId = pRequester.getId();
		
		final GroupJoinRequestDaoQueryBean queryBean = new GroupJoinRequestDaoQueryBean();
		queryBean.setRequesterIds(new Long[]{requesterId});
		queryBean.setGroupIds(new Long[]{groupId});
		queryBean.setCompleted(Boolean.FALSE);
		
		long nbHits = getGroupJoinRequestDao().countGroupJoinRequests(queryBean);
		
		return nbHits > 0;
	}

	public ListWithRowCount findCurrentUserPendingGroupJoinRequests(
			int firstRow, int rowsPerPage) {
		if (!SecurityUtils.isLoggedIn()) {
			throw new SecurityException("Not logged in");
		}
		final GroupJoinRequestDaoQueryBean queryBean = new GroupJoinRequestDaoQueryBean();
		queryBean.setCompleted(Boolean.FALSE);
		queryBean.setGroupIds(getOwnerAndAdministratorGroupIds(getPersonService().getCurrentPerson()));
		
		return groupJoinRequestDao.findGroupJoinRequests(queryBean);
	}
	
	public long countCurrentUserPendingGroupJoinRequests() {
		if (!SecurityUtils.isLoggedIn()) {
			throw new SecurityException("Not logged in");
		}
		return countUserPendingGroupJoinRequests(getPersonService().getCurrentPerson());
	}
	
	public long countUserPendingGroupJoinRequests(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		
		final GroupJoinRequestDaoQueryBean queryBean = new GroupJoinRequestDaoQueryBean();
		queryBean.setCompleted(Boolean.FALSE);
		queryBean.setGroupIds(getOwnerAndAdministratorGroupIds(pPerson));
		
		return groupJoinRequestDao.countGroupJoinRequests(queryBean);
	}
	
	private Long[] getOwnerAndAdministratorGroupIds(final Person pPerson) {
		final Set<Group> groupsAdmin = pPerson.getGroupsAdministrator();
		final Set<Group> groupsOwner = pPerson.getGroupsOwner();
		
		final Set<Long> groupIds = new HashSet<Long>();
		
		for(Group group: groupsAdmin) {
			groupIds.add(group.getId());
		}
		for(Group group: groupsOwner) {
			groupIds.add(group.getId());
		}
		
		// Trick to "tell" the DAO that this is a user without any
		// group, and not a user with all groups...
		if (groupIds == null || groupIds.isEmpty()) {
			return new Long[]{Long.valueOf(-1)};
		}
		else {
			return (Long[])groupIds.toArray(new Long[groupIds.size()]);
		}
	}
	
	public ListWithRowCount findCurrentUserPendingGroupJoinRequestsOut(
			int firstRow, int rowsPerPage) {
		final GroupJoinRequestDaoQueryBean queryBean = new GroupJoinRequestDaoQueryBean();
		queryBean.setCompleted(Boolean.FALSE);
		queryBean.setRequesterIds(new Long[]{getPersonService().getCurrentPerson().getId()});
		
		return groupJoinRequestDao.findGroupJoinRequests(queryBean);
	}
	
	private void setGroupJoinRequestResponse(final GroupJoinRequest pRequest, final GroupJoinRequestResponse pResponse) throws GroupJoinRequestException {
		CoreUtils.assertNotNull(pRequest);
		CoreUtils.assertNotNull(pResponse);
		
		getGroupService().assertCurrentUserOwnerOrAdministratorOfGroup(pRequest.getGroup());
		
//		final Person connection = pConnectionRequest.getConnection();
		if (pRequest.getResponse() != null) {
			throw new GroupJoinRequestException("Group join request with ID '" + pRequest.getId().toString() + "' already has a response.");
		}
		pRequest.setResponse(pResponse);
		pRequest.setResponseDate(new Date());
		pRequest.setResponseBy(getPersonService().getCurrentPerson());
	}

	private void sendResponseEmail(final GroupJoinRequest pRequest, final String pEmailSubject, final String pTemplateLocation) {
		// Send email (will actually create a JMS message, i.e. it is async).
		Map<String, String> objects = new HashMap<String, String>();
		objects.put("requesterFirstName", pRequest.getRequester().getFirstName());
		objects.put("groupTitle", pRequest.getGroup().getTitle());
		objects.put("groupUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.GROUP_OVERVIEW, PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID, pRequest.getGroup().getId().toString()));
		objects.put("signature", Configuration.getSiteName());
		objects.put("siteName", Configuration.getSiteName());
		objects.put("siteUrl", Configuration.getRootURL());
		
		Map<String, String> to = new HashMap<String, String>();
		to.put(pRequest.getRequester().getEmail(), pRequest.getRequester().getEmail());
	
		Map<String, String> inlineResources = new HashMap<String, String>();
		inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.png");
		
		mailManager.send(Configuration.getNoReplySenderName(), 
						 Configuration.getNoReplyEmailAddress(),
				         to,
				         null, 
				         null,
				         pEmailSubject,
				         objects, 
				         pTemplateLocation,
				         inlineResources);			
	}

	public void updateAcceptGroupJoinRequest(final GroupJoinRequest pGroupJoinRequest) throws GroupJoinRequestException {
		try {
			CoreUtils.assertNotNull(pGroupJoinRequest);

			setGroupJoinRequestResponse(pGroupJoinRequest, (GroupJoinRequestResponse)listValueDao.findListValue(GroupJoinRequestResponse.ACCEPT_LABEL_CODE));

			// If the user is a connection, he cannot be a banned.
			if (pGroupJoinRequest.getGroup().getBannedPersons().contains(pGroupJoinRequest.getRequester())) {
				if (log.isWarnEnabled()) {
					log.warn("'" + pGroupJoinRequest.getRequester() + "' is banned by '" + pGroupJoinRequest.getGroup() + "' before being added as member.");
				}				
				pGroupJoinRequest.getGroup().removeBannedPerson(pGroupJoinRequest.getRequester());
			}

			// Same for reverse link.
//			if (pConnectionRequest.getRequester().getBannedPersons().contains(pConnectionRequest.getConnection())) {
//				if (log.isWarnEnabled()) {
//					log.warn("'" + pConnectionRequest.getConnection() + "' is banned by '" + pConnectionRequest.getRequester() + "' before being added as connection.");
//				}
//				pConnectionRequest.getRequester().removeBannedPerson(pConnectionRequest.getConnection());
//			}
			
			// Add member (will add on reverse link as well).
			pGroupJoinRequest.getGroup().addMember(pGroupJoinRequest.getRequester());
			
			sendResponseEmail(pGroupJoinRequest,
					Configuration.getSiteName() + ": demande d'adhésion au groupe acceptée",
					"com/pferrot/lendity/emailtemplate/groupjoinrequest/accept/fr");

			if (log.isInfoEnabled()) {
				log.info("'" + pGroupJoinRequest.getRequester() + "' is accepted by '" + pGroupJoinRequest.getGroup() + "'.");
			}
		}
		catch (GroupJoinRequestException e) {
			throw e;
		}
		catch (Exception e) {
			throw new GroupJoinRequestException(e);
		}		
	}

	public void updateAcceptGroupJoinRequest(final Long pGroupJoinRequestId) throws GroupJoinRequestException {
		CoreUtils.assertNotNull(pGroupJoinRequestId);
		final GroupJoinRequest request = groupJoinRequestDao.findGroupJoinRequest(pGroupJoinRequestId);

		updateAcceptGroupJoinRequest(request);	
	}

	public void updateRefuseGroupJoinRequest(final GroupJoinRequest pGroupJoinRequest) throws GroupJoinRequestException {
		try {
			CoreUtils.assertNotNull(pGroupJoinRequest);
			
			setGroupJoinRequestResponse(pGroupJoinRequest, (GroupJoinRequestResponse)listValueDao.findListValue(GroupJoinRequestResponse.REFUSE_LABEL_CODE));
			
			sendResponseEmail(pGroupJoinRequest, Configuration.getSiteName() + ": demande d'adhésion au groupe refusée", "com/pferrot/lendity/emailtemplate/groupjoinrequest/refuse/fr");

			if (log.isInfoEnabled()) {
				log.info("'" + pGroupJoinRequest.getRequester() + "' is refused by '" + pGroupJoinRequest.getGroup() + "'.");
			}
		}
		catch (GroupJoinRequestException e) {
			throw e;
		}
		catch (Exception e) {
			throw new GroupJoinRequestException(e);
		}		
	}
	
	public void updateRefuseGroupJoinRequest(final Long pGroupJoinRequestId) throws GroupJoinRequestException {
		CoreUtils.assertNotNull(pGroupJoinRequestId);
		final GroupJoinRequest request = groupJoinRequestDao.findGroupJoinRequest(pGroupJoinRequestId);

		updateRefuseGroupJoinRequest(request);	
	}

	public void updateBanGroupJoinRequest(final GroupJoinRequest pGroupJoinRequest) throws GroupJoinRequestException {
		try {
			CoreUtils.assertNotNull(pGroupJoinRequest);
			
			setGroupJoinRequestResponse(pGroupJoinRequest, (GroupJoinRequestResponse)listValueDao.findListValue(GroupJoinRequestResponse.BAN_LABEL_CODE));

			// If the user is banned, it cannot be a member.
			if (pGroupJoinRequest.getRequester().getGroupsMember().contains(pGroupJoinRequest.getGroup())) {
				pGroupJoinRequest.getGroup().removeMember(pGroupJoinRequest.getRequester());
			}
			
			// If the user is banned, it cannot be an admin.
			if (pGroupJoinRequest.getRequester().getGroupsAdministrator().contains(pGroupJoinRequest.getGroup())) {
				pGroupJoinRequest.getGroup().removeAdministrator(pGroupJoinRequest.getRequester());
			}
			
			if (pGroupJoinRequest.getGroup().getOwner().equals(pGroupJoinRequest.getRequester())) {
				throw new GroupJoinRequestException("Owner cannot be banned");
			}
			
			// Ban connection.
			pGroupJoinRequest.getGroup().addBannedPerson(pGroupJoinRequest.getRequester());
			
			sendResponseEmail(pGroupJoinRequest,
					Configuration.getSiteName() + ": demande d'adhésion refusée et exclusion",
					"com/pferrot/lendity/emailtemplate/groupjoinrequest/ban/fr");

			if (log.isInfoEnabled()) {
				log.info("'" + pGroupJoinRequest.getRequester() + "' is banned by '" + pGroupJoinRequest.getGroup() + "'.");
			}
		}
		catch (GroupJoinRequestException e) {
			throw e;
		}
		catch (Exception e) {
			throw new GroupJoinRequestException(e);
		}	
	}
	
	public void updateBanGroupJoinRequest(final Long pGroupJoinRequestId) throws GroupJoinRequestException {
		CoreUtils.assertNotNull(pGroupJoinRequestId);
		final GroupJoinRequest request = groupJoinRequestDao.findGroupJoinRequest(pGroupJoinRequestId);

		updateBanGroupJoinRequest(request);	
	}	
}

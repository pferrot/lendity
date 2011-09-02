package com.pferrot.lendity.group;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.dao.DocumentDao;
import com.pferrot.lendity.dao.GroupDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.dao.bean.GroupDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.document.DocumentService;
import com.pferrot.lendity.group.exception.GroupException;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Document;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.HtmlUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.security.SecurityUtils;

public class GroupService {
	
	private final static Log log = LogFactory.getLog(GroupService.class);
	
	private GroupDao groupDao;
	private PersonDao personDao;
	private ListValueDao listValueDao;
	private PersonService personService;
	private DocumentService documentService;
	private DocumentDao documentDao;
	
	public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public DocumentDao getDocumentDao() {
		return documentDao;
	}

	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}

	public GroupDao getGroupDao() {
		return groupDao;
	}
	
	public void setGroupDao(GroupDao groupDao) {
		this.groupDao = groupDao;
	}
	
	public PersonDao getPersonDao() {
		return personDao;
	}

	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}

	public ListValueDao getListValueDao() {
		return listValueDao;
	}
	
	public void setListValueDao(ListValueDao listValueDao) {
		this.listValueDao = listValueDao;
	}
	
	public PersonService getPersonService() {
		return personService;
	}
	
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
	
	public Group findGroup(final Long pGroupId) {
		return groupDao.findGroup(pGroupId);
	}
	
	public String findGroupTitle(final Long pGroupId) {
		return groupDao.findGroup(pGroupId).getTitle();
	}
	
	public Long createGroup(final Group pGroup) {
		pGroup.setCreationDate(new Date());
		return groupDao.createGroup(pGroup);
	}
	
	public void updateGroup(final Group pGroup) {
		groupDao.updateGroup(pGroup);
	}
	
	public void updateGroupAddMember(final Long pGroupId, final Long pMemberToAddId) throws GroupException {
		CoreUtils.assertNotNull(pGroupId);
		CoreUtils.assertNotNull(pMemberToAddId);
		
		updateGroupAddMember(findGroup(pGroupId), getPersonService().findPerson(pMemberToAddId));
	}
	
	/**
	 * Does not check the AC.
	 * 
	 * @param pGroupId
	 * @param pMemberToAddId
	 * @throws GroupException
	 */
	public void updateGroupAddMemberPrivileged(final Long pGroupId, final Long pMemberToAddId) throws GroupException {
		CoreUtils.assertNotNull(pGroupId);
		CoreUtils.assertNotNull(pMemberToAddId);
		
		updateGroupAddMemberPrivileged(findGroup(pGroupId), getPersonService().findPerson(pMemberToAddId));
	}
	
	public void updateGroupAddMember(final Group pGroup, final Person pMemberToAdd) throws GroupException {
		updateGroupAddMemberInternal(pGroup, pMemberToAdd, true);
	}
	
	/**
	 * Does not check the AC.
	 * 
	 * @param pGroup
	 * @param pMemberToAdd
	 * @throws GroupException
	 */
	public void updateGroupAddMemberPrivileged(final Group pGroup, final Person pMemberToAdd) throws GroupException {
		updateGroupAddMemberInternal(pGroup, pMemberToAdd, false);
	}
	
	private void updateGroupAddMemberInternal(final Group pGroup, final Person pMemberToAdd, final boolean pCheckAC) throws GroupException {
		CoreUtils.assertNotNull(pGroup);
		CoreUtils.assertNotNull(pMemberToAdd);
		
		if (pCheckAC) {
			assertCurrentUserAuthorizedToAddMember(pGroup, pMemberToAdd);
		}
		
		pGroup.addMember(pMemberToAdd);
		groupDao.updateGroup(pGroup);
	}
	
	public void updateGroupRemoveMember(final Long pGroupId, final Long pMemberToRemoveId) throws GroupException {
		CoreUtils.assertNotNull(pGroupId);
		CoreUtils.assertNotNull(pMemberToRemoveId);
		
		updateGroupRemoveMember(findGroup(pGroupId), getPersonService().findPerson(pMemberToRemoveId));
	}
	
	public void updateGroupRemoveMember(final Group pGroup, final Person pMemberToRemove) throws GroupException {
		CoreUtils.assertNotNull(pGroup);
		CoreUtils.assertNotNull(pMemberToRemove);
		
		assertCurrentUserAuthorizedToRemoveMember(pGroup, pMemberToRemove);
		
		pGroup.removeMember(pMemberToRemove);
		groupDao.updateGroup(pGroup);
		
		// Remove shared items and needs.
	}
	
	public void updateGroupUnbanPerson(final Long pGroupId, final Long pPersonToUnbanId) throws GroupException {
		CoreUtils.assertNotNull(pGroupId);
		CoreUtils.assertNotNull(pPersonToUnbanId);
		
		updateGroupUnbanPerson(findGroup(pGroupId), getPersonService().findPerson(pPersonToUnbanId));
	}
	
	public void updateGroupUnbanPerson(final Group pGroup, final Person pPersonToUnban) throws GroupException {
		CoreUtils.assertNotNull(pGroup);
		CoreUtils.assertNotNull(pPersonToUnban);
		
		assertCurrentUserAuthorizedToUnbanPerson(pGroup, pPersonToUnban);
		
		pGroup.removeBannedPerson(pPersonToUnban);
		
		groupDao.updateGroup(pGroup);
	}
	
	public void updateGroupBanPerson(final Long pGroupId, final Long pPersonToBanId) throws GroupException {
		CoreUtils.assertNotNull(pGroupId);
		CoreUtils.assertNotNull(pPersonToBanId);
		
		updateGroupBanPerson(findGroup(pGroupId), getPersonService().findPerson(pPersonToBanId));
	}

	public void updateGroupBanPerson(final Group pGroup, final Person pPersonToBan) throws GroupException {
		CoreUtils.assertNotNull(pGroup);
		CoreUtils.assertNotNull(pPersonToBan);
		
		assertCurrentUserAuthorizedToBanPerson(pGroup, pPersonToBan);		
		
		pGroup.removeAdministrator(pPersonToBan);
		pGroup.removeMember(pPersonToBan);
		pGroup.addBannedPerson(pPersonToBan);
		
		groupDao.updateGroup(pGroup);
	}
	
	public void updateGroupRemoveAdminAddMember(final Long pGroupId, final Long pAdminToRemoveId) throws GroupException {
		CoreUtils.assertNotNull(pGroupId);
		CoreUtils.assertNotNull(pAdminToRemoveId);
		
		updateGroupRemoveAdmin(findGroup(pGroupId), getPersonService().findPerson(pAdminToRemoveId));
	}
	
	public void updateGroupRemoveAdmin(final Long pGroupId, final Long pAdminToRemoveId) throws GroupException {
		CoreUtils.assertNotNull(pGroupId);
		CoreUtils.assertNotNull(pAdminToRemoveId);
		
		updateGroupRemoveAdmin(findGroup(pGroupId), getPersonService().findPerson(pAdminToRemoveId));
	}
	
	public void updateGroupRemoveAdmin(final Group pGroup, final Person pAdminToRemove) throws GroupException {
		CoreUtils.assertNotNull(pGroup);
		CoreUtils.assertNotNull(pAdminToRemove);
		
		assertCurrentUserAuthorizedToRemoveAdmin(pGroup, pAdminToRemove);
		
		pGroup.removeAdministrator(pAdminToRemove);
		groupDao.updateGroup(pGroup);
	}

	public void updateGroupAddAdmin(final Long pGroupId, final Long pMemberToAddAdminToId) throws GroupException {
		CoreUtils.assertNotNull(pGroupId);
		CoreUtils.assertNotNull(pMemberToAddAdminToId);
		
		updateGroupAddAdmin(findGroup(pGroupId), getPersonService().findPerson(pMemberToAddAdminToId));
	}	
	
	public void updateGroupAddAdmin(final Group pGroup, final Person pMemberToAddAdminTo) throws GroupException {
		CoreUtils.assertNotNull(pGroup);
		CoreUtils.assertNotNull(pMemberToAddAdminTo);
		
		assertCurrentUserAuthorizedToAddAdmin(pGroup, pMemberToAddAdminTo);
		
		pGroup.removeMember(pMemberToAddAdminTo);
		pGroup.addAdministrator(pMemberToAddAdminTo);
		groupDao.updateGroup(pGroup);
	}

	public void updateGroupRemoveMemberAndAdministrator(final Long pGroupId, final Long pMemberToRemoveId) throws GroupException {
		CoreUtils.assertNotNull(pGroupId);
		CoreUtils.assertNotNull(pMemberToRemoveId);
		
		updateGroupRemoveMemberAndAdministrator(findGroup(pGroupId), getPersonService().findPerson(pMemberToRemoveId));
	}
	
	public void updateGroupRemoveMemberAndAdministrator(final Group pGroup, final Person pMemberToRemove) throws GroupException {
		CoreUtils.assertNotNull(pGroup);
		CoreUtils.assertNotNull(pMemberToRemove);
		
		assertCurrentUserAuthorizedToRemoveMember(pGroup, pMemberToRemove);		
						
		pGroup.removeMember(pMemberToRemove);
		pGroup.removeAdministrator(pMemberToRemove);
		groupDao.updateGroup(pGroup);
	}
	
	public void updateGroupAddCurrentUserAsMember(final Group pGroup, final String pPassword) throws GroupException {
		if (pGroup.isPasswordProtected() && !pGroup.getPassword().equals(pPassword)) {
			throw new SecurityException("Password is not correct");
		}
		updateGroupAddMember(pGroup, getPersonService().getCurrentPerson());
	}
	
	public void updateGroupRemoveCurrentUserFromMembersAndAdministrators(final Group pGroup) throws GroupException {
		updateGroupRemoveMemberAndAdministrator(pGroup, getPersonService().getCurrentPerson());
	}
	
	public void updateGroupAddCurrentUserAsMember(final Long pGroupId, final String pPassword) throws GroupException {
		updateGroupAddCurrentUserAsMember(findGroup(pGroupId), pPassword);
	}
	
	public void updateGroupRemoveCurrentUserFromMembersAndAdministrators(final Long pGroupId) throws GroupException {
		updateGroupRemoveCurrentUserFromMembersAndAdministrators(findGroup(pGroupId));
	}
	
	public void deleteGroup(final Long pGroupId) {
		deleteGroup(groupDao.findGroup(pGroupId));
	}
	
	public void deleteGroup(final Group pGroup) {
		groupDao.deleteGroup(pGroup);
	}

	/**
	 * Returns 5 random groups.
	 * 
	 * @return
	 */
	public List<Group> findRandomGroupsHomepage() {
		final GroupDaoQueryBean queryBean = new GroupDaoQueryBean();
		
		queryBean.setOrderBy("random");
		queryBean.setFirstResult(0);
		queryBean.setMaxResults(5);
		
		return groupDao.findGroupsList(queryBean);
	}

	public ListWithRowCount findGroups(final String pTitle, final int pFirstResult, final int pMaxResults) {
		final GroupDaoQueryBean bean = new GroupDaoQueryBean();
		if (!StringUtils.isNullOrEmpty(pTitle)) {
			bean.setTitle(pTitle);
		}
		bean.setFirstResult(pFirstResult);
		bean.setMaxResults(pMaxResults);
		return groupDao.findGroups(bean);
	}

	public ListWithRowCount findPersonGroupsWhereOwnerOrAdministratorOrMember(final Long pPersonId, final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		final GroupDaoQueryBean bean = new GroupDaoQueryBean();
		final Long[] ids = {pPersonId}; 
		
		bean.setOwnerOrAdministratorsOrMembersIds(ids);
		
		bean.setTitle(pSearchString);
		bean.setFirstResult(pFirstResult);
		bean.setMaxResults(pMaxResults);
		
		final ListWithRowCount lwro = groupDao.findGroups(bean); 
		
		return lwro;
	}

	public List<Group> findPersonGroupsWhereOwnerOrAdministratorOrMemberList(final Long pPersonId, final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		final GroupDaoQueryBean bean = new GroupDaoQueryBean();
		final Long[] ids = {pPersonId}; 
		
		bean.setOwnerOrAdministratorsOrMembersIds(ids);
		
		bean.setTitle(pSearchString);
		bean.setFirstResult(pFirstResult);
		bean.setMaxResults(pMaxResults);
		
		return groupDao.findGroupsList(bean); 
	}

	public ListWithRowCount findPersonGroupsWhereOwnerOrAdministrator(final Long pPersonId, final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		final GroupDaoQueryBean bean = new GroupDaoQueryBean();
		final Long[] ids = {pPersonId}; 
		
		bean.setOwnerOrAdministratorsIds(ids);
		
		bean.setTitle(pSearchString);
		bean.setFirstResult(pFirstResult);
		bean.setMaxResults(pMaxResults);
		
		final ListWithRowCount lwro = groupDao.findGroups(bean); 
		
		return lwro;
	}

	public ListWithRowCount findPersonGroupsWhereMember(final Long pPersonId, final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		final GroupDaoQueryBean bean = new GroupDaoQueryBean();
		final Long[] ids = {pPersonId}; 
		
		bean.setMembersIds(ids);
		
		bean.setTitle(pSearchString);
		bean.setFirstResult(pFirstResult);
		bean.setMaxResults(pMaxResults);
		
		final ListWithRowCount lwro = groupDao.findGroups(bean); 
		
		return lwro;
	}

	public ListWithRowCount findPersonGroupsWhereOwner(final Long pPersonId, final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		final GroupDaoQueryBean bean = new GroupDaoQueryBean();
		final Long[] ids = {pPersonId}; 
		
		bean.setOwnerIds(ids);
		
		bean.setTitle(pSearchString);
		bean.setFirstResult(pFirstResult);
		bean.setMaxResults(pMaxResults);
		
		final ListWithRowCount lwro = groupDao.findGroups(bean); 
		
		return lwro;
	}
	
	public ListWithRowCount findPersonGroupsWhereAdministrator(final Long pPersonId, final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		final GroupDaoQueryBean bean = new GroupDaoQueryBean();
		final Long[] ids = {pPersonId}; 
		
		bean.setAdministratorsIds(ids);
		
		bean.setTitle(pSearchString);
		bean.setFirstResult(pFirstResult);
		bean.setMaxResults(pMaxResults);
		
		final ListWithRowCount lwro = groupDao.findGroups(bean); 
		
		return lwro;
	}

	public List<Group> findPersonGroupsList(final Long pPersonId, final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		final GroupDaoQueryBean bean = new GroupDaoQueryBean();
		final Long[] ids = {pPersonId}; 
		
		bean.setOwnerOrAdministratorsOrMembersIds(ids);
		
		bean.setTitle(pSearchString);
		bean.setFirstResult(pFirstResult);
		bean.setMaxResults(pMaxResults);
		
		final List<Group> result = groupDao.findGroupsList(bean); 
	
		return result;
	}

	public ListWithRowCount findGroupBanned(final Long pGroupId, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pGroupId);
		
		return personDao.findGroupBanned(pGroupId, pFirstResult, pMaxResults);
	}
	
	public long countGroupBanned(final Long pGroupId) {
		CoreUtils.assertNotNull(pGroupId);
		
		return personDao.countGroupBanned(pGroupId);
	}
	
	public ListWithRowCount findGroupMembers(final Long pGroupId, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pGroupId);
		
		return personDao.findGroupMembers(pGroupId, pFirstResult, pMaxResults);
	}
	
	public long countGroupMembers(final Long pGroupId) {
		CoreUtils.assertNotNull(pGroupId);
		
		return personDao.countGroupMembers(pGroupId);
	}
	
	public ListWithRowCount findGroupAdministrators(final Long pGroupId, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pGroupId);
		
		return personDao.findGroupAdministrators(pGroupId, pFirstResult, pMaxResults);
	}
	
	public long countGroupAdministrators(final Long pGroupId) {
		CoreUtils.assertNotNull(pGroupId);
		
		return personDao.countGroupAdministrators(pGroupId);
	}
	
	public Long[] getCurrentPersonGroupIds() {
		return getPersonGroupsIds(getPersonService().getCurrentPerson(), null);
	}

	/**
	 * Returns an array containing the IDs of all groups of the person if
	 * null is passed as a parameter for pGroupId.
	 * If a value is passed for pGroupId, then an array with that ID is returned ONLY
	 * if that group is indeed a group of pPerson. Otherwise a SecurityException is
	 * throws.
	 * 
	 * @param pPerson
	 * @param pGroupId
	 * @return
	 */
	public Long[] getPersonGroupsIds(final Person pPerson, final Long pGroupId) {
		CoreUtils.assertNotNull(pPerson);
		Long[] groupsIds = null;
		// All groups.
		if (groupsIds == null) {
			final List<Group> groups = findPersonGroupsWhereOwnerOrAdministratorOrMemberList(pPerson.getId(), null, 0, 0);
			if (groups == null || groups.isEmpty()) {
				return new Long[]{Long.valueOf(-1)};
			}
			groupsIds = new Long[groups.size()];
			int counter = 0;
			for(Group group: groups) {			
				groupsIds[counter] = group.getId();
				counter++;
			}
		}
		// Only one group - make sure that it is a group of the user. If not, it is someone trying to hack...
		else {
			final List<Group> groups = findPersonGroupsWhereOwnerOrAdministratorOrMemberList(pPerson.getId(), null, 0, 0);
			boolean groupFound = false;
			if (groups != null) {
				for(Group group: groups) {					
					if (pGroupId.equals(group.getId())) {
						groupFound = true;
						break;
					}
				}
			}
			if (!groupFound) {
				throw new SecurityException("Person with ID " + pPerson.getId() + " is not in group " + pGroupId + " (current person: " + PersonUtils.getCurrentPersonId() + ").");
			}
			groupsIds = new Long[]{pGroupId};
		}
		return groupsIds;
	}
	
	public boolean isUserMemberOfGroup(final Person pPerson, final Group pGroup) {
		CoreUtils.assertNotNull(pPerson);
		CoreUtils.assertNotNull(pGroup);
		
		return pPerson.getGroupsMember().contains(pGroup);
	}
	
	public boolean isCurrentUserMemberOfGroup(final Group pGroup) {
		return SecurityUtils.isLoggedIn() && isUserMemberOfGroup(getPersonService().getCurrentPerson(), pGroup);
	}

	public boolean isUserOwnerOrAdministratorOrMemberOfGroup(final Person pPerson, final Group pGroup) {
		CoreUtils.assertNotNull(pPerson);
		CoreUtils.assertNotNull(pGroup);
		
		return isUserOwnerOfGroup(pPerson, pGroup) ||
			isUserAdministratorOfGroup(pPerson, pGroup) ||
			isUserMemberOfGroup(pPerson, pGroup);
	}
	
	public boolean isCurrentUserOwnerOrAdministratorOrMemberOfGroup(final Group pGroup) {
		return SecurityUtils.isLoggedIn() && isUserOwnerOrAdministratorOrMemberOfGroup(getPersonService().getCurrentPerson(), pGroup);
	}
	
	public void assertCurrentUserOwnerOrAdministratorOrMemberOfGroup(final Group pGroup) {
		if (!isCurrentUserOwnerOrAdministratorOrMemberOfGroup(pGroup)) {
			throw new SecurityException("Current user is not member of group");
		}
	}
	
	public boolean isUserOwnerOfGroup(final Person pPerson, final Group pGroup) {
		CoreUtils.assertNotNull(pPerson);
		CoreUtils.assertNotNull(pGroup);
		
		return pPerson.getId().equals(pGroup.getOwner().getId());
	}
	
	public boolean isCurrentUserOwnerOfGroup(final Group pGroup) {
		return SecurityUtils.isLoggedIn() && isUserOwnerOfGroup(getPersonService().getCurrentPerson(), pGroup);
	}
	
	public void assertCurrentUserOwnerOfGroup(final Group pGroup) {
		if (!isCurrentUserOwnerOfGroup(pGroup)) {
			throw new SecurityException("Current user is not owner of group");
		}
	}
	
	public void assertCurrentUserMemberOfGroup(final Group pGroup) {
		if (!isCurrentUserMemberOfGroup(pGroup)) {
			throw new SecurityException("Current user is not member of group");
		}
	}	
	
	public boolean isUserAdministratorOfGroup(final Person pPerson, final Group pGroup) {
		CoreUtils.assertNotNull(pPerson);
		CoreUtils.assertNotNull(pGroup);
		
		return pGroup.getAdministrators() != null && pGroup.getAdministrators().contains(pPerson);
	}
	
	public boolean isCurrentUserAdministratorOfGroup(final Group pGroup) {
		return SecurityUtils.isLoggedIn() && isUserAdministratorOfGroup(getPersonService().getCurrentPerson(), pGroup);
	}
	
	public void assertCurrentUserAdministratorOfGroup(final Group pGroup) {
		if (!isCurrentUserAdministratorOfGroup(pGroup)) {
			throw new SecurityException("Current user is not administrator of group");
		}
	}

	public void assertCurrentUserOwnerOrAdministratorOfGroup(final Group pGroup) {
		if (! (isCurrentUserOwnerOfGroup(pGroup) || isCurrentUserAdministratorOfGroup(pGroup))) {
			throw new SecurityException("Current user is neither owner nor administrator of group");
		}
	}
	
	public boolean isUserBannedByGroup(final Person pPerson, final Group pGroup) {
		CoreUtils.assertNotNull(pPerson);
		CoreUtils.assertNotNull(pGroup);
		
		return pGroup.getBannedPersons() != null && pGroup.getBannedPersons().contains(pPerson);
	}

	public boolean isCurrentUserBannedByGroup(final Group pGroup) {
		return SecurityUtils.isLoggedIn() && isUserBannedByGroup(getPersonService().getCurrentPerson(), pGroup);
	}
	
	public void assertCurrentUserBannedByGroup(final Group pGroup) {
		if (!isCurrentUserBannedByGroup(pGroup)) {
			throw new SecurityException("Current user is not banned by group");
		}
	}

	public String getGroupPicture1Src(final Group pGroup, final boolean pAuthorizeDocumentAccess) {
		final Document picture = pGroup.getImage1();
		if (picture == null ) {
			return JsfUtils.getFullUrl(GroupConsts.DUMMY_GROUP_PICTURE_URL);
		}
		else {
			if (pAuthorizeDocumentAccess) {
				documentService.authorizeDownloadOneMinute(JsfUtils.getSession(), picture.getId());
			}
			return JsfUtils.getFullUrl(
					PagesURL.DOCUMENT_DOWNLOAD, 
					PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_ID, 
					picture.getId().toString());
		}			
	}
	
	public String getGroupThumbnail1Src(final Group pGroup, final boolean pAuthorizeDocumentAccess) {
		return getGroupThumbnailSrc(pGroup, pAuthorizeDocumentAccess, JsfUtils.getSession(), JsfUtils.getContextRoot());		
	}
	
	public String getGroupThumbnailSrc(final Group pGroup, final boolean pAuthorizeDocumentAccess,
			final HttpSession pSession, final String pUrlPrefix) {
		final Document thumbnail = pGroup.getThumbnail1();
		if (thumbnail == null ) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, GroupConsts.DUMMY_GROUP_THUMBNAIL_URL);
		}
		else {
			if (pAuthorizeDocumentAccess) {
				documentService.authorizeDownloadOneMinute(pSession, thumbnail.getId());
			}
			return JsfUtils.getFullUrlWithPrefix(
					pUrlPrefix,
					PagesURL.DOCUMENT_DOWNLOAD, 
					PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_ID, 
					thumbnail.getId().toString());
		}		
	}

	public void updateGroupPicture(final Group pGroup, final Document pPicture, final Document pThumbnail) {
		assertCurrentUserAuthorizedToEdit(pGroup);
		if (pPicture != null) {
			pPicture.setPublik(Boolean.TRUE);
		}
		if (pThumbnail != null) {
			pThumbnail.setPublik(Boolean.TRUE);
		}
		final Document oldPic = pGroup.getImage1();
		final Document oldThumbnail = pGroup.getThumbnail1();		
		if (pPicture != null) {
			documentDao.createDocument(pPicture);
		}
		pGroup.setImage1(pPicture);
		if (pThumbnail != null) {
			documentDao.createDocument(pThumbnail);
		}
		pGroup.setThumbnail1(pThumbnail);
		
		if (oldPic != null) {
			documentDao.deleteDocument(oldPic);
		}
		if (oldThumbnail != null) {
			documentDao.deleteDocument(oldThumbnail);
		}
		
		groupDao.updateGroup(pGroup);
 	}

	/////////////////////////////////////////////////////////
	// Access control
	
	public boolean isCurrentUserAuthorizedToView(final Group pGroup) {
		return isUserAuthorizedToView(getPersonService().getCurrentPerson(), pGroup);
	}

	public boolean isUserAuthorizedToView(final Person pPerson, final Group pGroup) {
		return true;
	}
	
	public boolean isCurrentUserAuthorizedToViewComments(final Group pGroup) {
		return isUserAuthorizedToViewComments(personService.getCurrentPerson(), pGroup);
	}
	
	public boolean isUserAuthorizedToViewComments(final Person pPerson, final Group pGroup) {
		if (!SecurityUtils.isLoggedIn()) {
			return false;
		}
		if (pPerson.getUser() != null && pPerson.getUser().isAdmin()) {
			return true;
		}
		if (Boolean.TRUE.equals(pGroup.getOnlyMembersCanSeeComments())) {
			return pPerson != null && isUserOwnerOrAdministratorOrMemberOfGroup(pPerson, pGroup);
		}
		else {
			return isUserAuthorizedToView(pPerson, pGroup);
		}
	}	
	
	public void assertCurrentUserAuthorizedToView(final Group pGroup) {
		if (!isCurrentUserAuthorizedToView(pGroup)) {
			throw new SecurityException("Current user is not authorized to view group");
		}
	}
	
	public void assertUserAuthorizedToView(final Person pPerson, final Group pGroup) {
		if (!isUserAuthorizedToView(pPerson, pGroup)) {
			throw new SecurityException("User is not authorized to view group");
		}
	}
	
	public boolean isCurrentUserAuthorizedToEdit(final Group pGroup) {
		return isUserAuthorizedToEdit(getPersonService().getCurrentPerson(), pGroup);
	}
	
	public boolean isUserAuthorizedToEdit(final Person pPerson, final Group pGroup) {
		CoreUtils.assertNotNull(pGroup);
		if (!SecurityUtils.isLoggedIn()) {
			return false;
		}
		else if (pPerson == null || pPerson.getUser() == null) {
			return false;
		}
		else if (pPerson.getUser() != null &&
			pPerson.getUser().isAdmin()) {
			return true;
		}
		else if (isUserOwnerOfGroup(pPerson, pGroup)) {
			return true;
		}
		else if (isUserAdministratorOfGroup(pPerson, pGroup)) {
			return true;
		}
		return false;
	}

	public void assertCurrentUserAuthorizedToEdit(final Group pGroup) {
		if (!isCurrentUserAuthorizedToEdit(pGroup)) {
			throw new SecurityException("Current user is not authorized to edit group");
		}
	}

	public boolean isCurrentUserAuthorizedToAdd() {
		final Person currentPerson = getPersonService().getCurrentPerson();
		if (currentPerson != null && currentPerson.getUser() != null) {
			return true;
		}
		return false;
	}

	public void assertCurrentUserAuthorizedToAdd(final Group pGroup) {
		if (!isCurrentUserAuthorizedToAdd()) {
			throw new SecurityException("Current user is not authorized to add group");
		}
	}

	public boolean isCurrentUserAuthorizedToDelete(final Group pGroup) {
		final Person currentPerson = getPersonService().getCurrentPerson();
		return isUserAuthorizedToDelete(currentPerson, pGroup);
	}
	
	/**
	 * Only owner of the group and SYSTEM admins can delete, i.e. administrators of
	 * the group cannot delete.
	 * 
	 * @param pPerson
	 * @param pGroup
	 * @return
	 */
	public boolean isUserAuthorizedToDelete(final Person pPerson, final Group pGroup) {
		CoreUtils.assertNotNull(pGroup);
		if (!SecurityUtils.isLoggedIn()) {
			return false;
		}
		else if (pPerson == null || pPerson.getUser() == null) {
			return false;
		}
		else if (pPerson.getUser() != null &&
			pPerson.getUser().isAdmin()) {
			return true;
		}
		else if (isUserOwnerOfGroup(pPerson, pGroup)) {
			return true;
		}
		return false;
	}

	public void assertCurrentUserAuthorizedToDelete(final Group pGroup) {
		if (!isCurrentUserAuthorizedToDelete(pGroup)) {
			throw new SecurityException("Current user is not authorized to delete group");
		}
	}

	public boolean isUserAuthorizedToAddMember(Person pUser, Group pGroup, Person pMemberToAdd) {
		return pUser.isEnabled() &&
			   pMemberToAdd.isEnabled() &&
			!isUserOwnerOrAdministratorOrMemberOfGroup(pMemberToAdd, pGroup) &&
			!isUserBannedByGroup(pMemberToAdd, pGroup) &&
			((Boolean.FALSE.equals(pGroup.getValidateMembership()) && pUser.getId().equals(pMemberToAdd.getId())) ||
			 isUserAdministratorOfGroup(pUser, pGroup) ||
			 isUserOwnerOfGroup(pUser, pGroup));
	}
	
	public void assertUserAuthorizedToAddMember(final Person pUser, final Group pGroup, final Person pMemberToAdd) {
		if (!isUserAuthorizedToAddMember(pUser, pGroup, pMemberToAdd)) {
			throw new SecurityException("Current user is not authorized to remove member");
		}		
	}
	
	public void assertCurrentUserAuthorizedToAddMember(final Group pGroup, final Person pMemberToAdd) {
		assertUserAuthorizedToAddMember(getPersonService().getCurrentPerson(), pGroup, pMemberToAdd);		
	}
	
	public boolean isUserAuthorizedToRemoveMember(final Person pUser, final Group pGroup, final Person pMemberToRemove) {
		return pUser.isEnabled() &&
		       pMemberToRemove.isEnabled() &&
			(pUser.getId().equals(pMemberToRemove.getId()) ||
			 isUserAdministratorOfGroup(pUser, pGroup) ||
			 isUserOwnerOfGroup(pUser, pGroup)) &&
			!isUserOwnerOfGroup(pMemberToRemove, pGroup);
	}
	
	public boolean isUserAuthorizedToRemoveMember(final Long pUserId, final Long pGroupId, final Long pMemberToRemoveId) {
		final Person user = getPersonService().findPerson(pUserId);
		final Group group = findGroup(pGroupId);
		final Person memberToRemove = getPersonService().findPerson(pMemberToRemoveId);
		
		return isUserAuthorizedToRemoveMember(user, group, memberToRemove);
	}
	
	public void assertUserAuthorizedToRemoveMember(final Person pUser, final Group pGroup, final Person pMemberToRemove) {
		if (!isUserAuthorizedToRemoveMember(pUser, pGroup, pMemberToRemove)) {
			throw new SecurityException("Current user is not authorized to remove member");
		}		
	}
	
	public void assertCurrentUserAuthorizedToRemoveMember(final Group pGroup, final Person pMemberToRemove) {
		assertUserAuthorizedToRemoveMember(getPersonService().getCurrentPerson(), pGroup, pMemberToRemove);		
	}
	
	public boolean isUserAuthorizedToRemoveAdmin(final Person pUser, final Group pGroup, final Person pPersonToRemoveAdminFrom) {
		return isUserAuthorizedToRemoveMember(pUser, pGroup, pPersonToRemoveAdminFrom);
	}
	
	public boolean isUserAuthorizedToRemoveAdmin(final Long pUserId, final Long pGroupId, final Long pPersonToRemoveAdminFromId) {
		final Person user = getPersonService().findPerson(pUserId);
		final Group group = findGroup(pGroupId);
		final Person personToRemoveAdminFrom = getPersonService().findPerson(pPersonToRemoveAdminFromId);
		
		return isUserAuthorizedToRemoveAdmin(user, group, personToRemoveAdminFrom);
	}
	
	public void assertUserAuthorizedToRemoveAdmin(final Person pUser, final Group pGroup, final Person pPersonToRemoveAdminFrom) {
		if (!isUserAuthorizedToRemoveAdmin(pUser, pGroup, pPersonToRemoveAdminFrom)) {
			throw new SecurityException("User " + pUser.getId() + " is not authorized to remove admin person " + pPersonToRemoveAdminFrom.getId() + " from group " + pGroup.getId());
		}
	}

	public void assertCurrentUserAuthorizedToRemoveAdmin(final Group pGroup, final Person pPersonToRemoveAdminFrom) {
		assertUserAuthorizedToRemoveAdmin(getPersonService().getCurrentPerson(), pGroup, pPersonToRemoveAdminFrom);		
	}
	
	public boolean isUserAuthorizedToAddAdmin(final Person pUser, final Group pGroup, final Person pAdminToAdd) {
		return pUser.isEnabled() &&
			   pAdminToAdd.isEnabled() &&
			(isUserAdministratorOfGroup(pUser, pGroup) ||
			 isUserOwnerOfGroup(pUser, pGroup)) &&
			!isUserBannedByGroup(pAdminToAdd, pGroup) &&
			!isUserOwnerOfGroup(pAdminToAdd, pGroup);
	}
	
	public boolean isUserAuthorizedToAddAdmin(final Long pUserId, final Long pGroupId, final Long pAdminToAddId) {
		final Person user = getPersonService().findPerson(pUserId);
		final Group group = findGroup(pGroupId);
		final Person adminToAdd = getPersonService().findPerson(pAdminToAddId);
		
		return isUserAuthorizedToAddAdmin(user, group, adminToAdd);
	}
	
	public void assertUserAuthorizedToAddAdmin(final Person pUser, final Group pGroup, final Person pPersonToAddAdminTo) {
		if (!isUserAuthorizedToAddAdmin(pUser, pGroup, pPersonToAddAdminTo)) {
			throw new SecurityException("User " + pUser.getId() + " is not authorized to add admin person " + pPersonToAddAdminTo.getId() + " to group " + pGroup.getId());
		}
	}
	
	public void assertCurrentUserAuthorizedToAddAdmin(final Group pGroup, final Person pPersonToAddAdminTo) {
		assertUserAuthorizedToAddAdmin(getPersonService().getCurrentPerson(), pGroup, pPersonToAddAdminTo);		
	}
	
	public boolean isUserAuthorizedToUnbanPerson(final Person pUser, final Group pGroup, final Person pPersonToUnban) {
		return pUser.isEnabled() &&
		       pPersonToUnban.isEnabled() &&
			(isUserAdministratorOfGroup(pUser, pGroup) ||
			 isUserOwnerOfGroup(pUser, pGroup));
	}
	
	public boolean isUserAuthorizedToUnbanPerson(final Long pUserId, final Long pGroupId, final Long pPersonToUnbanId) {
		final Person user = getPersonService().findPerson(pUserId);
		final Group group = findGroup(pGroupId);
		final Person personToUnban = getPersonService().findPerson(pPersonToUnbanId);
		
		return isUserAuthorizedToUnbanPerson(user, group, personToUnban);
	}
	
	public void assertUserAuthorizedToUnbanPerson(final Person pUser, final Group pGroup, final Person pPersonToUnban) {
		if (!isUserAuthorizedToUnbanPerson(pUser, pGroup, pPersonToUnban)) {
			throw new SecurityException("User " + pUser.getId() + " is not authorized to unban person " + pPersonToUnban.getId() + " from group " + pGroup.getId());
		}		
	}
	
	public void assertCurrentUserAuthorizedToUnbanPerson(final Group pGroup, final Person pPersonToUnban) {
		assertUserAuthorizedToUnbanPerson(getPersonService().getCurrentPerson(), pGroup, pPersonToUnban);		
	}
	
	public boolean isUserAuthorizedToBanPerson(final Person pUser, final Group pGroup, final Person pPersonToBan) {
		return pUser.isEnabled() &&
		       pPersonToBan.isEnabled() &&
			(isUserAdministratorOfGroup(pUser, pGroup) ||
			 isUserOwnerOfGroup(pUser, pGroup)) &&
			!isUserOwnerOfGroup(pPersonToBan, pGroup);
	}
	
	public boolean isUserAuthorizedToBanPerson(final Long pUserId, final Long pGroupId, final Long pPersonToBanId) {
		final Person user = getPersonService().findPerson(pUserId);
		final Group group = findGroup(pGroupId);
		final Person personToBan = getPersonService().findPerson(pPersonToBanId);
		
		return isUserAuthorizedToBanPerson(user, group, personToBan);
	}
	
	public void assertUserAuthorizedToBanPerson(final Person pUser, final Group pGroup, final Person pPersonToBan) {
		if (!isUserAuthorizedToBanPerson(pUser, pGroup, pPersonToBan)) {
			throw new SecurityException("User " + pUser.getId() + " is not authorized to ban person " + pPersonToBan.getId() + " from group " + pGroup.getId());
		}		
	}

	public void assertCurrentUserAuthorizedToBanPerson(final Group pGroup, final Person pPersonToBan) {
		assertUserAuthorizedToBanPerson(getPersonService().getCurrentPerson(), pGroup, pPersonToBan);		
	}

	// Access control
	/////////////////////////////////////////////////////////
	

	/**
	 * Replaces all occurrences of strings like {g123} with an href link to the
	 * corresponding object, e.g.:
	 * 
	 * <a href="http://www.lendity.ch/group/groupOverview.faces?groupID=123" target="_blank">The group title</a>
	 * 
	 * If pPerson is not authorized to view the group, a standard error text is used instead.
	 * 
	 * @param pText
	 * @param pPerson
	 * @return
	 */
	public String processGroupHref(final String pText, final Person pPerson) {
		if (StringUtils.isNullOrEmpty(pText)) {
			return pText;
		}
		
		final String regex = "\\{g[0-9]+\\}";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(pText);

		final StringBuffer result = new StringBuffer();
		while (m.find()) {
			try {
				final String text = m.group();
				final Long groupId = Long.parseLong(text.substring(2, text.length() - 1));
				final Group group = findGroup(groupId);
				assertUserAuthorizedToView(pPerson, group);
				m.appendReplacement(result, getHrefLinkToGroup(group, true));
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
	
	private String getHrefLinkToGroup(final Group pGroup, final boolean pOpenInNewWindow) {
		return "<a href=\"" + 
			JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.GROUP_OVERVIEW, PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID, pGroup.getId().toString()) +
			"\"" +
			(pOpenInNewWindow?" target=\"_blank\"":"") +
			">" + 
			HtmlUtils.escapeHtmlAndReplaceCr(pGroup.getTitle()) + 
			"</a>";
	}
}

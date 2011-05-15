package com.pferrot.lendity.group;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.core.StringUtils;
import com.pferrot.lendity.dao.GroupDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.dao.bean.GroupDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.group.exception.GroupException;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.security.SecurityUtils;

public class GroupService {
	
	private final static Log log = LogFactory.getLog(GroupService.class);
	
	private GroupDao groupDao;
	private PersonDao personDao;
	private ListValueDao listValueDao;
	private PersonService personService;
	
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

	public void updateGroupAddMember(final Group pGroup, final Person pMemberToAdd) throws GroupException {
		CoreUtils.assertNotNull(pGroup);
		CoreUtils.assertNotNull(pMemberToAdd);
		
		assertCurrentUserAuthorizedToAddMember(pGroup, pMemberToAdd);
		
		if (!pMemberToAdd.isEnabled()) {
			throw new GroupException("Person is not enabled: " + pMemberToAdd);
		}
		
		if (isUserOwnerOrAdministratorOrMemberOfGroup(pMemberToAdd, pGroup)) {
			throw new GroupException("Person (" + pMemberToAdd + ") is already owner or admin or member of group (" + pGroup + ")");
		}
		
		if (!Boolean.FALSE.equals(pGroup.getValidateMembership())) {
			throw new GroupException("New memberships must be validated for group " + pGroup);
		}
		
		if (isUserBannedByGroup(pMemberToAdd, pGroup)) {
			throw new GroupException("Person " + pMemberToAdd + " is banned by group " + pGroup);
		}
		
		pGroup.addMember(pMemberToAdd);
		groupDao.updateGroup(pGroup);
		
//		pPerson.addGroupMember(pGroup);
//		getPersonService().updatePerson(pPerson);	
	}

	public void updateGroupRemoveMemberAndAdministrator(final Group pGroup, final Person pMemberToRemove) throws GroupException {
		CoreUtils.assertNotNull(pGroup);
		CoreUtils.assertNotNull(pMemberToRemove);
		
		assertCurrentUserAuthorizedToRemoveMember(pGroup, pMemberToRemove);
		
						
		pGroup.removeMember(pMemberToRemove);
		pGroup.removeAdministrator(pMemberToRemove);
		groupDao.updateGroup(pGroup);
		
//		pPerson.addGroupMember(pGroup);
//		getPersonService().updatePerson(pPerson);	
	}
	
	public void updateGroupAddCurrentUserAsMember(final Group pGroup) throws GroupException {
		updateGroupAddMember(pGroup, getPersonService().getCurrentPerson());
	}
	
	public void updateGroupRemoveCurrentUserFromMembersAndAdministrators(final Group pGroup) throws GroupException {
		updateGroupRemoveMemberAndAdministrator(pGroup, getPersonService().getCurrentPerson());
	}
	
	public void updateGroupAddCurrentUserAsMember(final Long pGroupId) throws GroupException {
		updateGroupAddCurrentUserAsMember(findGroup(pGroupId));
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
		return isUserMemberOfGroup(getPersonService().getCurrentPerson(), pGroup);
	}

	public boolean isUserOwnerOrAdministratorOrMemberOfGroup(final Person pPerson, final Group pGroup) {
		CoreUtils.assertNotNull(pPerson);
		CoreUtils.assertNotNull(pGroup);
		
		return isUserOwnerOfGroup(pPerson, pGroup) ||
			isUserAdministratorOfGroup(pPerson, pGroup) ||
			isUserMemberOfGroup(pPerson, pGroup);
	}
	
	public boolean isCurrentUserOwnerOrAdministratorOrMemberOfGroup(final Group pGroup) {
		return isUserOwnerOrAdministratorOrMemberOfGroup(getPersonService().getCurrentPerson(), pGroup);
	}
	
	public void assertCurrentUserOwnerOrAdministratorOrMemberOfGroup(final Group pGroup) {
		if (!isCurrentUserOwnerOrAdministratorOrMemberOfGroup(pGroup)) {
			throw new SecurityException("Current user is not member of group");
		}
	}
	
	public boolean isUserOwnerOfGroup(final Person pPerson, final Group pGroup) {
		CoreUtils.assertNotNull(pPerson);
		CoreUtils.assertNotNull(pGroup);
		
		return pPerson.equals(pGroup.getOwner());
	}
	
	public boolean isCurrentUserOwnerOfGroup(final Group pGroup) {
		return isUserOwnerOfGroup(getPersonService().getCurrentPerson(), pGroup);
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
		return isUserAdministratorOfGroup(getPersonService().getCurrentPerson(), pGroup);
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
		return isUserBannedByGroup(getPersonService().getCurrentPerson(), pGroup);
	}
	
	public void assertCurrentUserBannedByGroup(final Group pGroup) {
		if (!isCurrentUserBannedByGroup(pGroup)) {
			throw new SecurityException("Current user is not banned by group");
		}
	}

	/////////////////////////////////////////////////////////
	// Access control
	
	public boolean isCurrentUserAuthorizedToView(final Group pGroup) {
		return isUserAuthorizedToView(getPersonService().getCurrentPerson(), pGroup);
	}

	public boolean isUserAuthorizedToView(final Person pPerson, final Group pGroup) {
		return true;
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

	private boolean isUserAuthorizedToAddMember(Person pUser, Group pGroup, Person pMemberToAdd) {
		return pUser.isEnabled() && pMemberToAdd.isEnabled() &&
			((Boolean.FALSE.equals(pGroup.getValidateMembership()) && pUser.equals(pMemberToAdd)) ||
			 isUserAdministratorOfGroup(pUser, pGroup) ||
			 isUserOwnerOfGroup(pUser, pGroup));
	}
	
	private void assertUserAuthorizedToAddMember(final Person pUser, final Group pGroup, final Person pMemberToAdd) {
		if (!isUserAuthorizedToAddMember(pUser, pGroup, pMemberToAdd)) {
			throw new SecurityException("Current user is not authorized to remove member");
		}		
	}
	
	private void assertCurrentUserAuthorizedToAddMember(final Group pGroup, final Person pMemberToAdd) {
		assertUserAuthorizedToAddMember(getPersonService().getCurrentPerson(), pGroup, pMemberToAdd);		
	}
	
	private boolean isUserAuthorizedToRemoveMember(final Person pUser, final Group pGroup, final Person pMemberToAdd) {
		return pUser.isEnabled() && pMemberToAdd.isEnabled() &&
			(pUser.equals(pMemberToAdd) ||
			 isUserAdministratorOfGroup(pUser, pGroup) ||
			 isUserOwnerOfGroup(pUser, pGroup));
	}
	
	private void assertUserAuthorizedToRemoveMember(final Person pUser, final Group pGroup, final Person pMemberToAdd) {
		if (!isUserAuthorizedToRemoveMember(pUser, pGroup, pMemberToAdd)) {
			throw new SecurityException("Current user is not authorized to remove member");
		}		
	}
	
	private void assertCurrentUserAuthorizedToRemoveMember(final Group pGroup, final Person pMemberToAdd) {
		assertUserAuthorizedToRemoveMember(getPersonService().getCurrentPerson(), pGroup, pMemberToAdd);		
	}

	// Access control
	/////////////////////////////////////////////////////////
}

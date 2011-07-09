package com.pferrot.lendity.group.jsf;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.providers.encoding.MessageDigestPasswordEncoder;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.group.GroupService;
import com.pferrot.lendity.groupjoinrequest.GroupJoinRequestService;
import com.pferrot.lendity.groupjoinrequest.exception.GroupJoinRequestException;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.HtmlUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;
import com.pferrot.security.SecurityUtils;

@ViewController(viewIds={"/public/group/groupOverview.jspx"})
public class GroupOverviewController  {
	
	private final static Log log = LogFactory.getLog(GroupOverviewController.class);
	
	private Group group;
	private GroupService groupService;
	private GroupJoinRequestService groupJoinRequestService;
	private MessageDigestPasswordEncoder passwordEncoder;
	
	private Long groupId;
	
	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public GroupJoinRequestService getGroupJoinRequestService() {
		return groupJoinRequestService;
	}

	public void setGroupJoinRequestService(
			GroupJoinRequestService groupJoinRequestService) {
		this.groupJoinRequestService = groupJoinRequestService;
	}

	public MessageDigestPasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(MessageDigestPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	@InitView
	public void initView() {
		// Read the group ID from the request parameter and load the correct group.
		try {
			final String groupIdString = JsfUtils.getRequestParameter(PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID);
			Group group = null;
			if (groupIdString != null) {
				setGroupId(Long.parseLong(groupIdString));
				group = getGroupService().findGroup(getGroupId());
				// Access control check.
				if (!getGroupService().isCurrentUserAuthorizedToView(group)) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (group view): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), group = " + groupIdString);
					}
					return;
				}
				setGroup(group);
			}
		}
		catch (AccessDeniedException ade) {
			throw ade;
		}
		catch (Exception e) {
			//TODO display standard error page instead.
			JsfUtils.redirect(PagesURL.GROUPS_LIST);
		}		
	}

	public boolean isDescriptionAvailable() {
		final String itemDescription = getGroup().getDescription();
		return !StringUtils.isNullOrEmpty(itemDescription);
	}

	public String getDescription() {
		final String desc = getGroup().getDescription();
		if (desc != null) {
			return HtmlUtils.escapeHtmlAndReplaceCr(desc);
		}
		return "";
	}

	public String getOwnerHref() {
		return PersonUtils.getPersonOverviewPageUrl(getGroup().getOwner().getId().toString());
	}

	public String getCreationDateLabel() {
		return UiUtils.getDateAsString(getGroup().getCreationDate(), I18nUtils.getDefaultLocale());
	}

	public boolean isEditAvailable() {
		return getGroupService().isCurrentUserAuthorizedToEdit(getGroup());
	}
	
	public boolean isDeleteAvailable() {
		return getGroupService().isCurrentUserAuthorizedToDelete(getGroup());
	}

	public boolean isRequestJoinAvailable() {
		try {
			return SecurityUtils.isLoggedIn() &&
				   Boolean.TRUE.equals(getGroup().getValidateMembership()) &&
				   getGroupJoinRequestService().isGroupJoinRequestAllowedFromCurrentUser(getGroup());
		}
		catch (GroupJoinRequestException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean isUncompletedGroupJoinRequestAvailable() {
		return SecurityUtils.isLoggedIn() &&
			   getGroupJoinRequestService().isUncompletedGroupJoinRequestAvailableFromCurrentPerson(getGroup());
	}
	
	public boolean isBannedFromGroup() {
		return SecurityUtils.isLoggedIn() &&
		       getGroupService().isCurrentUserBannedByGroup(getGroup());
	}
	
	public boolean isJoinAvailable() {
			return SecurityUtils.isLoggedIn() &&
				   Boolean.FALSE.equals(getGroup().getValidateMembership()) &&
				   !getGroupService().isCurrentUserOwnerOrAdministratorOrMemberOfGroup(getGroup()) && 
				   !getGroupService().isCurrentUserBannedByGroup(getGroup());
	}
	
	public boolean isLeaveAvailable() {
		return SecurityUtils.isLoggedIn() &&
			   	(getGroupService().isCurrentUserMemberOfGroup(getGroup()) || 
			   	 getGroupService().isCurrentUserAdministratorOfGroup(getGroup()));
	}
	
	public String getGroupEditHref() {
		return JsfUtils.getFullUrl(PagesURL.GROUP_EDIT, PagesURL.GROUP_EDIT_PARAM_GROUP_ID, getGroup().getId().toString());
	}
	
	public String getGroupMembersUrl() {
		return JsfUtils.getFullUrl(PagesURL.GROUP_MEMBERS_LIST, PagesURL.GROUP_MEMBERS_LIST_PARAM_GROUP_ID, getGroupId().toString());
	}
	
	public String getGroupAdministratorsUrl() {
		return JsfUtils.getFullUrl(PagesURL.GROUP_ADMINISTRATORS_LIST, PagesURL.GROUP_ADMINISTRATORS_LIST_PARAM_GROUP_ID, getGroupId().toString());
	}
	
	public String getGroupBannedUrl() {
		return JsfUtils.getFullUrl(PagesURL.GROUP_BANNED_LIST, PagesURL.GROUP_BANNED_LIST_PARAM_GROUP_ID, getGroupId().toString());
	}

	public String getGroupPicture1Src() {
		if (FacesContext.getCurrentInstance().getRenderResponse()) {
			return groupService.getGroupPicture1Src(getGroup(), true);
		}
		return null;
	}
	
	public String getGroupThumbnail1Src() {
		if (FacesContext.getCurrentInstance().getRenderResponse()) {
			return groupService.getGroupThumbnail1Src(getGroup(), true);
		}
		return null;
	}
	
	public String getGroupEditPictureHref() {		
		return JsfUtils.getFullUrl(PagesURL.GROUP_EDIT_PICTURE, PagesURL.GROUP_EDIT_PICTURE_PARAM_GROUP_ID, getGroup().getId().toString());
	}
	
	public boolean isAuthorizedToAddComments() {
		return SecurityUtils.isLoggedIn() &&
		       getGroupService().isCurrentUserOwnerOrAdministratorOrMemberOfGroup(getGroup());
	}
	
	public String getPasswordEncoded() {
		if (getGroup().isPasswordProtected()) {
			return getPasswordEncoder().encodePassword(getGroup().getPassword(), null);
		}
		else {
			return "";
		} 
	}
	
	// Method must exists otherwise we get an exception when trying to join a group.
	public void setPasswordEncoded(final String pPasswordEncoded) {
		// Leave empty.
	}
}

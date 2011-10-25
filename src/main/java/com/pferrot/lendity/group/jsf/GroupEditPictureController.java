package com.pferrot.lendity.group.jsf;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.springframework.security.AccessDeniedException;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.group.GroupConsts;
import com.pferrot.lendity.group.GroupService;
import com.pferrot.lendity.image.jsf.AbstractEditPictureController;
import com.pferrot.lendity.item.ItemUtils;
import com.pferrot.lendity.model.Document;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/group/groupEditPicture.jspx"})
public class GroupEditPictureController  extends AbstractEditPictureController {
	
	private final static Log log = LogFactory.getLog(GroupEditPictureController.class);
	
	private static final int IMAGE_MAX_HEIGHT = 200;
	private static final int IMAGE_MAX_WIDTH = 200;
	
	private static final int THUMBNAIL_MAX_HEIGHT = 50;
	private static final int THUMBNAIL_MAX_WIDTH = 50;
	
	private GroupService groupService;
	
	private Group group;

	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	@InitView
	public void initView() {
		// Read the group ID from the request parameter and load the correct group.
		try {
			final String groupIdString = JsfUtils.getRequestParameter(PagesURL.GROUP_EDIT_PICTURE_PARAM_GROUP_ID);
			if (groupIdString != null) {
				final Long groupId = Long.parseLong(groupIdString);
				final Group group = getGroupService().findGroup(groupId);				
				// Access control check.
				if (!getGroupService().isCurrentUserAuthorizedToEdit(group)) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (group edit picture): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), group = " + groupIdString);
					}
					return;
				}
				setGroup(group);
			}
			// Group not found or no group ID specified.
			if (getGroup() == null) {
				JsfUtils.redirect(PagesURL.GROUPS_LIST);
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

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Long updateGroup() {
		try {				
			if (getTempFileLocation() != null) {
				getGroupService().updateGroupPicture(getGroup(), getImageDocumentFromTempFile(), getThumbnailDocumentFromTempFile());
			}
			else if (getTempThumbnailLocation() != null) {
				getGroupService().updateGroupThumbnail(getGroup(), getThumbnailDocumentFromTempFile());
			}
			else if (Boolean.TRUE.equals(getRemoveCurrentImage())) {
				getGroupService().updateGroupPicture(getGroup(), null, null);
			}			
			return getGroup().getId();
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	
	public String getMyProfileHref() {
		return JsfUtils.getFullUrl(PagesURL.MY_PROFILE);
	}

	public String submit() {
		try {
			processThumbnailSelection();
			Long groupId = updateGroup();
			
			if (groupId != null) {
				JsfUtils.redirect(PagesURL.GROUP_OVERVIEW, PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID, groupId.toString());
			}
		
			// Return to the same page.
			return "error";
		}
		catch (Exception e) {
			return "error";
		}
	}
	
	public String getImgSrc() {
		if (Boolean.TRUE.equals(getRemoveCurrentImage())) {
			return JsfUtils.getFullUrl(GroupConsts.DUMMY_GROUP_PICTURE_URL);
		}
		else if (getTempFileLocation() != null) {
			return getTempFileImgSrc();
		}
		else {
			return groupService.getGroupPicture1Src(getGroup(), true);
		}
	}
	
	public String getThumbnailSrc() {
		if (Boolean.TRUE.equals(getRemoveCurrentImage())) {
			return JsfUtils.getFullUrl(GroupConsts.DUMMY_GROUP_THUMBNAIL_URL);
		}
		else if (getTempThumbnailLocation() != null) {
			return getTempThumbnailImgSrc();			
		}
		else {
			return groupService.getGroupThumbnail1Src(getGroup(), true);
		}
	}

	@Override
	protected int getImageMaxHeight() {
		return IMAGE_MAX_HEIGHT;
	}

	@Override
	protected int getImageMaxWidth() {
		return IMAGE_MAX_WIDTH;
	}

	@Override
	protected int getThumbnailMaxHeight() {
		return THUMBNAIL_MAX_HEIGHT;
	}

	@Override
	protected int getThumbnailMaxWidth() {
		return THUMBNAIL_MAX_WIDTH;
	}
	
	public String getGroupOverviewHref() {		
		return JsfUtils.getFullUrl(PagesURL.GROUP_OVERVIEW, PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID, getGroup().getId().toString());
	}

	@Override
	public String getCancelHref() {
		return getGroupOverviewHref();
	}

	@Override
	public boolean isExistingImage() {
		return getGroup().getImage1() != null;
	}

	@Override
	protected Document getCurrentImage() {
		return getGroup().getImage1();
	}
}

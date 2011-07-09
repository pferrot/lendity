package com.pferrot.lendity.group.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/group/groupEdit.jspx"})
public class GroupEditController extends AbstractGroupAddEditController {
	
	private final static Log log = LogFactory.getLog(GroupEditController.class);
	
	private Group group;

	public Group getGroup() {
		return group;
	}

	public void setGroup(final Group pGroup) {
		this.group = pGroup;
		
		// Initialize the model to be edited.
		setTitle(pGroup.getTitle());
		setDescription(pGroup.getDescription());
		setValidateMembership(pGroup.getValidateMembership());
		setPassword(pGroup.getPassword());
	}

	@InitView
	public void initView() {
		// Read the group ID from the request parameter and load the correct group.
		try {
			final String groupIdString = JsfUtils.getRequestParameter(PagesURL.GROUP_EDIT_PARAM_GROUP_ID);
			Group group = null;
			if (groupIdString != null) {
				group = getGroupService().findGroup(Long.parseLong(groupIdString));
				// Access control check.
				if (!getGroupService().isCurrentUserAuthorizedToEdit(group)) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (group edit): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), group = " + groupIdString);
					}
					return;
				}
				else {
					setGroup(group);
				}
			}
			// Group not found or no group ID specified.
			if (getGroup() == null) {
				JsfUtils.redirect(PagesURL.GROUPS_LIST);
				return;
			}			
		}
		catch (Exception e) {
			//TODO display standard error page instead.
			JsfUtils.redirect(PagesURL.GROUPS_LIST);
		}		
	}
	
	public Long updateGroup() {		
		getGroup().setTitle(getTitle());
		getGroup().setDescription(getDescription());
		getGroup().setValidateMembership(getValidateMembership());
		getGroup().setPassword(getPassword());
		getGroupService().updateGroup(getGroup());

		return getGroup().getId();
	}
	
	@Override
	public Long processGroup() {
		return updateGroup();
	}
	
	public String getGroupOverviewHref() {		
		return JsfUtils.getFullUrl(PagesURL.GROUP_OVERVIEW, PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID, getGroup().getId().toString());
	}
}

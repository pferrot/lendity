package com.pferrot.lendity.need.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.springframework.security.AccessDeniedException;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.group.GroupService;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/public/need/groupNeedsList.jspx"})
public class GroupNeedsListController extends AbstractNeedsListController {
	
	private final static Log log = LogFactory.getLog(GroupNeedsListController.class);
	
	private GroupService groupService;
	
	private Long groupId;
	private String groupTitle;
	
	public GroupNeedsListController() {
		super();
	}

	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupTitle() {
		return groupTitle;
	}

	public void setGroupTitle(String groupTitle) {
		this.groupTitle = groupTitle;
	}

	@InitView
	public void initView() {
		try {
			final String groupIdString = JsfUtils.getRequestParameter(PagesURL.GROUP_NEEDS_LIST_PARAM_GROUP_ID);
			if (!StringUtils.isNullOrEmpty(groupIdString)) {			
				resetFilters();
				setGroupId(Long.valueOf(groupIdString));
				if (!getGroupService().isCurrentUserOwnerOrAdministratorOrMemberOfGroup(getGroupId())) {
					throw new AccessDeniedException("Only members can access that page.");
				}
				setGroupTitle(getGroupService().findGroupTitle(getGroupId()));
			}
			super.initView();
		}
		catch (Exception e) {
			JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
		}
	}

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getNeedService().findGroupNeeds(getGroupId(), getSearchString(), getCategoryId(), 
				getOrderByField(), getOrderByAscending(), getFirstRow(), getRowsPerPage());
	}

	public String getGroupUrl() {
		return JsfUtils.getFullUrl(
				PagesURL.GROUP_OVERVIEW,
				PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID,
				getGroupId().toString());
	}
	
	@Override
	protected String getCategoryLinkBaseUrl() {
		return JsfUtils.getFullUrl(PagesURL.GROUP_NEEDS_LIST, PagesURL.NEEDS_SEARCH_PARAM_CATEGORY_ID, PagesURL.NEEDS_SEARCH_PARAM_CATEGORY_ID_TO_REPLACE);
	}
}
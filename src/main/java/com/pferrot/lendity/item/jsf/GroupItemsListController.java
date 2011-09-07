package com.pferrot.lendity.item.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.springframework.security.AccessDeniedException;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.group.GroupService;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

@ViewController(viewIds={"/public/item/groupItemsList.jspx"})
public class GroupItemsListController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(GroupItemsListController.class);
	
	private GroupService groupService;
	
	private Long groupId;
	private String groupTitle;
	
	public GroupItemsListController() {
		super();
		// Display available items by default.
		//setBorrowStatus(UiUtils.getLongFromBoolean(Boolean.FALSE));
	}

	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public String getGroupTitle() {
		return groupTitle;
	}

	public void setGroupTitle(String groupTitle) {
		this.groupTitle = groupTitle;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	@InitView
	public void initView() {
		try {
			final String groupIdString = JsfUtils.getRequestParameter(PagesURL.GROUP_ITEMS_LIST_PARAM_GROUP_ID);
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
		return getItemService().findGroupItems(getGroupId(), getSearchString(), getCategoryId(), 
				getBorrowStatusBoolean(), getOrderByField(), getOrderByAscending(), getFirstRow(), getRowsPerPage());
	}

	@Override
	public List<SelectItem> getBorrowStatusSelectItems() {
		if (getBorrowStatusSelectItemsInternal() == null) {
			final List<SelectItem> result = new ArrayList<SelectItem>();
			final Locale locale = I18nUtils.getDefaultLocale();
			
			result.add(new SelectItem(UiUtils.getLongFromBoolean(null), I18nUtils.getMessageResourceString("item_availableStatusAll", locale)));
			result.add(new SelectItem(UiUtils.getLongFromBoolean(Boolean.FALSE), I18nUtils.getMessageResourceString("item_availableStatusAvailable", locale)));
			result.add(new SelectItem(UiUtils.getLongFromBoolean(Boolean.TRUE), I18nUtils.getMessageResourceString("item_availableStatusNotAvailable", locale)));
			
			setBorrowStatusSelectItemsInternal(result);
		}		
		return getBorrowStatusSelectItemsInternal();	
	}

	public String getGroupUrl() {
		return JsfUtils.getFullUrl(
				PagesURL.GROUP_OVERVIEW,
				PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID,
				getGroupId().toString());
	}
	
	@Override
	protected String getCategoryLinkBaseUrl() {
		return JsfUtils.getFullUrl(PagesURL.GROUP_ITEMS_LIST, PagesURL.ITEMS_SEARCH_PARAM_CATEGORY_ID, PagesURL.ITEMS_SEARCH_PARAM_CATEGORY_ID_TO_REPLACE);
	}
}
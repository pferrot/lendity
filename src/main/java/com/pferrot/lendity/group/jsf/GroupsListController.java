package com.pferrot.lendity.group.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/public/group/groupsList.jspx"})
public class GroupsListController extends AbstractGroupsListController {
	
	private final static Log log = LogFactory.getLog(GroupsListController.class);
	
	public final static String SEARCH_TEXT_PARAM_NAME = "search";
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getGroupService().findGroups (getSearchString(), getFirstRow(), getRowsPerPage());
	}
	
	@InitView
	public void initView() {
		final String searchString = JsfUtils.getRequestParameter(SEARCH_TEXT_PARAM_NAME);
		if (!StringUtils.isNullOrEmpty(searchString)) {
			resetFilters();
			setSearchString(searchString);
			return;
		}
	}
}

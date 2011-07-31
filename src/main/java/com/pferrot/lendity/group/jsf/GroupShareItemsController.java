package com.pferrot.lendity.group.jsf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.group.GroupService;
import com.pferrot.lendity.i18n.SelectItemComparator;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.item.jsf.AbstractObjektOverviewController;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/group/groupShareItems.jspx"})
public class GroupShareItemsController {
	
	private final static Log log = LogFactory.getLog(AbstractObjektOverviewController.class);
	
	private GroupService groupService;
	private ItemService itemService;
	private Group group;
	private List<SelectItem> itemsSelectItems;
	private List<Long> sharedItemsIds;
	private List<Long> originalSharedItemsIds;
	
	
	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public ItemService getItemService() {
		return itemService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
		
		setSharedItemsIdsFromCurrentPerson();
	}
	
	public List<SelectItem> getItemsSelectItems() {
		if (itemsSelectItems == null) {
			Set<Item> items = new HashSet<Item>();	
			
			items.addAll(getItemService().findAllItemsForPerson(PersonUtils.getCurrentPersonId()));
			
			final TreeSet<SelectItem> treeSet = new TreeSet<SelectItem>(new SelectItemComparator());
			for (Item item: items) {
				final SelectItem selectItem = new SelectItem(item.getId(), item.getTitle());
				treeSet.add(selectItem);
			}
			itemsSelectItems = new ArrayList<SelectItem>();
			itemsSelectItems.addAll(treeSet);
		}		
		return itemsSelectItems;	
	}

	public List<Long> getSharedItemsIds() {
		return sharedItemsIds;
	}

	public void setSharedItemsIds(List<Long> sharedItemsIds) {
		this.sharedItemsIds = sharedItemsIds;
	}

	protected void setSharedItemsIdsFromCurrentPerson() {
		List<Item> items = getItemService().findAllItemsForPerson(PersonUtils.getCurrentPersonId());
		final List<Long> sharedItemsIds = new ArrayList<Long>();
		if (items != null) {
			for (Item item: items) {
				if (item.getGroupsAuthorized().contains(getGroup())) {
					sharedItemsIds.add(item.getId());
				}
			}
		}
		setSharedItemsIds(sharedItemsIds);
		originalSharedItemsIds = new ArrayList<Long>();
		originalSharedItemsIds.addAll(getSharedItemsIds());
	}
	
	protected void updateItems() {
		// Newly shared items.
		for (Long itemId: sharedItemsIds) {
			if (!originalSharedItemsIds.contains(itemId)) {
				final Item item = getItemService().findItem(itemId);
				if (!item.getOwner().getId().equals(PersonUtils.getCurrentPersonId())) {
					throw new SecurityException("Not the owner trying to modify");
				}
				item.getGroupsAuthorized().add(getGroup());
				getItemService().updateItem(item);
			}
		}
		
		// Items that are not shared anymore.
		for (Long itemId: originalSharedItemsIds) {
			if (!sharedItemsIds.contains(itemId)) {
				final Item item = getItemService().findItem(itemId);
				if (!item.getOwner().getId().equals(PersonUtils.getCurrentPersonId())) {
					throw new SecurityException("Not the owner trying to modify");
				}
				item.getGroupsAuthorized().remove(getGroup());
				getItemService().updateItem(item);
			}
		}		
	}
	
	public String submit() {
		updateItems();
		
		JsfUtils.redirect(PagesURL.GROUP_OVERVIEW, PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID, getGroup().getId().toString());
	
		// As a redirect is used, this is actually useless.
		return null;
	}
	
	public String getGroupOverviewHref() {		
		return JsfUtils.getFullUrl(PagesURL.GROUP_OVERVIEW, PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID, getGroup().getId().toString());
	}
	
	@InitView
	public void initView() {
		// Read the group ID from the request parameter and load the correct group.
		try {
			final String groupIdString = JsfUtils.getRequestParameter(PagesURL.GROUP_SHARE_ITEMS_PARAM_GROUP_ID);
			Group group = null;
			if (groupIdString != null) {
				group = getGroupService().findGroup(Long.parseLong(groupIdString));
				// Access control check.
				if (!getGroupService().isCurrentUserOwnerOrAdministratorOrMemberOfGroup(group)) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (group share items): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), group = " + groupIdString);
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

}

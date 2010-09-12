package com.pferrot.lendity.lendrequest.jsf;

import javax.faces.context.FacesContext;

import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.item.ItemUtils;
import com.pferrot.lendity.jsf.list.AbstractListController;
import com.pferrot.lendity.lendrequest.LendRequestConsts;
import com.pferrot.lendity.lendrequest.LendRequestService;
import com.pferrot.lendity.model.LendRequest;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.UiUtils;

public abstract class AbstractLendRequestsListController extends AbstractListController {
	
	private LendRequestService lendRequestService;
	private ItemService itemService;

	
	public AbstractLendRequestsListController() {
		super();
		setRowsPerPage(LendRequestConsts.NB_LEND_REQUESTS_PER_PAGE);
	}

	public LendRequestService getLendRequestService() {
		return lendRequestService;
	}

	public ItemService getItemService() {
		return itemService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public void setLendRequestService(LendRequestService lendRequestService) {
		this.lendRequestService = lendRequestService;
	}

	public String getRequesterOverviewHref() {
		final LendRequest lendRequest = (LendRequest)getTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(lendRequest.getRequester().getId().toString());
	}
	
	public String getOwnerOverviewHref() {
		final LendRequest lendRequest = (LendRequest)getTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(lendRequest.getOwner().getId().toString());
	}

	public String getItemOverviewHref() {
		final LendRequest lendRequest = (LendRequest)getTable().getRowData();
		return ItemUtils.getInternalItemOverviewPageUrl(lendRequest.getItem().getId().toString());
	}
	
	public String getRequestDateLabel() {
		final LendRequest lendRequest = (LendRequest)getTable().getRowData();
		return UiUtils.getDateAsString(lendRequest.getRequestDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}

	public String getImage1Src() {
		final LendRequest lendRequest = (LendRequest)getTable().getRowData();
		return getItemService().getItemPicture1Src(lendRequest.getItem(), true);
	}
	
	public String getThumbnail1Src() {
		final LendRequest lendRequest = (LendRequest)getTable().getRowData();
		return getItemService().getItemThumbnail1Src(lendRequest.getItem(), true);
	}
}

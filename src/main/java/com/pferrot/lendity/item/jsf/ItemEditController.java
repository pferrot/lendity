package com.pferrot.lendity.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.item.ItemUtils;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/item/itemEdit.jspx"})
public class ItemEditController extends AbstractItemAddEditController {
	
	private final static Log log = LogFactory.getLog(ItemEditController.class);
	
	private Item item;

	@InitView
	public void initView() {
		// Read the item ID from the request parameter and load the correct item.
		try {
			final String itemIdString = JsfUtils.getRequestParameter(PagesURL.ITEM_EDIT_PARAM_ITEM_ID);
			Item item = null;
			if (itemIdString != null) {
				item = getItemService().findItem(Long.parseLong(itemIdString));
				// Access control check.
				if (!getItemService().isCurrentUserAuthorizedToEdit(item)) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (item edit): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), item = " + itemIdString);
					}
					return;
				}
				else {
					setItem(item);
				}
			}
			// Item not found or no item ID specified.
			if (getItem() == null) {
				JsfUtils.redirect(PagesURL.ITEMS_LIST);
				return;
			}
		}
		catch (Exception e) {
			//TODO display standard error page instead.
			JsfUtils.redirect(PagesURL.ITEMS_LIST);
		}		
	}

	public Item getItem() {
		return item;
	}
	
	private void setItem(final Item pItem) {
		item = pItem;

		// Initialize the model to be edited.
		setTitle(pItem.getTitle());
		setDescription(pItem.getDescription());
		setCategoriesIdsFromObjekt(pItem);
		setVisibilityId(pItem.getVisibility().getId());
		setDeposit(pItem.getDeposit());
		setRentalFee(pItem.getRentalFee());
		setSalePrice(pItem.getSalePrice());
		setToGiveForFree(pItem.getToGiveForFree());
		setAuthorizedGroupsIdsFromObjekt(pItem);		
	}	

	public Long updateItem() {		
		getItem().setTitle(getTitle());
		getItem().setDescription(getDescription());
		getItem().setDeposit(getDeposit());
		getItem().setRentalFee(getRentalFee());
		getItem().setToGiveForFree(getToGiveForFree());
		getItem().setSalePrice(getSalePrice());
		getItemService().updateItem(getItem(), getCategoriesIds(), getVisibilityId(), getAuthorizedGroupsIds());

		return getItem().getId();
	}

	public String getItemOverviewHref() {		
		return ItemUtils.getItemOverviewPageUrl(item.getId().toString());
	}	

	@Override
	public Long processItem() {
		return updateItem();
	}	
}

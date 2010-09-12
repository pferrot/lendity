package com.pferrot.lendity.item.jsf;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.item.ItemUtils;
import com.pferrot.lendity.model.InternalItem;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.image.jsf.AbstractEditPictureController;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/item/internalItemEditPicture.jspx"})
public class InternalItemEditPictureController  extends AbstractEditPictureController {
	
	private final static Log log = LogFactory.getLog(InternalItemEditPictureController.class);
	
	private static final int IMAGE_MAX_HEIGHT = 550;
	private static final int IMAGE_MAX_WIDTH = 550;
	
	private static final int THUMBNAIL_MAX_HEIGHT = 45;
	private static final int THUMBNAIL_MAX_WIDTH = 45;
	
	private ItemService itemService;
	
	private InternalItem item;

	public ItemService getItemService() {
		return itemService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	@InitView
	public void initView() {
		// Read the item ID from the request parameter and load the correct item.
		try {
			final String itemIdString = JsfUtils.getRequestParameter(PagesURL.INTERNAL_ITEM_EDIT_PICTURE_PARAM_ITEM_ID);
			if (itemIdString != null) {
				setItem(getItemService().findInternalItem(Long.parseLong(itemIdString)));
				// Access control check.
				if (!getItemService().isCurrentUserAuthorizedToEdit(getItem())) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (item edit): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), item = " + itemIdString);
					}
					return;
				}
			}
			// Item not found or no item ID specified.
			if (getItem() == null) {
				JsfUtils.redirect(PagesURL.ITEMS_LIST);
			}
		}
		catch (Exception e) {
			//TODO display standard error page instead.
			JsfUtils.redirect(PagesURL.ITEMS_LIST);
		}		
	}

	public InternalItem getItem() {
		return item;
	}

	public void setItem(InternalItem item) {
		this.item = item;
	}

	public Long updateItem() {
		try {				
			if (getTempFileLocation() != null) {
				getItemService().updateItemPicture1(getItem(), getImageDocumentFromTempFile(), getThumbnailDocumentFromTempFile());
			}
			else if (Boolean.TRUE.equals(getRemoveCurrentImage())) {
				getItemService().updateItemPicture1(getItem(), null, null);
			}			
			return getItem().getId();
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	
	public String getItemOverviewHref() {		
		return ItemUtils.getInternalItemOverviewPageUrl(item.getId().toString());
	}

	public String submit() {
		Long itemId = updateItem();
		
		if (itemId != null) {
			JsfUtils.redirect(PagesURL.INTERNAL_ITEM_OVERVIEW, PagesURL.INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID, itemId.toString());
		}
	
		// Return to the same page.
		return "error";
	}
	
	public String getImgSrc() {
		if (Boolean.TRUE.equals(getRemoveCurrentImage())) {
			return null;
		}
		else if (getTempFileLocation() != null) {
			return getTempFileImgSrc();
		}
		else {
			return itemService.getItemPicture1Src(getItem(), true);
		}
	}
	
	public String getThumbnailSrc() {
		if (Boolean.TRUE.equals(getRemoveCurrentImage())) {
			return null;
		}
		else if (getTempThumbnailLocation() != null) {
			return getTempThumbnailImgSrc();			
		}
		else {
			return itemService.getItemThumbnail1Src(getItem(), true);
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
	
	@Override
	public String getCancelHref() {
		return getItemOverviewHref();
	}
	
	@Override
	public boolean isExistingImage() {
		return getItem().getImage1() != null;
	}
}

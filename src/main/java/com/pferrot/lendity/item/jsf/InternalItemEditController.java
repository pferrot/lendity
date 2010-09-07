package com.pferrot.lendity.item.jsf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.hibernate.Hibernate;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.document.DocumentConsts;
import com.pferrot.lendity.document.DocumentUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemUtils;
import com.pferrot.lendity.model.Document;
import com.pferrot.lendity.model.InternalItem;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/item/internalItemEdit.jspx"})
public class InternalItemEditController extends AbstractInternalItemAddEditController {
	
	private final static Log log = LogFactory.getLog(InternalItemEditController.class);
	
	private InternalItem item;

	@InitView
	public void initView() {
		// Read the item ID from the request parameter and load the correct item.
		try {
			final String itemIdString = JsfUtils.getRequestParameter(PagesURL.INTERNAL_ITEM_EDIT_PARAM_ITEM_ID);
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
	
	private void setItem(final InternalItem pItem) {
		item = pItem;

		// Initialize the model to be edited.
		setTitle(pItem.getTitle());
		setDescription(pItem.getDescription());
		setCategoryId(pItem.getCategory().getId());
		setVisible(pItem.getVisible());
	}	

	public Long updateItem() {
		try {
			getItem().setTitle(getTitle());
			getItem().setDescription(getDescription());
			getItem().setVisible(getVisible());
			
			Document image1 = null;
			
			if (getImageFile1() != null) {
				final String mimeType = getImageFile1().getContentType();
				if (! DocumentUtils.isSupportedImageMimeType(mimeType)) {
					errorImageFile();
					return null;
				}
				final long size = getImageFile1().getSize();
				if (size > DocumentConsts.MAX_ITEM_IMAGE_SIZE) {
					errorImageFile();
					return null;
				}
				image1 = new Document();
				image1.setMimeType(getImageFile1().getContentType());
				image1.setSize(getImageFile1().getSize());
				image1.setName(getImageFile1().getName());
				image1.setInputStream(getImageFile1().getInputStream());
			}
			
			getItemService().updateItemWithCategory(getItem(), getCategoryId(), image1);
	
			return getItem().getId();
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}


	public String getItemOverviewHref() {		
		return ItemUtils.getInternalItemOverviewPageUrl(item.getId().toString());
	}	

	@Override
	public Long processItem() {
		return updateItem();
	}	
}

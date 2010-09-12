package com.pferrot.lendity.item;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.utils.JsfUtils;

public class ItemUtils {

	/**
	 * Returns the HTML link to an internal item overview page.
	 * 
	 * @param pItemId
	 * @return
	 */
	public static String getInternalItemOverviewPageUrl(final String pItemId) {
		CoreUtils.assertNotNull(pItemId);
		
		return JsfUtils.getFullUrl(PagesURL.INTERNAL_ITEM_OVERVIEW, PagesURL.INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID, pItemId);
	}
	
	/**
	 * 
	 * @param pItemId
	 * @return
	 */
	public static String getInternalItemEditPageUrl(final String pItemId) {
		CoreUtils.assertNotNull(pItemId);
		
		return JsfUtils.getFullUrl(PagesURL.INTERNAL_ITEM_EDIT, PagesURL.INTERNAL_ITEM_EDIT_PARAM_ITEM_ID, pItemId);
	}
	
	/**
	 * 
	 * @param pItemId
	 * @return
	 */
	public static String getInternalItemEditPicturePageUrl(final String pItemId) {
		CoreUtils.assertNotNull(pItemId);
		
		return JsfUtils.getFullUrl(PagesURL.INTERNAL_ITEM_EDIT_PICTURE, PagesURL.INTERNAL_ITEM_EDIT_PICTURE_PARAM_ITEM_ID, pItemId);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getInternalItemAddPageUrl() {		
		return JsfUtils.getFullUrl(PagesURL.INTERNAL_ITEM_ADD);
	}

	/**
	 * Returns the HTML link to an external item overview page.
	 * 
	 * @param pItemId
	 * @return
	 */
	public static String getExternalItemOverviewPageUrl(final String pItemId) {
		CoreUtils.assertNotNull(pItemId);
		
		return JsfUtils.getFullUrl(PagesURL.EXTERNAL_ITEM_OVERVIEW, PagesURL.EXTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID, pItemId);
	}
	
	/**
	 * 
	 * @param pItemId
	 * @return
	 */
	public static String getExternalItemEditPageUrl(final String pItemId) {
		CoreUtils.assertNotNull(pItemId);
		
		return JsfUtils.getFullUrl(PagesURL.EXTERNAL_ITEM_EDIT, PagesURL.EXTERNAL_ITEM_EDIT_PARAM_ITEM_ID, pItemId);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getExternalItemAddPageUrl() {		
		return JsfUtils.getFullUrl(PagesURL.EXTERNAL_ITEM_ADD);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getItemsListUrl() {		
		return JsfUtils.getFullUrl(PagesURL.ITEMS_LIST);
	}
}

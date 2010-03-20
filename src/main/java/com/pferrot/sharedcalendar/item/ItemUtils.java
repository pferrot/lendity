package com.pferrot.sharedcalendar.item;

import com.pferrot.core.CoreUtils;
import com.pferrot.sharedcalendar.PagesURL;
import com.pferrot.sharedcalendar.utils.JsfUtils;

public class ItemUtils {

	/**
	 * Returns the HTML link to an item overview page.
	 * 
	 * @param pItemId
	 * @return
	 */
	public static String getItemOverviewPageUrl(final String pItemId) {
		CoreUtils.assertNotNull(pItemId);
		
		return JsfUtils.getFullUrl(PagesURL.ITEM_OVERVIEW, PagesURL.ITEM_OVERVIEW_PARAM_ITEM_ID, pItemId);
	}
	
	/**
	 * 
	 * @param pItemId
	 * @return
	 */
	public static String getItemEditPageUrl(final String pItemId) {
		CoreUtils.assertNotNull(pItemId);
		
		return JsfUtils.getFullUrl(PagesURL.ITEM_EDIT, PagesURL.ITEM_EDIT_PARAM_ITEM_ID, pItemId);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getItemAddPageUrl() {		
		return JsfUtils.getFullUrl(PagesURL.ITEM_ADD);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getItemsListUrl() {		
		return JsfUtils.getFullUrl(PagesURL.ITEMS_LIST);
	}
}

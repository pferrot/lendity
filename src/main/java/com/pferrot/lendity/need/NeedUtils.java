package com.pferrot.lendity.need;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.utils.JsfUtils;

public class NeedUtils {

	/**
	 * Returns the HTML link to a need overview page.
	 * 
	 * @param pNeedId
	 * @return
	 */
	public static String getNeedOverviewPageUrl(final String pNeedId) {
		CoreUtils.assertNotNull(pNeedId);
		
		return JsfUtils.getFullUrl(PagesURL.NEED_OVERVIEW, PagesURL.NEED_OVERVIEW_PARAM_NEED_ID, pNeedId);
	}
	
	/**
	 * 
	 * @param pNeedId
	 * @return
	 */
	public static String getNeedEditPageUrl(final String pNeedId) {
		CoreUtils.assertNotNull(pNeedId);
		
		return JsfUtils.getFullUrl(PagesURL.NEED_EDIT, PagesURL.NEED_EDIT_PARAM_NEED_ID, pNeedId);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getNeedAddPageUrl() {		
		return JsfUtils.getFullUrl(PagesURL.NEED_ADD);
	}

	/**
	 * 
	 * @return
	 */
	public static String getMyNeedsListUrl() {		
		return JsfUtils.getFullUrl(PagesURL.MY_NEEDS_LIST);
	}

	/**
	 * 
	 * @return
	 */
	public static String getMyConnectionsNeedsListUrl() {		
		return JsfUtils.getFullUrl(PagesURL.MY_CONNECTIONS_NEEDS_LIST);
	}
}

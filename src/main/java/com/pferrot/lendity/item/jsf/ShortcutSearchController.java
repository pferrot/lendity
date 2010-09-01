package com.pferrot.lendity.item.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.utils.JsfUtils;

public class ShortcutSearchController implements Serializable {
	
	private final static Log log = LogFactory.getLog(ShortcutSearchController.class);
	
	private String searchString;
	
	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public String submit() {
		JsfUtils.redirect(PagesURL.MY_CONNECTIONS_ITEMS_LIST, 
				          MyConnectionsItemsListController.SEARCH_TEXT_PARAM_NAME,
				          searchString);
		return null;
	}
}
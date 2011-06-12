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
		return submitHomeSearchItems();
	}
	
	public String submitHomeSearchItems() {
		JsfUtils.redirect(PagesURL.ITEMS_SEARCH, 
						  SearchItemsListController.SEARCH_TEXT_PARAM_NAME,
				          searchString);
		return null;
	}
	
	public String submitHomeSearchNeeds() {
		JsfUtils.redirect(PagesURL.NEEDS_SEARCH, 
						  SearchItemsListController.SEARCH_TEXT_PARAM_NAME,
				          searchString);
		return null;
	}
	
	public String submitHomeSearchPersons() {
		JsfUtils.redirect(PagesURL.PERSONS_LIST, 
						  SearchItemsListController.SEARCH_TEXT_PARAM_NAME,
				          searchString);
		return null;
	}
	
	public String submitHomeSearchGroups() {
		JsfUtils.redirect(PagesURL.GROUPS_LIST, 
						  SearchItemsListController.SEARCH_TEXT_PARAM_NAME,
				          searchString);
		return null;
	}
}

package com.pferrot.sharedcalendar.jsf.list;

import java.util.List;

import javax.faces.component.html.HtmlDataTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractListController {
	private final static Log log = LogFactory.getLog(AbstractListController.class);
	
	private List<Object> listInternal;
	private HtmlDataTable table;
	private int firstResultIndex = 0;
	private String searchString = null;
	
	public abstract List<Object> getListInternal();
	public abstract int getNbEntriesPerPage();
	
	public List getList() {
		// Keep a member variable so that the DB is not accessed everytime.
		listInternal = getListInternal();
		if (isNextPage()) {
			return listInternal.subList(0, getNbEntriesPerPage());
		}
		else {
			return listInternal;
		}		
	}

	public int getFirstResultIndex() {
		return firstResultIndex;
	}

	public void setFirstResultIndex(int firstResultIndex) {
		this.firstResultIndex = firstResultIndex;
	}

	public void setTable(final HtmlDataTable pTable) {
		this.table = pTable;
	}

	public HtmlDataTable getTable() {
		return table;
	}	
	
	public boolean isNextPage() {
		return listInternal.size() > getNbEntriesPerPage();
	}
	
	public boolean isPreviousPage() {
		return getFirstResultIndex() > 0;
	}	
	
	public String nextPage() {
		if (isNextPage()) {
			setFirstResultIndex(getFirstResultIndex() + getNbEntriesPerPage());
		}
		return "nextPage";
	}
	
	public String previousPage() {
		if (isPreviousPage()) {
			setFirstResultIndex(getFirstResultIndex() - getNbEntriesPerPage());
		}
		return "previousPage";
	}
	
	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(final String pSearchString) {
		this.searchString = pSearchString;
		// If the search string changes, go the the first page.
		setFirstResultIndex(0);
	}
	
	public String search() {
		return "search";
	}
	
	public String clearSearch() {
		setSearchString(null);
		return "clearSearch";
	}		
}

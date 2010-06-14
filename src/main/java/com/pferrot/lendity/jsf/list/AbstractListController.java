package com.pferrot.lendity.jsf.list;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.dao.bean.ListWithRowCount;

/**
 * See http://balusc.blogspot.com/2008/10/effective-datatable-paging-and-sorting.html
 * 
 * @author pferrot
 *
 */
public abstract class AbstractListController implements Serializable {
	private final static Log log = LogFactory.getLog(AbstractListController.class);
	
    private List list;

	private HtmlDataTable table;
	
    // Paging.
	private int totalRows;
    private int firstRow;
    private int rowsPerPage;
    private int totalPages;
    private int pageRange;
    private Integer[] pages;
    private int currentPage;
	private Integer goToPageNumber;
	private List<SelectItem> goToPageNumberSelectItems;
//	private boolean emptyList;
	
    // Search
	private String searchString;

	public AbstractListController() {
		super();
		// Default rows per page (max amount of rows to be displayed at once).
		rowsPerPage = 10;
		// Default page range (max amount of page links to be displayed at once).
		pageRange = 10;
	}

	protected abstract ListWithRowCount getListWithRowCount();	
	
	
	// Keep a member variable so that the DB is not accessed everytime.
//	private List<Object> getOrLoadListInternal() {
//		if (listInternal == null) {
//			listInternal = getListInternal();
//		}
//		return listInternal;
//	}
//	
//	protected void forceReloadList() {
//		listInternal = null;
//	}


	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(final String pSearchString) {
		setSearchString(pSearchString, true);
	}
	
	private void setSearchString(final String pSearchString, final boolean pReloadList) {
		this.searchString = pSearchString;
		if (pReloadList) {
			reloadList();
		}
	}
	
	public String search() {
		return "search";
	}
	
	public String clearSearch() {
		setSearchString(null, false);
		return "clearSearch";
	}
	
	public boolean isFilteredList() {
		return !StringUtils.isNullOrEmpty(getSearchString());
	}
	
	
	 // Paging actions -----------------------------------------------------------------------------

    public void pageFirst() {
        page(0);
    }

    public void pageNext() {
        page(firstRow + rowsPerPage);
    }

    public void pagePrevious() {
        page(firstRow - rowsPerPage);
    }

    public void pageLast() {
        page(totalRows - ((totalRows % rowsPerPage != 0) ? totalRows % rowsPerPage : rowsPerPage));
    }

    public void page(final ValueChangeEvent event) {
        page(((Integer) ((HtmlSelectOneMenu) event.getComponent()).getValue() - 1) * rowsPerPage);
        // loadDataList() is not called by getList() when the page is submitted with the onchange
        // event on the h:selectOneMenu. Not sure why!?
        loadDataList();
    }

    protected void page(int firstRow) {
        this.firstRow = firstRow;
        // Load requested page.
//        loadDataList();
    }

    protected void loadDataList() {
    	
    	if (log.isDebugEnabled()) {
    		log.debug("Loading list");
    	}

        // Load list and totalCount.
    	final ListWithRowCount listWithRowCount = getListWithRowCount();
        list = listWithRowCount.getList();
//        emptyList = list == null || list.isEmpty();
        totalRows = (int)listWithRowCount.getRowCount();

        // Set currentPage, totalPages and pages.
        currentPage = (totalRows / rowsPerPage) - ((totalRows - firstRow) / rowsPerPage) + 1;
        totalPages = (totalRows / rowsPerPage) + ((totalRows % rowsPerPage != 0) ? 1 : 0);
        // Still display page 1 (and not 0) if no row.
        if (totalPages < 1) {
        	currentPage = 1;
        	totalPages = 1;        	
        }
        goToPageNumber = Integer.valueOf(currentPage);
        int pagesLength = Math.min(pageRange, totalPages);
        pages = new Integer[pagesLength];

        // firstPage must be greater than 0 and lesser than totalPages-pageLength.
        int firstPage = Math.min(Math.max(0, currentPage - (pageRange / 2)), totalPages - pagesLength);

        // Create pages (page numbers for page links).
        for (int i = 0; i < pagesLength; i++) {
            pages[i] = ++firstPage;
        }
        loadGoToPageNumberSelectItems();
    }
    
    private void loadGoToPageNumberSelectItems() {
    	final List<SelectItem> list = new ArrayList<SelectItem>();
    	for (int i = 0; i < totalPages; i++) {
    		list.add(new SelectItem(Integer.valueOf(i+1), String.valueOf(i+1)));
    	}
    	goToPageNumberSelectItems = list;
    } 

    protected void reloadList() {
    	pageFirst();
    	loadDataList();
    }

    // Getters ------------------------------------------------------------------------------------

	public List getList() {
        // See http://balusc.blogspot.com/2006/06/using-datatables.html
		// Doing this since using session bean.
    	if (FacesContext.getCurrentInstance().getRenderResponse()) {
    		// Reload to get most recent data.
			loadDataList();
        }
        return list;
	}

//	public boolean isEmptyList() {
//		return emptyList;
//	}

	public HtmlDataTable getTable() {
		return table;
	}	

    public int getTotalRows() {
        return totalRows;
    }

    public int getFirstRow() {
        return firstRow;
    }

    public int getRowsPerPage() {
        return rowsPerPage;
    }

    public Integer[] getPages() {
        return pages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }
    
    public List<SelectItem> getGoToPageNumberSelectItems() {
    	return goToPageNumberSelectItems;    	
    }

    public Integer getGoToPageNumber() {
		return goToPageNumber;
	}    

    // Setters ------------------------------------------------------------------------------------	
	public void setGoToPageNumber(Integer goToPageNumber) {
		this.goToPageNumber = goToPageNumber;
	}

	public void setTable(final HtmlDataTable pTable) {
		this.table = pTable;
	}
	
    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }
}

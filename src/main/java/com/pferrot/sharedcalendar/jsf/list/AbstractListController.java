package com.pferrot.sharedcalendar.jsf.list;

import java.io.Serializable;
import java.util.List;

import javax.faces.component.UICommand;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.dao.bean.ListWithRowCount;

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
	
    // Search
	private String searchString = null;
	
	
	
	
	
	
	
	public AbstractListController() {
		super();
		// Default rows per page (max amount of rows to be displayed at once).
		rowsPerPage = 10;
		// Default page range (max amount of page links to be displayed at once).
		pageRange = 10;
	}
	
	public abstract List<Object> getListInternal();
	// TODO: make it abstract.
	protected ListWithRowCount getListWithRowCount() {
		return null;
	}
	
//	public abstract int getTotalListSize();
	public abstract int getNbEntriesPerPage();
	
	
	
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
		this.searchString = pSearchString;
		// If the search string changes, go the the first page.
//		forceReloadList();
//		setFirstResultIndex(0);
	}
	
	public String search() {
		return "search";
	}
	
	public String clearSearch() {
		setSearchString(null);
		return "clearSearch";
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

    public void page(ActionEvent event) {
        page(((Integer) ((UICommand) event.getComponent()).getValue() - 1) * rowsPerPage);
    }

    private void page(int firstRow) {
        this.firstRow = firstRow;
        // Load requested page.
        loadDataList();
    }

    private void loadDataList() {

        // Load list and totalCount.
    	final ListWithRowCount listWithRowCount = getListWithRowCount();
        list = listWithRowCount.getList();
        totalRows = (int)listWithRowCount.getRowCount();

        // Set currentPage, totalPages and pages.
        currentPage = (totalRows / rowsPerPage) - ((totalRows - firstRow) / rowsPerPage) + 1;
        totalPages = (totalRows / rowsPerPage) + ((totalRows % rowsPerPage != 0) ? 1 : 0);
        int pagesLength = Math.min(pageRange, totalPages);
        pages = new Integer[pagesLength];

        // firstPage must be greater than 0 and lesser than totalPages-pageLength.
        int firstPage = Math.min(Math.max(0, currentPage - (pageRange / 2)), totalPages - pagesLength);

        // Create pages (page numbers for page links).
        for (int i = 0; i < pagesLength; i++) {
            pages[i] = ++firstPage;
        }
    }
    

    // Getters ------------------------------------------------------------------------------------

	public List getList() {
        // See http://balusc.blogspot.com/2006/06/using-datatables.html
		// Doing this since using session bean.
    	if (FacesContext.getCurrentInstance().getRenderResponse()) {
//		if (list == null) {
    		// Reload to get most recent data.
			loadDataList();
        }
        return list;
	}

	public HtmlDataTable getTable() {
		return table;
	}	

    public int getTotalRows() {
        return totalRows;
    }

    public int getFirstRow() {
        return firstRow;
    }
    
    /**
     * 
     * @return
     * @deprecated
     */
    public int getFirstResultIndex() {
    	return getFirstRow();
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

    // Setters ------------------------------------------------------------------------------------
	
    public void setTable(final HtmlDataTable pTable) {
		this.table = pTable;
	}
	
    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

	
	
}

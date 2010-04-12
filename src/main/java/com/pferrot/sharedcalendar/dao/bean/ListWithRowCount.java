package com.pferrot.sharedcalendar.dao.bean;

import java.util.List;

/**
 * The beans should store both the result and the total number of items when querying
 * the DAO. The idea is that we should be able to only run the query once, i.e. not have
 * to run the query again just to get the row count. This is not implemented yes (two 
 * queries are still executed) but at least this bean will allow only refactoring the
 * DAO implementation when we want to improve this.
 *
 * @author pferrot
 *
 */
public class ListWithRowCount {

	private long rowCount;
	private int firstRow;
	private int maxRows;
	private List list;
	
	public ListWithRowCount(int firstRow, int maxRows, List list, long rowCount) {
		super();
		this.rowCount = rowCount;
		this.firstRow = firstRow;
		this.maxRows = maxRows;
		this.list = list;
	}

	public long getRowCount() {
		return rowCount;
	}

	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}

	public int getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}

	public int getMaxRows() {
		return maxRows;
	}

	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}
}

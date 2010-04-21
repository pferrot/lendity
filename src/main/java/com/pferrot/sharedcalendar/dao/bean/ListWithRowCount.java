package com.pferrot.sharedcalendar.dao.bean;

import java.util.Collections;
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
	private List list;
	private static ListWithRowCount emptyListWithRowCount;
		
	public ListWithRowCount(List list, long rowCount) {
		super();
		this.rowCount = rowCount;
		this.list = list;
	}

	public long getRowCount() {
		return rowCount;
	}

	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public static ListWithRowCount emptyListWithRowCount() {
		if (emptyListWithRowCount == null) {
			emptyListWithRowCount = new ListWithRowCount(Collections.EMPTY_LIST, 0);
		}
		return emptyListWithRowCount;	
	}
}

package com.pferrot.sharedcalendar.dao;

import java.util.List;

import com.pferrot.sharedcalendar.model.Gender;
import com.pferrot.sharedcalendar.model.ListValue;
import com.pferrot.sharedcalendar.model.OrderedListValue;

public interface ListValueDao {
	
	Long createListValue(ListValue listValue);
	
	ListValue findListValue(Long listValueId);
	
	List<ListValue> findListValue(Class clazz);
	
	/**
	 * Return the list of values in ascending order ("position" field).
	 * 
	 * @param clazz
	 * @return
	 */
	List<OrderedListValue> findOrderedListValue(Class clazz);
	
	Gender findGender(String labelCode);
		
	void updateListValue(ListValue listValue);
	
	void deleteListValue(ListValue listValue);
}

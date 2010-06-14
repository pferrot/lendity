package com.pferrot.lendity.dao;

import java.util.List;

import com.pferrot.lendity.model.Gender;
import com.pferrot.lendity.model.ListValue;
import com.pferrot.lendity.model.OrderedListValue;

public interface ListValueDao {
	
	Long createListValue(ListValue listValue);
	
	ListValue findListValue(Long id);
	
	ListValue findListValue(String labelCode);
	
	List<ListValue> findListValue(Class clazz);
	
	/**
	 * Return the list of values in ascending order ("position" field).
	 * 
	 * @param clazz
	 * @return
	 */
	List<OrderedListValue> findOrderedListValue(Class clazz);
		
	void updateListValue(ListValue listValue);
	
	void deleteListValue(ListValue listValue);
	
	Gender findGender(String labelCode);
}

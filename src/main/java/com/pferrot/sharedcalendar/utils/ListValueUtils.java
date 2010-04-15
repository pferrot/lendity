package com.pferrot.sharedcalendar.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.pferrot.core.CoreUtils;
import com.pferrot.sharedcalendar.dao.ListValueDao;
import com.pferrot.sharedcalendar.model.ListValue;

public class ListValueUtils {
	
	public static List<Long> getIdsFromListValues(final Collection listValues) {
		final List<Long> result = new ArrayList<Long>();
		if (listValues == null) {
			return result;
		}
		final Iterator<ListValue> ite = listValues.iterator();
		while (ite.hasNext()) {
			result.add(ite.next().getId());
		}
		return result;
	}

	public static Set getListValuesFromIds(final Collection<Long> listValuesId, final ListValueDao listValueDao) {
		CoreUtils.assertNotNull(listValueDao);
		if (listValuesId == null) {
			return Collections.EMPTY_SET;
		}
		final Set<ListValue> result = new HashSet<ListValue>();
		for (Long id: listValuesId) {
			result.add(listValueDao.findListValue(id));
		}
		return result;		
	}

	public static ListValue getListValueFromId(final Long listValueId, final ListValueDao listValueDao) {
		CoreUtils.assertNotNull(listValueDao);
		if (listValueId == null) {
			return null;
		}
		return listValueDao.findListValue(listValueId);
	}
}

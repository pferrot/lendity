package com.pferrot.sharedcalendar.dao.hibernate;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.sharedcalendar.dao.ListValueDao;
import com.pferrot.sharedcalendar.model.Gender;
import com.pferrot.sharedcalendar.model.ListValue;
import com.pferrot.sharedcalendar.model.OrderedListValue;

public class ListValueDaoHibernateImpl extends HibernateDaoSupport implements ListValueDao {

	public Long createListValue(final ListValue listValue) {
		return (Long)getHibernateTemplate().save(listValue);
	}

	public void deleteListValue(final ListValue listValue) {
		getHibernateTemplate().delete(listValue);
	}

	public void updateListValue(final ListValue listValue) {
		getHibernateTemplate().update(listValue);
	}

	public ListValue findListValue(final Long id) {
		return (ListValue)getHibernateTemplate().load(ListValue.class, id);
	}

	public List<ListValue> findListValue(final Class clazz) {
		return getHibernateTemplate().loadAll(clazz);
	}
	
	public List<OrderedListValue> findOrderedListValue(final Class clazz) {
		List<OrderedListValue> list = getHibernateTemplate().find("from " + clazz.getName() + " olv order by position asc");
		return list;
	}

	public Gender findGender(final String labelCode) {
		return (Gender)findListValue(labelCode);
	}

	public ListValue findListValue(final String labelCode) {
		List<ListValue> list = getHibernateTemplate().find("from ListValue lv where lv.labelCode = ?", labelCode);
		if (list == null ||
			list.isEmpty()) {
			return null;
		}
		else if (list.size() == 1) {
			return list.get(0);
		}
		else {
			throw new DataIntegrityViolationException("More that one list value with label code '" + labelCode + "'");
		}
	}
}

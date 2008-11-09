package com.pferrot.sharedcalendar.dao.hibernate;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.sharedcalendar.dao.ListValueDao;
import com.pferrot.sharedcalendar.model.Gender;
import com.pferrot.sharedcalendar.model.ListValue;
import com.pferrot.sharedcalendar.model.OrderedListValue;

public class ListValueDaoHibernateImpl extends HibernateDaoSupport implements ListValueDao {

	public Long createListValue(ListValue listValue) {
		return (Long)getHibernateTemplate().save(listValue);
	}

	public void deleteListValue(ListValue listValue) {
		getHibernateTemplate().delete(listValue);
	}

	public void updateListValue(ListValue listValue) {
		getHibernateTemplate().update(listValue);
	}

	public ListValue findListValue(Long listValueId) {
		return (ListValue)getHibernateTemplate().load(ListValue.class, listValueId);
	}

	public List<ListValue> findListValue(Class clazz) {
		return getHibernateTemplate().loadAll(clazz);
	}
	
	public List<OrderedListValue> findOrderedListValue(Class clazz) {
		List<OrderedListValue> list = getHibernateTemplate().find("from " + clazz.getName() + " olv order by position asc");
		return list;
	}

	public Gender findGender(String labelCode) {
		List<Gender> list = getHibernateTemplate().find("from Gender gender where gender.labelCode = ?", labelCode);
		if (list == null ||
			list.isEmpty()) {
			return null;
		}
		else if (list.size() == 1) {
			return list.get(0);
		}
		else {
			throw new DataIntegrityViolationException("More that one gender with label code'" + labelCode + "'");
		}
	}
}

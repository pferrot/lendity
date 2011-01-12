package com.pferrot.lendity.dao.hibernate;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.model.Gender;
import com.pferrot.lendity.model.ListValue;
import com.pferrot.lendity.model.OrderedListValue;

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
		getHibernateTemplate().setCacheQueries(true);
		return (ListValue)getHibernateTemplate().load(ListValue.class, id);
	}

	public List<ListValue> findListValue(final Class clazz) {
		getHibernateTemplate().setCacheQueries(true);
		return getHibernateTemplate().loadAll(clazz);
	}
	
	public List<OrderedListValue> findOrderedListValue(final Class clazz) {
		getHibernateTemplate().setCacheQueries(true);
		List<OrderedListValue> list = getHibernateTemplate().find("from " + clazz.getName() + " olv order by position asc");
		return list;
	}

	public Gender findGender(final String labelCode) {
		return (Gender)findListValue(labelCode);
	}

	public ListValue findListValue(final String labelCode) {
		getHibernateTemplate().setCacheQueries(true);
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

package com.pferrot.sharedcalendar.dao.hibernate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.CoreUtils;
import com.pferrot.sharedcalendar.dao.ItemDao;
import com.pferrot.sharedcalendar.model.Item;
import com.pferrot.sharedcalendar.model.Person;

public class ItemDaoHibernateImpl extends HibernateDaoSupport implements ItemDao {

	public Long createItem(final Item item) {
		return (Long)getHibernateTemplate().save(item);
	}

	public void deleteItem(final Item item) {
		getHibernateTemplate().delete(item);
	}

	public void updateItem(final Item item) {
		getHibernateTemplate().update(item);
	}

	public Item findItem(final Long itemId) {
		return (Item)getHibernateTemplate().load(Item.class, itemId);
	}

	public List<Item> findItemsByTitle(final String pTitle, final int pFirstResult, final int pMaxResults) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Item.class);
		if (pTitle != null && pTitle.trim().length() > 0) {
			criteria.add(Restrictions.ilike("title", pTitle, MatchMode.ANYWHERE));
		}
		criteria.addOrder(Order.asc("title").ignoreCase());
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);
	}
	
	public List<Item> findItemsByTitle(final String pTitle) {
		return findItemsByTitle(pTitle, 0, 0);
	}	
	
	public List<Item> findItems(final int pFirstResult, final int pMaxResults) {
		return findItemsByTitle(null, pFirstResult, pMaxResults);
	}
	
	public List<Item> findAllItems() {
		return findItems(0, 0);
	}

	public List<Item> findItemsByTitleOwnedByPerson(final String pTitle, final Long pPersonId, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Item.class).
			addOrder(Order.asc("title").ignoreCase());
		if (pTitle != null && pTitle.trim().length() > 0) {
			criteria.add(Restrictions.ilike("title", pTitle, MatchMode.ANYWHERE));
		}
		criteria.createCriteria("owner", CriteriaSpecification.INNER_JOIN).
			add(Restrictions.eq("id", pPersonId));
		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);		
	}
	
	public List<Item> findItemsOwnedByPerson(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
		return findItemsByTitleOwnedByPerson(null, pPersonId, pFirstResult, pMaxResults);
	}

	public List<Item> findItemsOwnedByPerson(final Person pPerson, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPerson);
		return findItemsOwnedByPerson(pPerson.getId(), pFirstResult, pMaxResults);
	}

	public List<Item> findItemsByTitleBorrowedByPerson(final String pTitle, final Long pPersonId, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Item.class).
			addOrder(Order.asc("title").ignoreCase());
		if (pTitle != null && pTitle.trim().length() > 0) {
			criteria.add(Restrictions.ilike("title", pTitle, MatchMode.ANYWHERE));
		}
		criteria.createCriteria("borrower", CriteriaSpecification.INNER_JOIN).
			add(Restrictions.eq("id", pPersonId));
		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);
	}

	public List<Item> findItemsBorrowedByPerson(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
		return findItemsByTitleBorrowedByPerson(null, pPersonId, pFirstResult, pMaxResults);
	}

	public List<Item> findItemsBorrowedByPerson(final Person pPerson, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPerson);
		
		return findItemsBorrowedByPerson(pPerson.getId(), pFirstResult, pMaxResults);
	}

	public List<Item> findItemsByTitleLentByPerson(final String pTitle, final Long pPersonId, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Item.class).
		addOrder(Order.asc("title").ignoreCase());
		if (pTitle != null && pTitle.trim().length() > 0) {
			criteria.add(Restrictions.ilike("title", pTitle, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.isNotNull("borrower")).
			createCriteria("owner", CriteriaSpecification.INNER_JOIN).
			add(Restrictions.eq("id", pPersonId));
	
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);
	}

	public List<Item> findItemsLentByPerson(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
		return findItemsByTitleLentByPerson(null, pPersonId, pFirstResult, pMaxResults);		
	}

	public List<Item> findItemsLentByPerson(final Person pPerson, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPerson);
		return findItemsLentByPerson(pPerson.getId(), pFirstResult, pMaxResults);
	}

	private List<Item> findItemsByTitleOwnedByPersons(final String pTitle, final Long[] pPersonIds, final boolean pVisibleItemsOnly, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonIds);
		if (pPersonIds.length == 0) {
			return Collections.EMPTY_LIST;
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(Item.class).
			addOrder(Order.asc("title").ignoreCase());
		
		if (pTitle != null && pTitle.trim().length() > 0) {
			criteria.add(Restrictions.ilike("title", pTitle, MatchMode.ANYWHERE));
		}
		if (pVisibleItemsOnly) {
			criteria.add(Restrictions.eq("visible", Boolean.TRUE));
		}
		criteria.createCriteria("owner", CriteriaSpecification.INNER_JOIN).
			add(Restrictions.in("id", pPersonIds));
		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);
	}

	public List<Item> findVisibleItemsByTitleOwnedByPersons(final String pTitle, final Long[] pPersonIds, final int pFirstResult, final int pMaxResults) {
		return findItemsByTitleOwnedByPersons(pTitle, pPersonIds, true, pFirstResult, pMaxResults);
	}

	public List<Item> findVisibleItemsOwnedByPersons(final Long[] pPersonIds, final int pFirstResult, final int pMaxResults) {
		return findVisibleItemsByTitleOwnedByPersons(null, pPersonIds, pFirstResult, pMaxResults);
	}

	public List<Item> findVisibleItemsByTitleOwnedByConnections(final String pTitle, final Person pPerson, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPerson);
		
		final List<Long> connetionsIds = new ArrayList<Long>();
		
		final Set<Person> connections = pPerson.getConnections();
		if (connections != null) { 
			for (Person connection: connections) {
				connetionsIds.add(connection.getId());
			}
		}
		final Long[] connetionsIdsArray = (Long[])connetionsIds.toArray(new Long[connetionsIds.size()]);
		
		return findVisibleItemsByTitleOwnedByPersons(pTitle, connetionsIdsArray, pFirstResult, pMaxResults);
	}

	public List<Item> findVisibleItemsOwnedByConnections(final Person pPerson, final int pFirstResult, final int pMaxResults) {
		return findVisibleItemsByTitleOwnedByConnections(null, pPerson, pFirstResult, pMaxResults);
	}	
}

package com.pferrot.sharedcalendar.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.CoreUtils;
import com.pferrot.security.model.User;
import com.pferrot.sharedcalendar.dao.ItemDao;
import com.pferrot.sharedcalendar.model.Item;

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
		CoreUtils.assertNotNullOrEmptyString(pTitle);
		final String titleLower = pTitle.trim().toLowerCase();
		DetachedCriteria critera = DetachedCriteria.forClass(Item.class).
			add(Restrictions.ilike("title", pTitle, MatchMode.ANYWHERE)).
			addOrder(Order.asc("title").ignoreCase());
		return getHibernateTemplate().findByCriteria(critera, pFirstResult, pMaxResults);
	}
	
	public List<Item> findItemsByTitle(final String pTitle) {
		return findItemsByTitle(pTitle, 0, 0);
	}	
	
	public List<Item> findItems(final int pFirstResult, final int pMaxResults) {
		DetachedCriteria critera = DetachedCriteria.forClass(Item.class).
			addOrder(Order.asc("title").ignoreCase());
		return getHibernateTemplate().findByCriteria(critera, pFirstResult, pMaxResults);
	}
	
	public List<Item> findAllItems() {
		return findItems(0, 0);
	}
	
	public List<Item> findItemsOwnedByUser(final String pUsername, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNullOrEmptyString(pUsername);
		
		DetachedCriteria critera = DetachedCriteria.forClass(Item.class).
			addOrder(Order.asc("title").ignoreCase()).
			createCriteria("owner", CriteriaSpecification.INNER_JOIN).
			add(Restrictions.eq("username", pUsername));
		
		return getHibernateTemplate().findByCriteria(critera, pFirstResult, pMaxResults);
	}

	public List<Item> findItemsOwnedByUser(final User pUser, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pUser);
		return findItemsOwnedByUser(pUser.getUsername(), pFirstResult, pMaxResults);
	}

	public List<Item> findItemsBorrowedByUser(final String pUsername, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNullOrEmptyString(pUsername);
		
		DetachedCriteria critera = DetachedCriteria.forClass(Item.class).
			addOrder(Order.asc("title").ignoreCase()).
			createCriteria("owner", CriteriaSpecification.INNER_JOIN).
			add(Restrictions.eq("username", pUsername));
		
		return getHibernateTemplate().findByCriteria(critera, pFirstResult, pMaxResults);
	}

	public List<Item> findItemsBorrowedByUser(final User pUser, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pUser);
		
		return findItemsBorrowedByUser(pUser.getUsername(), pFirstResult, pMaxResults);
	}
}

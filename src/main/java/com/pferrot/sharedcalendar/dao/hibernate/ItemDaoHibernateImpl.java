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
import com.pferrot.security.model.User;
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

	public List<Item> findItemsByTitleOwnedByUser(final String pTitle, final String pUsername, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNullOrEmptyString(pUsername);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Item.class).
			addOrder(Order.asc("title").ignoreCase());
		if (pTitle != null && pTitle.trim().length() > 0) {
			criteria.add(Restrictions.ilike("title", pTitle, MatchMode.ANYWHERE));
		}
		criteria.createCriteria("owner", CriteriaSpecification.INNER_JOIN).
			add(Restrictions.eq("username", pUsername));
		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);		
	}
	
	public List<Item> findItemsOwnedByUser(final String pUsername, final int pFirstResult, final int pMaxResults) {
		return findItemsByTitleOwnedByUser(null, pUsername, pFirstResult, pMaxResults);
	}

	public List<Item> findItemsOwnedByUser(final User pUser, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pUser);
		return findItemsOwnedByUser(pUser.getUsername(), pFirstResult, pMaxResults);
	}

	public List<Item> findItemsByTitleBorrowedByUser(final String pTitle, final String pUsername, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNullOrEmptyString(pUsername);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Item.class).
			addOrder(Order.asc("title").ignoreCase());
		if (pTitle != null && pTitle.trim().length() > 0) {
			criteria.add(Restrictions.ilike("title", pTitle, MatchMode.ANYWHERE));
		}
		criteria.createCriteria("borrower", CriteriaSpecification.INNER_JOIN).
			add(Restrictions.eq("username", pUsername));
		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);
	}

	public List<Item> findItemsBorrowedByUser(final String pUsername, final int pFirstResult, final int pMaxResults) {
		return findItemsByTitleBorrowedByUser(null, pUsername, pFirstResult, pMaxResults);
	}

	public List<Item> findItemsBorrowedByUser(final User pUser, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pUser);
		
		return findItemsBorrowedByUser(pUser.getUsername(), pFirstResult, pMaxResults);
	}

	public List<Item> findItemsByTitleLentByUser(final String pTitle, final String pUsername, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNullOrEmptyString(pUsername);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Item.class).
		addOrder(Order.asc("title").ignoreCase());
		if (pTitle != null && pTitle.trim().length() > 0) {
			criteria.add(Restrictions.ilike("title", pTitle, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.isNotNull("borrower")).
			createCriteria("owner", CriteriaSpecification.INNER_JOIN).
			add(Restrictions.eq("username", pUsername));
	
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);
	}

	public List<Item> findItemsLentByUser(final String pUsername, final int pFirstResult, final int pMaxResults) {
		return findItemsByTitleLentByUser(null, pUsername, pFirstResult, pMaxResults);		
	}

	public List<Item> findItemsLentByUser(final User pUser, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pUser);
		return findItemsLentByUser(pUser.getUsername(), pFirstResult, pMaxResults);
	}

	public List<Item> findItemsByTitleOwnedByUsers(final String pTitle, final String[] pUsernames , final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pUsernames);
		if (pUsernames.length == 0) {
			return Collections.EMPTY_LIST;
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(Item.class).
			addOrder(Order.asc("title").ignoreCase());
		
		if (pTitle != null && pTitle.trim().length() > 0) {
			criteria.add(Restrictions.ilike("title", pTitle, MatchMode.ANYWHERE));
		}
		criteria.createCriteria("owner", CriteriaSpecification.INNER_JOIN).
			add(Restrictions.in("username", pUsernames));
		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);
	}

	public List<Item> findItemsOwnedByUsers(final String[] pUsernames , final int pFirstResult, final int pMaxResults) {
		return findItemsByTitleOwnedByUsers(null, pUsernames, pFirstResult, pMaxResults);
	}

	public List<Item> findItemsByTitleOwnedByConnections(final String pTitle, final Person pPerson, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPerson);
		
		final List<String> connetionsUsernames = new ArrayList<String>();
		
		final Set<Person> connections = pPerson.getConnections();
		if (connections != null) { 
			for (Person connection: connections) {
				final User connectionUser = connection.getUser();
				if (connectionUser != null) {
					final String connectionUsername = connectionUser.getUsername();
					if (connectionUsername != null) {
						connetionsUsernames.add(connectionUsername);
					}					
				}
			}
		}
		final String[] connectionsUsernamesArray = (String[])connetionsUsernames.toArray(new String[connetionsUsernames.size()]);
		
		return findItemsByTitleOwnedByUsers(pTitle, connectionsUsernamesArray, pFirstResult, pMaxResults);
	}

	public List<Item> findItemsOwnedByConnections(final Person pPerson, final int pFirstResult, final int pMaxResults) {
		return findItemsByTitleOwnedByConnections(null, pPerson, pFirstResult, pMaxResults);
	}	
}

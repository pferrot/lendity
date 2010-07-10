package com.pferrot.lendity.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.dao.ItemDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.ExternalItem;
import com.pferrot.lendity.model.InternalItem;
import com.pferrot.lendity.model.Item;

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

	public InternalItem findInternalItem(final Long itemId) {
		return (InternalItem)getHibernateTemplate().load(InternalItem.class, itemId);
	}

	public ExternalItem findExternalItem(final Long itemId) {
		return (ExternalItem)getHibernateTemplate().load(ExternalItem.class, itemId);
	}
	
//	public List<InternalItem> findAllInternalItems() {
//		return findInternalItems(0, 0);
//	}
//	
//	public List<InternalItem> findInternalItems(final int pFirstResult, final int pMaxResults) {
//		return findInternalItemsByTitle(null, pFirstResult, pMaxResults);
//	}
//
//	public List<InternalItem> findInternalItemsByTitle(final String pTitle) {
//		return (List<InternalItem>) findItemsByTitle(pTitle, 0, 0, InternalItem.class);
//	}
//
//	public List<InternalItem> findInternalItemsByTitle(final String pTitle, final int pFirstResult, final int pMaxResults) {
//		return (List<InternalItem>) findItemsByTitle(pTitle, pFirstResult, pMaxResults, InternalItem.class);
//	}
//
//	private List findItemsByTitle(final String pTitle, final int pFirstResult, final int pMaxResults, final Class clazz) {
//		final DetachedCriteria criteria = getItemsByTitleCriteria(pTitle, clazz);
//		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);
//	}
//	
//	private long countItemsByTitle(final String pTitle, final Class clazz) {
//		final DetachedCriteria criteria = getItemsByTitleCriteria(pTitle, clazz);
//		return rowCount(criteria);
//	}
//	
//	private DetachedCriteria getItemsByTitleCriteria(final String pTitle, final Class clazz) {
//		final DetachedCriteria criteria = DetachedCriteria.forClass(clazz);
//		if (pTitle != null && pTitle.trim().length() > 0) {
//			criteria.add(Restrictions.ilike("title", pTitle, MatchMode.ANYWHERE));
//		}
//		criteria.addOrder(Order.asc("title").ignoreCase());
//		return criteria;
//	}
//
//	public List<Item> findItemsByTitleBorrowedByPerson(final String pTitle, final Long pPersonId, final int pFirstResult, final int pMaxResults) {
//		CoreUtils.assertNotNull(pPersonId);
//		
//		DetachedCriteria criteria = DetachedCriteria.forClass(Item.class).
//			addOrder(Order.asc("title").ignoreCase());
//		if (pTitle != null && pTitle.trim().length() > 0) {
//			criteria.add(Restrictions.ilike("title", pTitle, MatchMode.ANYWHERE));
//		}
//		criteria.createCriteria("borrower", CriteriaSpecification.INNER_JOIN).
//			add(Restrictions.eq("id", pPersonId));
//		
//		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);
//	}
//
//	public List<Item> findItemsBorrowedByPerson(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
//		return findItemsByTitleBorrowedByPerson(null, pPersonId, pFirstResult, pMaxResults);
//	}
//
//	public List<Item> findItemsBorrowedByPerson(final Person pPerson, final int pFirstResult, final int pMaxResults) {
//		CoreUtils.assertNotNull(pPerson);
//		
//		return findItemsBorrowedByPerson(pPerson.getId(), pFirstResult, pMaxResults);
//	}
//
//	public List<InternalItem> findItemsByTitleLentByPerson(final String pTitle, final Long pPersonId, final int pFirstResult, final int pMaxResults) {
//		CoreUtils.assertNotNull(pPersonId);
//		
//		DetachedCriteria criteria = DetachedCriteria.forClass(InternalItem.class).
//		addOrder(Order.asc("title").ignoreCase());
//		if (pTitle != null && pTitle.trim().length() > 0) {
//			criteria.add(Restrictions.ilike("title", pTitle, MatchMode.ANYWHERE));
//		}
//		criteria.add(Restrictions.isNotNull("borrowDate")).
//			createCriteria("owner", CriteriaSpecification.INNER_JOIN).
//			add(Restrictions.eq("id", pPersonId));
//	
//		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);
//	}
//
//	public List<InternalItem> findItemsLentByPerson(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
//		return findItemsByTitleLentByPerson(null, pPersonId, pFirstResult, pMaxResults);		
//	}
//
//	public List<InternalItem> findItemsLentByPerson(final Person pPerson, final int pFirstResult, final int pMaxResults) {
//		CoreUtils.assertNotNull(pPerson);
//		return findItemsLentByPerson(pPerson.getId(), pFirstResult, pMaxResults);
//	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Items owned by person
//	public ListWithRowCount findItemsByTitleOwnedByPerson(final String pTitle, final Long pPersonId, final int pFirstResult, final int pMaxResults) {
//		
//		final List list = listItemsByTitleOwnedByPerson(pTitle, pPersonId, pFirstResult, pMaxResults);
//		final long rowCount = countItemsByTitleOwnedByPerson(pTitle, pPersonId);
//		
//		final ListWithRowCount result = new ListWithRowCount(list, rowCount);
//		
//		return result;		
//	}
//	
//	public ListWithRowCount findItemsByTitleOwnedByPerson(final String pTitle, final Person pPerson, final int pFirstResult, final int pMaxResults) {
//		CoreUtils.assertNotNull(pPerson);
//		return findItemsByTitleOwnedByPerson(pTitle, pPerson.getId(), pFirstResult, pMaxResults);
//	}
//	
//	public ListWithRowCount findItemsOwnedByPerson(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
//		return findItemsByTitleOwnedByPerson(null, pPersonId, pFirstResult, pMaxResults);
//	}
//
//	public ListWithRowCount findItemsOwnedByPerson(final Person pPerson, final int pFirstResult, final int pMaxResults) {
//		CoreUtils.assertNotNull(pPerson);
//		return findItemsOwnedByPerson(pPerson.getId(), pFirstResult, pMaxResults);
//	}
//	
//	private List<InternalItem> listItemsByTitleOwnedByPerson(final String pTitle, final Long pPersonId, final int pFirstResult, final int pMaxResults) {		
//		final DetachedCriteria criteria = getItemsByTitleOwnedByPersonCriteria(pTitle, pPersonId);
//		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);		
//	}
//	
//	private DetachedCriteria getItemsByTitleOwnedByPersonCriteria(final String pTitle, final Long pPersonId) {
//		CoreUtils.assertNotNull(pPersonId);
//		final DetachedCriteria criteria = DetachedCriteria.forClass(InternalItem.class).
//			addOrder(Order.asc("title").ignoreCase());
//		if (pTitle != null && pTitle.trim().length() > 0) {
//			criteria.add(Restrictions.ilike("title", pTitle, MatchMode.ANYWHERE));
//		}
//		criteria.createCriteria("owner", CriteriaSpecification.INNER_JOIN).
//			add(Restrictions.eq("id", pPersonId));
//		return criteria;
//	}
//
//
//
	private List<InternalItem> findItemsList(final Long[] pOwnerIds, final Boolean pOwnerEnabled, final Long[] pBorrwoerIds, final Boolean pBorrowerEnabled,
			final String pTitle, final Long[] pCategoryIds, final Boolean pVisible, final Boolean pBorrowed, final String pOrderBy, final Boolean pOrderByAscending, final int pFirstResult,
			final int pMaxResults, final Class pClass) {
		
		final DetachedCriteria criteria = getItemsDetachedCriteria(pOwnerIds, pOwnerEnabled, pBorrwoerIds, pBorrowerEnabled, pTitle, pCategoryIds, pVisible, pBorrowed, pClass);
		
		if (!StringUtils.isNullOrEmpty(pOrderBy)) {
			// Ascending.
			if (pOrderByAscending == null || pOrderByAscending.booleanValue()) {
				criteria.addOrder(Order.asc(pOrderBy).ignoreCase());
			}
			// Descending.
			else {
				criteria.addOrder(Order.desc(pOrderBy).ignoreCase());
			}
		}
		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);
	}

	private long countItems(final Long[] pOwnerIds, final Boolean pOwnerEnabled, final Long[] pBorrwoerIds, final Boolean pBorrowerEnabled, final String pTitle, final Long[] pCategoryIds,
			final Boolean pVisible, final Boolean pBorrowed, final Class pClass) {
		final DetachedCriteria criteria = getItemsDetachedCriteria(pOwnerIds, pOwnerEnabled, pBorrwoerIds, pBorrowerEnabled, pTitle, pCategoryIds, pVisible, pBorrowed, pClass);
		return rowCount(criteria);
	}
	
	public long countInternalItems(final Long[] pOwnerIds, final Boolean pOwnerEnabled, final Long[] pBorrwoerIds, final Boolean pBorrowerEnabled, final String pTitle, final Long[] pCategoryIds,
			final Boolean pVisible, final Boolean pBorrowed) {
		return countItems(pOwnerIds, pOwnerEnabled, pBorrwoerIds, pBorrowerEnabled, pTitle, pCategoryIds, pVisible, pBorrowed, InternalItem.class);
	}
	
	private DetachedCriteria getItemsDetachedCriteria(final Long[] pOwnerIds, final Boolean pOwnerEnabled, final Long[] pBorrwoerIds,
			final Boolean pBorrowerEnabled, final String pTitle, final Long[] pCategoryIds,
			final Boolean pVisible, final Boolean pBorrowed, final Class pClass) {
		
		Class theClass = pClass;
		// Only internal items have an owner.
		if (pOwnerIds != null && pOwnerIds.length > 0) {
			theClass = InternalItem.class;
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(theClass);
	
		if (pTitle != null && pTitle.trim().length() > 0) {
			criteria.add(Restrictions.ilike("title", pTitle, MatchMode.ANYWHERE));
		}
		if (pVisible != null) {
			criteria.add(Restrictions.eq("visible", pVisible));
		}
		if (pBorrowed != null) {
			if (pBorrowed.booleanValue()) {
				criteria.add(Restrictions.isNotNull("borrowDate"));
			}
			else {
				criteria.add(Restrictions.isNull("borrowDate"));
			}
		}
		if (pCategoryIds != null && pCategoryIds.length > 0) {
			criteria.createCriteria("category", CriteriaSpecification.INNER_JOIN).
				add(Restrictions.in("id", pCategoryIds));
		}
		if (pOwnerIds != null && pOwnerIds.length > 0) {
			final DetachedCriteria ownerCriteria = criteria.createCriteria("owner", CriteriaSpecification.INNER_JOIN);
			ownerCriteria.add(Restrictions.in("id", pOwnerIds));
			if (pOwnerEnabled != null) {
				ownerCriteria.add(Restrictions.eq("enabled", pOwnerEnabled));
			}
		}
		if (pBorrwoerIds != null && pBorrwoerIds.length > 0) {
			final DetachedCriteria borrowerCriteria = criteria.createCriteria("borrower", CriteriaSpecification.INNER_JOIN);
			borrowerCriteria.add(Restrictions.in("id", pBorrwoerIds));
			if (pBorrowerEnabled != null) {
				borrowerCriteria.add(Restrictions.eq("enabled", pBorrowerEnabled));
			}
		}
		
		return criteria;	
	}

//	public List<InternalItem> findVisibleItemsByTitleOwnedByPersons(final String pTitle, final Long[] pPersonIds, final int pFirstResult, final int pMaxResults) {
//		return findItemsByTitleOwnedByPersons(pPersonIds, pTitle, null, true, pFirstResult, pMaxResults);
//	}
//
//	public List<InternalItem> findVisibleItemsOwnedByPersons(final Long[] pPersonIds, final int pFirstResult, final int pMaxResults) {
//		return findVisibleItemsByTitleOwnedByPersons(null, pPersonIds, pFirstResult, pMaxResults);
//	}
//
//
//	private long countItemsByTitleOwnedByPerson(final String pTitle, final Person pPerson) {
//		CoreUtils.assertNotNull(pPerson);
//		return countItemsByTitleOwnedByPerson(pTitle, pPerson.getId());
//	}
//
//	private long countItemsByTitleOwnedByPerson(final String pTitle, final Long pPersonId) {
//		return rowCount(getItemsByTitleOwnedByPersonCriteria(pTitle, pPersonId));
//	}
//
//	private long countItemsOwnedByPerson(final Person pPerson) {
//		CoreUtils.assertNotNull(pPerson);
//		return countItemsOwnedByPerson(pPerson.getId());
//	}
//
//	private long countItemsOwnedByPerson(final Long pPersonId) {
//		return countItemsByTitleOwnedByPerson(null, pPersonId);
//	}
	
	// Items owned by person - end
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

//	public List<InternalItem> findVisibleItemsByTitleOwnedByConnections(final String pTitle, final Person pPerson, final int pFirstResult, final int pMaxResults) {
//		CoreUtils.assertNotNull(pPerson);
//		
//		final List<Long> connetionsIds = new ArrayList<Long>();
//		
//		final Set<Person> connections = pPerson.getConnections();
//		if (connections != null) { 
//			for (Person connection: connections) {
//				connetionsIds.add(connection.getId());
//			}
//		}
//		final Long[] connetionsIdsArray = (Long[])connetionsIds.toArray(new Long[connetionsIds.size()]);
//		
//		return findVisibleItemsByTitleOwnedByPersons(pTitle, connetionsIdsArray, pFirstResult, pMaxResults);
//	}
//
//	public List<InternalItem> findVisibleItemsOwnedByConnections(final Person pPerson, final int pFirstResult, final int pMaxResults) {
//		return findVisibleItemsByTitleOwnedByConnections(null, pPerson, pFirstResult, pMaxResults);
//	}

	/**
	 * Returns the number of rows for a giver DetachedCriteria.
	 *
	 * @param pCriteria
	 * @return
	 */
	private long rowCount(final DetachedCriteria pCriteria) {
		pCriteria.setProjection(Projections.rowCount());
		return ((Long)getHibernateTemplate().findByCriteria(pCriteria).get(0)).longValue();
	}

	public ListWithRowCount findInternalItems(final Long[] pOwnerIds, final Boolean pOwnerEnabled, final Long[] pBorrowerIds, final Boolean pBorrowerEnabled, final String pTitle,
			final Long[] categoriesId, final Boolean pVisible, final Boolean pBorrowed, final String pOrderBy, final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
		final List list = findItemsList(pOwnerIds, pOwnerEnabled, pBorrowerIds, pBorrowerEnabled, pTitle,
				categoriesId, pVisible, pBorrowed, pOrderBy, pOrderByAscending, pFirstResult, pMaxResults, InternalItem.class);
		final long count = countItems(pOwnerIds, pOwnerEnabled, pBorrowerIds, pBorrowerEnabled, pTitle, categoriesId, pVisible, pBorrowed, InternalItem.class);
		
		return new ListWithRowCount(list, count);
	}
	
	public ListWithRowCount findInternalAndExternalItems(final Long[] pOwnerIds, final Boolean pOwnerEnabled, final Long[] pBorrowerIds, final Boolean pBorrowerEnabled, final String pTitle,
			final Long[] categoriesId, final Boolean pBorrowed, final String pOrderBy, final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
		final List list = findItemsList(pOwnerIds, pOwnerEnabled, pBorrowerIds, pBorrowerEnabled, pTitle, categoriesId, null,
				pBorrowed, pOrderBy, pOrderByAscending, pFirstResult, pMaxResults, Item.class);
		final long count = countItems(pOwnerIds, pOwnerEnabled, pBorrowerIds, pBorrowerEnabled, pTitle, categoriesId, null, pBorrowed, Item.class);
		
		return new ListWithRowCount(list, count);
	}


	
}

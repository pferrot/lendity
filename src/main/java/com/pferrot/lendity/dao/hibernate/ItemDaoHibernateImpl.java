package com.pferrot.lendity.dao.hibernate;

import java.util.Date;
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


	public List<InternalItem> findInternalItemsList(final Long[] pOwnerIds, final Boolean pOwnerEnabled, final Long[] pBorrwoerIds, final Boolean pBorrowerEnabled,
			final String pTitle, final Long[] pCategoryIds, final Boolean pVisible, final Boolean pBorrowed, final Date pCreationDateMin, final String pOrderBy, final Boolean pOrderByAscending,
			final int pFirstResult, final int pMaxResults) {
		return (List<InternalItem>)findItemsList(pOwnerIds, pOwnerEnabled, pBorrwoerIds, pBorrowerEnabled, pTitle, pCategoryIds, pVisible, pBorrowed, pCreationDateMin, pOrderBy, pOrderByAscending, InternalItem.class, pFirstResult, pMaxResults);
		
	}
	
	private List findItemsList(final Long[] pOwnerIds, final Boolean pOwnerEnabled, final Long[] pBorrwoerIds, final Boolean pBorrowerEnabled,
			final String pTitle, final Long[] pCategoryIds, final Boolean pVisible, final Boolean pBorrowed, final Date pCreationDateMin, final String pOrderBy, final Boolean pOrderByAscending,
			final Class pClass, final int pFirstResult, final int pMaxResults) {
		
		final DetachedCriteria criteria = getItemsDetachedCriteria(pOwnerIds, pOwnerEnabled, pBorrwoerIds, pBorrowerEnabled, pTitle, pCategoryIds, pVisible, pBorrowed, pCreationDateMin, pClass);
		
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
			final Boolean pVisible, final Boolean pBorrowed, final Date pCreationDateMin, final Class pClass) {
		final DetachedCriteria criteria = getItemsDetachedCriteria(pOwnerIds, pOwnerEnabled, pBorrwoerIds, pBorrowerEnabled, pTitle, pCategoryIds, pVisible, pBorrowed, pCreationDateMin, pClass);
		return rowCount(criteria);
	}
	
	public long countInternalItems(final Long[] pOwnerIds, final Boolean pOwnerEnabled, final Long[] pBorrwoerIds, final Boolean pBorrowerEnabled, final String pTitle, final Long[] pCategoryIds,
			final Boolean pVisible, final Boolean pBorrowed, final Date pCreationDateMin) {
		return countItems(pOwnerIds, pOwnerEnabled, pBorrwoerIds, pBorrowerEnabled, pTitle, pCategoryIds, pVisible, pBorrowed, pCreationDateMin, InternalItem.class);
	}
	
	private DetachedCriteria getItemsDetachedCriteria(final Long[] pOwnerIds, final Boolean pOwnerEnabled, final Long[] pBorrwoerIds,
			final Boolean pBorrowerEnabled, final String pTitle, final Long[] pCategoryIds,
			final Boolean pVisible, final Boolean pBorrowed, final Date pCreationDateMin, final Class pClass) {
		
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
		
		if (pCreationDateMin != null) {
			criteria.add(Restrictions.gt("creationDate", pCreationDateMin));
		}
		
		return criteria;	
	}

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
			final Long[] categoriesId, final Boolean pVisible, final Boolean pBorrowed, final Date pCreationDateMin, final String pOrderBy, final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
		final List list = findItemsList(pOwnerIds, pOwnerEnabled, pBorrowerIds, pBorrowerEnabled, pTitle,
				categoriesId, pVisible, pBorrowed, pCreationDateMin, pOrderBy, pOrderByAscending, InternalItem.class, pFirstResult, pMaxResults);
		final long count = countItems(pOwnerIds, pOwnerEnabled, pBorrowerIds, pBorrowerEnabled, pTitle, categoriesId, pVisible, pBorrowed, pCreationDateMin, InternalItem.class);
		
		return new ListWithRowCount(list, count);
	}
	
	public ListWithRowCount findInternalAndExternalItems(final Long[] pOwnerIds, final Boolean pOwnerEnabled, final Long[] pBorrowerIds, final Boolean pBorrowerEnabled, final String pTitle,
			final Long[] categoriesId, final Boolean pBorrowed, final Date pCreationDateMin, final String pOrderBy, final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
		final List list = findItemsList(pOwnerIds, pOwnerEnabled, pBorrowerIds, pBorrowerEnabled, pTitle, categoriesId, null,
				pBorrowed, pCreationDateMin, pOrderBy, pOrderByAscending, Item.class, pFirstResult, pMaxResults);
		final long count = countItems(pOwnerIds, pOwnerEnabled, pBorrowerIds, pBorrowerEnabled, pTitle, categoriesId, null, pBorrowed, pCreationDateMin, Item.class);
		
		return new ListWithRowCount(list, count);
	}


	
}

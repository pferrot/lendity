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
import com.pferrot.lendity.dao.NeedDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.Need;

public class NeedDaoHibernateImpl extends HibernateDaoSupport implements NeedDao {

	public Long createNeed(final Need pNeed) {
		return (Long)getHibernateTemplate().save(pNeed);
	}

	public void deleteNeed(final Need pNeed) {
		getHibernateTemplate().delete(pNeed);
	}

	public void updateNeed(final Need pNeed) {
		getHibernateTemplate().update(pNeed);
	}

	public Need findNeed(final Long pNeedId) {
		return (Need)getHibernateTemplate().load(Need.class, pNeedId);
	}

	private List<Need> findNeedsList(final Long[] pOwnerIds, final Boolean pOwnerEnabled, final String pTitle, final Long[] pCategoryIds,
			final String pOrderBy, final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
		
		final DetachedCriteria criteria = getNeedsDetachedCriteria(pOwnerIds, pOwnerEnabled, pTitle, pCategoryIds);
		
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

	public long countNeeds(final Long[] pOwnerIds, final Boolean pOwnerEnabled, final String pTitle, final Long[] pCategoryIds) {
		final DetachedCriteria criteria = getNeedsDetachedCriteria(pOwnerIds, pOwnerEnabled, pTitle, pCategoryIds);
		return rowCount(criteria);
	}
	
	private DetachedCriteria getNeedsDetachedCriteria(final Long[] pOwnerIds, final Boolean pOwnerEnabled, final String pTitle,
			final Long[] pCategoryIds) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Need.class);
	
		if (pTitle != null && pTitle.trim().length() > 0) {
			criteria.add(Restrictions.ilike("title", pTitle, MatchMode.ANYWHERE));
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

	public ListWithRowCount findNeeds(final Long[] pOwnerIds, final Boolean pOwnerEnabled, final String pTitle,
			final Long[] categoriesId, final String pOrderBy, final Boolean pOrderByAscending, final int pFirstResult, final int pMaxResults) {
		final List list = findNeedsList(pOwnerIds, pOwnerEnabled, pTitle,
				categoriesId, pOrderBy, pOrderByAscending, pFirstResult, pMaxResults);
		final long count = countNeeds(pOwnerIds, pOwnerEnabled, pTitle, categoriesId);
		
		return new ListWithRowCount(list, count);
	}	
}

package com.pferrot.lendity.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.dao.NeedDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.dao.bean.NeedDaoQueryBean;
import com.pferrot.lendity.model.Need;

public class NeedDaoHibernateImpl extends ObjektDaoHibernateImpl implements NeedDao {

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

	public List<Need> findNeedsList(final NeedDaoQueryBean pNeedDaoQueryBean) {
		
		final DetachedCriteria criteria = getNeedsDetachedCriteria(pNeedDaoQueryBean);
		
		if (!StringUtils.isNullOrEmpty(pNeedDaoQueryBean.getOrderBy())) {
			if ("random".equals(pNeedDaoQueryBean.getOrderBy())) {
				// This is MySql specific !!!
				criteria.add(Restrictions.sqlRestriction("1=1 order by rand()"));
			}
			else {
				// Ascending.
				if (pNeedDaoQueryBean.getOrderByAscending() == null || pNeedDaoQueryBean.getOrderByAscending().booleanValue()) {
					criteria.addOrder(Order.asc(pNeedDaoQueryBean.getOrderBy()).ignoreCase());
				}
				// Descending.
				else {
					criteria.addOrder(Order.desc(pNeedDaoQueryBean.getOrderBy()).ignoreCase());
				}
			}
		}
		
		return getHibernateTemplate().findByCriteria(criteria, pNeedDaoQueryBean.getFirstResult(), pNeedDaoQueryBean.getMaxResults());
	}

	public long countNeeds(final NeedDaoQueryBean pNeedDaoQueryBean) {
		final DetachedCriteria criteria = getNeedsDetachedCriteria(pNeedDaoQueryBean);
		return rowCount(criteria);
	}
	
	protected DetachedCriteria getNeedsDetachedCriteria(final NeedDaoQueryBean pNeedDaoQueryBean) {
		return getObjectsDetachedCriteria(pNeedDaoQueryBean);
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

	public ListWithRowCount findNeeds(final NeedDaoQueryBean pNeedDaoQueryBean) {
		final List list = findNeedsList(pNeedDaoQueryBean);
		final long count = countNeeds(pNeedDaoQueryBean);
		
		return new ListWithRowCount(list, count);
	}

	@Override
	protected Class getObjectClass() {
		return Need.class;
	}	
}

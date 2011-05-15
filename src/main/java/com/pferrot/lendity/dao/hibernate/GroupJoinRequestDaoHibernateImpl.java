package com.pferrot.lendity.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.dao.GroupJoinRequestDao;
import com.pferrot.lendity.dao.bean.GroupJoinRequestDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.GroupJoinRequest;

public class GroupJoinRequestDaoHibernateImpl extends HibernateDaoSupport implements GroupJoinRequestDao {

	public Long createGroupJoinRequest(final GroupJoinRequest pGroupJoinRequest) {
		return (Long)getHibernateTemplate().save(pGroupJoinRequest);
	}

	public void deleteGroupJoinRequest(final GroupJoinRequest pGroupJoinRequest) {
		getHibernateTemplate().delete(pGroupJoinRequest);		
	}

	public void updateGroupJoinRequest(final GroupJoinRequest pGroupJoinRequest) {
		getHibernateTemplate().update(pGroupJoinRequest);
	}

	public GroupJoinRequest findGroupJoinRequest(final Long pGroupJoinRequestId) {
		return (GroupJoinRequest)getHibernateTemplate().load(GroupJoinRequest.class, pGroupJoinRequestId);
	}
	
	public List<GroupJoinRequest> findGroupJoinRequestsList(final GroupJoinRequestDaoQueryBean pQueryBean) {
		final DetachedCriteria criteria = getGroupJoinRequestDetachedCriteria(pQueryBean);
		if (!StringUtils.isNullOrEmpty(pQueryBean.getOrderBy())) {
			if (Boolean.FALSE.equals(pQueryBean.getOrderByAscending())) {
				criteria.addOrder(Order.desc(pQueryBean.getOrderBy()));
			}
			else {
				criteria.addOrder(Order.asc(pQueryBean.getOrderBy()));
			}
		}	
		
		return getHibernateTemplate().findByCriteria(criteria, pQueryBean.getFirstResult(), pQueryBean.getMaxResults());		
	}

	public long countGroupJoinRequests(final GroupJoinRequestDaoQueryBean pQueryBean) {
		final DetachedCriteria criteria = getGroupJoinRequestDetachedCriteria(pQueryBean);
		return rowCount(criteria);
	}

	private DetachedCriteria getGroupJoinRequestDetachedCriteria(final GroupJoinRequestDaoQueryBean pQueryBean) {
		DetachedCriteria criteria = DetachedCriteria.forClass(GroupJoinRequest.class);
	
		if (pQueryBean.getCompleted() != null) {
			if (pQueryBean.getCompleted().booleanValue()) {
				criteria.add(Restrictions.isNotNull("responseDate"));
			}
			else {
				criteria.add(Restrictions.isNull("responseDate"));
			}			
		}
		
		if (pQueryBean.getResponseIds() != null && pQueryBean.getResponseIds().length > 0) {
			final DetachedCriteria responseCriteria = criteria.createCriteria("response", CriteriaSpecification.INNER_JOIN);
			responseCriteria.add(Restrictions.in("id", pQueryBean.getResponseIds()));
		}
		
		if (pQueryBean.getRequesterIds() != null && pQueryBean.getRequesterIds().length > 0) {
			final DetachedCriteria responseCriteria = criteria.createCriteria("requester", CriteriaSpecification.INNER_JOIN);
			responseCriteria.add(Restrictions.in("id", pQueryBean.getRequesterIds()));
		}
		
		if (pQueryBean.getGroupIds() != null && pQueryBean.getGroupIds().length > 0) {
			final DetachedCriteria responseCriteria = criteria.createCriteria("group", CriteriaSpecification.INNER_JOIN);
			responseCriteria.add(Restrictions.in("id", pQueryBean.getGroupIds()));
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

	public ListWithRowCount findGroupJoinRequests(final GroupJoinRequestDaoQueryBean pQueryBean) {
		final List list = findGroupJoinRequestsList(pQueryBean);
		final long count = countGroupJoinRequests(pQueryBean);
		
		return new ListWithRowCount(list, count);
	}
}

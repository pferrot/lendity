package com.pferrot.lendity.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.dao.GroupDao;
import com.pferrot.lendity.dao.bean.GroupDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.Group;

public class GroupDaoHibernateImpl extends HibernateDaoSupport implements GroupDao {

	public Long createGroup(final Group pGroup) {
		return (Long)getHibernateTemplate().save(pGroup);
	}

	public void deleteGroup(final Group pGroup) {
		getHibernateTemplate().delete(pGroup);
	}

	public void updateGroup(final Group pGroup) {
		getHibernateTemplate().update(pGroup);
	}

	public Group findGroup(final Long personId) {
		return (Group)getHibernateTemplate().load(Group.class, personId);
	}
	
	
	public List<Group> findGroupsList(final GroupDaoQueryBean pGroupDaoQueryBean) {
		final DetachedCriteria criteria = getGroupsDetachedCriteria(pGroupDaoQueryBean);
		
		if (!StringUtils.isNullOrEmpty(pGroupDaoQueryBean.getOrderBy())) {
			if ("random".equals(pGroupDaoQueryBean.getOrderBy())) {
				// This is MySql specific !!!
				criteria.add(Restrictions.sqlRestriction("1=1 order by rand()"));
			}
			else {
				// Ascending.
				if (pGroupDaoQueryBean.getOrderByAscending() == null || pGroupDaoQueryBean.getOrderByAscending().booleanValue()) {
					criteria.addOrder(Order.asc(pGroupDaoQueryBean.getOrderBy()).ignoreCase());
				}
				// Descending.
				else {
					criteria.addOrder(Order.desc(pGroupDaoQueryBean.getOrderBy()).ignoreCase());
				}
			}
		}
		return getHibernateTemplate().findByCriteria(criteria, pGroupDaoQueryBean.getFirstResult(), pGroupDaoQueryBean.getMaxResults());
	}
	
	public long countGroups(final GroupDaoQueryBean pGroupDaoQueryBean) {
		final DetachedCriteria criteria = getGroupsDetachedCriteria(pGroupDaoQueryBean);
		return rowCount(criteria);
	}

	public ListWithRowCount findGroups(final GroupDaoQueryBean pGroupDaoQueryBean) {
		final List list = findGroupsList(pGroupDaoQueryBean);
		final long count = countGroups(pGroupDaoQueryBean);

		return new ListWithRowCount(list, count);
	}

	private DetachedCriteria getGroupsDetachedCriteria(final GroupDaoQueryBean pGroupDaoQueryBean) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(Group.class);
		
		if (!StringUtils.isNullOrEmpty(pGroupDaoQueryBean.getTitle())) {
			criteria.add(Restrictions.ilike("title", pGroupDaoQueryBean.getTitle(), MatchMode.ANYWHERE));
		}

		if (!StringUtils.isNullOrEmpty(pGroupDaoQueryBean.getDescription())) {
			criteria.add(Restrictions.ilike("description", pGroupDaoQueryBean.getDescription(), MatchMode.ANYWHERE));
		}
		boolean useSubquery = false;
		if (pGroupDaoQueryBean.getOwnerOrAdministratorsOrMembersIds() != null) {
			criteria.createAlias("owner", "o", CriteriaSpecification.LEFT_JOIN);
			criteria.createAlias("members", "m", CriteriaSpecification.LEFT_JOIN);
			criteria.createAlias("administrators", "a", CriteriaSpecification.LEFT_JOIN);
			
			
			criteria.add(Restrictions.or(
							Restrictions.in("o.id", pGroupDaoQueryBean.getOwnerOrAdministratorsOrMembersIds()),
							Restrictions.or(
									Restrictions.in("m.id", pGroupDaoQueryBean.getOwnerOrAdministratorsOrMembersIds()),
									Restrictions.in("a.id", pGroupDaoQueryBean.getOwnerOrAdministratorsOrMembersIds()))));
			if (!useSubquery) {
				useSubquery = true;
				criteria.setProjection(Projections.distinct(Projections.id()));
			}
		}
		
		if (pGroupDaoQueryBean.getOwnerOrAdministratorsIds() != null) {
			criteria.createAlias("owner", "o2", CriteriaSpecification.LEFT_JOIN);
			criteria.createAlias("administrators", "a2", CriteriaSpecification.LEFT_JOIN);
			
			
			criteria.add(Restrictions.or(
							Restrictions.in("o2.id", pGroupDaoQueryBean.getOwnerOrAdministratorsIds()),
							Restrictions.in("a2.id", pGroupDaoQueryBean.getOwnerOrAdministratorsIds())));
			
			if (!useSubquery) {
				useSubquery = true;
				criteria.setProjection(Projections.distinct(Projections.id()));
			}
		}
		
		if (pGroupDaoQueryBean.getOwnerIds() != null) {
			criteria.add(Restrictions.in("owner.id", pGroupDaoQueryBean.getOwnerIds()));
		}
		
		if (pGroupDaoQueryBean.getAdministratorsIds() != null) {
			final DetachedCriteria c = criteria.createCriteria("administrators", CriteriaSpecification.INNER_JOIN);
			c.add(Restrictions.in("id", pGroupDaoQueryBean.getAdministratorsIds()));
		}
		
		if (pGroupDaoQueryBean.getMembersIds() != null) {
			final DetachedCriteria c = criteria.createCriteria("members", CriteriaSpecification.INNER_JOIN);
			c.add(Restrictions.in("id", pGroupDaoQueryBean.getMembersIds()));
		}
		
		if (pGroupDaoQueryBean.getBannedPersonsIds() != null) {
			final DetachedCriteria c = criteria.createCriteria("bannedPersons", CriteriaSpecification.INNER_JOIN);
			c.add(Restrictions.in("id", pGroupDaoQueryBean.getBannedPersonsIds()));
		}
		
		if (pGroupDaoQueryBean.getValidateMembership() != null) {
			criteria.add(Restrictions.eq("validateMembership", pGroupDaoQueryBean.getValidateMembership()));
		}
		
		if (useSubquery) {
			final DetachedCriteria selectCriteria = DetachedCriteria.forClass(Group.class);
			selectCriteria.add(Subqueries.propertyIn("id", criteria));
			return selectCriteria;
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
}
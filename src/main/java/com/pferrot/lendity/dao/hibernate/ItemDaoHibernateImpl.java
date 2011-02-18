package com.pferrot.lendity.dao.hibernate;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.dao.ItemDao;
import com.pferrot.lendity.dao.bean.ItemDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.dao.hibernate.criterion.CustomSqlCriterion;
import com.pferrot.lendity.geolocation.GeoLocationConsts;
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

	public List<InternalItem> findInternalItemsList(final ItemDaoQueryBean pItemDaoQueryBean) {
		return (List<InternalItem>)findItemsList(pItemDaoQueryBean, InternalItem.class);
		
	}
	
	private List findItemsList(final ItemDaoQueryBean pItemDaoQueryBean, final Class pClass) {
		
		final DetachedCriteria criteria = getItemsDetachedCriteria(pItemDaoQueryBean, pClass);
		
		if (!StringUtils.isNullOrEmpty(pItemDaoQueryBean.getOrderBy())) {
			// Ascending.
			if (pItemDaoQueryBean.getOrderByAscending() == null || pItemDaoQueryBean.getOrderByAscending().booleanValue()) {
				criteria.addOrder(Order.asc(pItemDaoQueryBean.getOrderBy()).ignoreCase());
			}
			// Descending.
			else {
				criteria.addOrder(Order.desc(pItemDaoQueryBean.getOrderBy()).ignoreCase());
			}
		}
		return getHibernateTemplate().findByCriteria(criteria, pItemDaoQueryBean.getFirstResult(), pItemDaoQueryBean.getMaxResults());
	}

	private long countItems(final ItemDaoQueryBean pItemDaoQueryBean, final Class pClass) {
		final DetachedCriteria criteria = getItemsDetachedCriteria(pItemDaoQueryBean, pClass);
		return rowCount(criteria);
	}
	
	public long countInternalItems(final ItemDaoQueryBean pItemDaoQueryBean) {
		return countItems(pItemDaoQueryBean, InternalItem.class);
	}
	
	private DetachedCriteria getItemsDetachedCriteria(final ItemDaoQueryBean pItemDaoQueryBean, final Class pClass) {
		Class theClass = pClass;
		// Only internal items have an owner.
		if (pItemDaoQueryBean.getOwnerIds() != null && pItemDaoQueryBean.getOwnerIds().length > 0) {
			theClass = InternalItem.class;
		}
//		if (Boolean.TRUE.equals(pShowAllPublic) && pMaxDistanceKm == null) {
//			throw new IllegalArgumentException("Must specify a max distance when querying for all public items.");
//		}
		DetachedCriteria criteria = DetachedCriteria.forClass(theClass);
	
		if (pItemDaoQueryBean.getTitle() != null && pItemDaoQueryBean.getTitle().trim().length() > 0) {
			criteria.add(Restrictions.ilike("title", pItemDaoQueryBean.getTitle(), MatchMode.ANYWHERE));
		}
		
		// Visibility and owner.
		Criterion c = null;
		if (pItemDaoQueryBean.getOwnerIds() != null && pItemDaoQueryBean.getOwnerIds().length > 0) {
			c = Restrictions.in("owner.id", pItemDaoQueryBean.getOwnerIds());
		}
		if (pItemDaoQueryBean.getVisibilityIds() != null && pItemDaoQueryBean.getVisibilityIds().length > 0) {
			if (c != null) {
				c = Restrictions.and(c, Restrictions.in("visibility.id", pItemDaoQueryBean.getVisibilityIds()));
			}
			else {
				c = Restrictions.in("visibility.id", pItemDaoQueryBean.getVisibilityIds());
			}
		}
		if (pItemDaoQueryBean.getVisibilityIdsToForce() != null && pItemDaoQueryBean.getVisibilityIdsToForce().length > 0) {
			// Only need to do an OR if there is a criterion already.
			if (c != null) {
				Criterion c1 = Restrictions.in("visibility.id", pItemDaoQueryBean.getVisibilityIdsToForce());
				if (pItemDaoQueryBean.getOwnerIdsToExcludeForVisibilityIdsToForce() != null && pItemDaoQueryBean.getOwnerIdsToExcludeForVisibilityIdsToForce().length > 0) {
					c1 = Restrictions.and(c1, Restrictions.not(Restrictions.in("owner.id", pItemDaoQueryBean.getOwnerIdsToExcludeForVisibilityIdsToForce())));
				}
				c = Restrictions.or(c, c1);
			}
		}
		if (c != null) {
			criteria.add(c);
		}
		
		DetachedCriteria ownerCriteria = null;
		// Exclude public items owned by persons that do not want to share their address if filtering by distance.
		if (pItemDaoQueryBean.getOwnerIds() != null && pItemDaoQueryBean.getOwnerIds().length > 0 &&
			pItemDaoQueryBean.getMaxDistanceKm() != null) {
			ownerCriteria = criteria.createCriteria("owner", CriteriaSpecification.INNER_JOIN);
			c = Restrictions.in("id", pItemDaoQueryBean.getOwnerIds());
			if (pItemDaoQueryBean.getMaxDistanceKm() != null) {
				c = Restrictions.or(c, Restrictions.eq("showContactDetailsToAll", Boolean.TRUE));
			}
			ownerCriteria.add(c);
		}
		if (pItemDaoQueryBean.getOwnerEnabled() != null) {
			if (ownerCriteria == null) {
				ownerCriteria = criteria.createCriteria("owner", CriteriaSpecification.INNER_JOIN);
			}
			ownerCriteria.add(Restrictions.eq("enabled", pItemDaoQueryBean.getOwnerEnabled()));
		}		
		
		if (pItemDaoQueryBean.getBorrowed() != null) {
			if (pItemDaoQueryBean.getBorrowed().booleanValue()) {
				criteria.add(Restrictions.isNotNull("borrowDate"));
			}
			else {
				criteria.add(Restrictions.isNull("borrowDate"));
			}
		}
		
		if (pItemDaoQueryBean.getCategoryIds() != null && pItemDaoQueryBean.getCategoryIds().length > 0) {
			criteria.createCriteria("category", CriteriaSpecification.INNER_JOIN).
				add(Restrictions.in("id", pItemDaoQueryBean.getCategoryIds()));
		}		
		
		if (pItemDaoQueryBean.getBorrowerIds() != null && pItemDaoQueryBean.getBorrowerIds().length > 0) {
			final DetachedCriteria borrowerCriteria = criteria.createCriteria("borrower", CriteriaSpecification.INNER_JOIN);
			borrowerCriteria.add(Restrictions.in("id", pItemDaoQueryBean.getBorrowerIds()));
			if (pItemDaoQueryBean.getBorrowerEnabled() != null) {
				borrowerCriteria.add(Restrictions.eq("enabled", pItemDaoQueryBean.getBorrowerEnabled()));
			}
		}
		
		if (pItemDaoQueryBean.getCreationDateMin() != null) {
			criteria.add(Restrictions.gt("creationDate", pItemDaoQueryBean.getCreationDateMin()));
		}

		if (pItemDaoQueryBean.getMaxDistanceKm() != null) {
			// Calculate a rectangle and only consider persons in that rectangle for obvious performance reason.
			double maxDistanceKm = pItemDaoQueryBean.getMaxDistanceKm().doubleValue();
			double originLatitude = pItemDaoQueryBean.getOriginLatitude().doubleValue();
			double originLongitude = pItemDaoQueryBean.getOriginLongitude().doubleValue();
			
			double deltaLongitude =  maxDistanceKm / Math.abs(Math.cos(Math.toRadians(originLatitude)) * GeoLocationConsts.ONE_DEGRE_LATITUDE_KM);
			double deltaLatitude = maxDistanceKm / GeoLocationConsts.ONE_DEGRE_LATITUDE_KM;
			
			double longitude1 = originLongitude - deltaLongitude;
			double longitude2 = originLongitude + deltaLongitude;
			
			double latitude1 = originLatitude - deltaLatitude; 
			double latitude2 = originLatitude + deltaLatitude;
			
			if (ownerCriteria == null) {
				ownerCriteria = criteria.createCriteria("owner", CriteriaSpecification.INNER_JOIN);
			}			
			
			// Pre-filtering for performance reason. Records not in that square do not need
			// to be considered. 
			ownerCriteria.add(Restrictions.ge("addressHomeLatitude", Double.valueOf(latitude1)));
			ownerCriteria.add(Restrictions.le("addressHomeLatitude", Double.valueOf(latitude2)));
			ownerCriteria.add(Restrictions.ge("addressHomeLongitude", Double.valueOf(longitude1)));
			ownerCriteria.add(Restrictions.le("addressHomeLongitude", Double.valueOf(longitude2)));			
			
			// Fine grain the result - calculate the exact distance.
			// It would be too expensive to do that for all records, that is why we pre-filter with
			// a square above.
			final String sql = "(acos(sin(? * pi()/180) * sin({0} * pi()/180) + " +
				"cos(? * pi()/180) * cos({1} * pi()/180) * cos(({2} - ?) * pi()/180)) * 6371) <= ?";
			
			final String[] propertyNames = {"addressHomeLatitude", "addressHomeLatitude", "addressHomeLongitude"};
			final Object[] values = {pItemDaoQueryBean.getOriginLatitude(), pItemDaoQueryBean.getOriginLatitude(),
					pItemDaoQueryBean.getOriginLongitude(), pItemDaoQueryBean.getMaxDistanceKm()};
			final Type[] types = {Hibernate.DOUBLE, Hibernate.DOUBLE, Hibernate.DOUBLE, Hibernate.DOUBLE};		
			
			ownerCriteria.add(CustomSqlCriterion.sqlRestriction(sql, propertyNames, values, types));
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

	public ListWithRowCount findInternalItems(final ItemDaoQueryBean pItemDaoQueryBean) {
		final List list = findItemsList(pItemDaoQueryBean, InternalItem.class);
		final long count = countItems(pItemDaoQueryBean, InternalItem.class);
		
		return new ListWithRowCount(list, count);
	}
}

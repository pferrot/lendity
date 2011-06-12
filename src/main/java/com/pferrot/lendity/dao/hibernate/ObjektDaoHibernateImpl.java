package com.pferrot.lendity.dao.hibernate;

import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.lendity.dao.bean.ObjektDaoQueryBean;
import com.pferrot.lendity.dao.hibernate.criterion.CustomSqlCriterion;
import com.pferrot.lendity.geolocation.GeoLocationConsts;
import com.pferrot.lendity.geolocation.GeoLocationUtils;

public abstract class ObjektDaoHibernateImpl extends HibernateDaoSupport {

	protected abstract Class getObjectClass();
	
	protected DetachedCriteria getObjectsDetachedCriteria(final ObjektDaoQueryBean pObjectDaoQueryBean) {

		DetachedCriteria criteria = DetachedCriteria.forClass(getObjectClass());
	
		if (pObjectDaoQueryBean.getTitle() != null && pObjectDaoQueryBean.getTitle().trim().length() > 0) {
			criteria.add(Restrictions.ilike("title", pObjectDaoQueryBean.getTitle(), MatchMode.ANYWHERE));
		}

		// Visibility and owner.
		Criterion c = null;
		if (pObjectDaoQueryBean.getOwnerIds() != null && pObjectDaoQueryBean.getOwnerIds().length > 0) {
			c = Restrictions.in("owner.id", pObjectDaoQueryBean.getOwnerIds());
		}
//		if (pObjectDaoQueryBean.getOwnerIdsToExcludeForVisibilityIdsToForce() != null && pObjectDaoQueryBean.getOwnerIdsToExcludeForVisibilityIdsToForce().length > 0) {
//			if (c != null) {
//				c = Restrictions.and(c, Restrictions.not(Restrictions.in("owner.id", pObjectDaoQueryBean.getOwnerIdsToExcludeForVisibilityIdsToForce())));
//			}
//			else {
//				c = Restrictions.not(Restrictions.in("owner.id", pObjectDaoQueryBean.getOwnerIdsToExcludeForVisibilityIdsToForce()));
//			}
//		}
		if (pObjectDaoQueryBean.getVisibilityIds() != null && pObjectDaoQueryBean.getVisibilityIds().length > 0) {
			if (c != null) {
				c = Restrictions.and(c, Restrictions.in("visibility.id", pObjectDaoQueryBean.getVisibilityIds()));
			}
			else {
				c = Restrictions.in("visibility.id", pObjectDaoQueryBean.getVisibilityIds());
			}
		}
		if (pObjectDaoQueryBean.getVisibilityIdsToForce() != null && pObjectDaoQueryBean.getVisibilityIdsToForce().length > 0) {
			// Only need to do an OR if there is a criterion already.
			if (c != null) {
				Criterion c1 = Restrictions.in("visibility.id", pObjectDaoQueryBean.getVisibilityIdsToForce());
				if (pObjectDaoQueryBean.getOwnerIdsToExcludeForVisibilityIdsToForce() != null && pObjectDaoQueryBean.getOwnerIdsToExcludeForVisibilityIdsToForce().length > 0) {
					c1 = Restrictions.and(c1, Restrictions.not(Restrictions.in("owner.id", pObjectDaoQueryBean.getOwnerIdsToExcludeForVisibilityIdsToForce())));
				}
				c = Restrictions.or(c, c1);
			}
		}
		if (pObjectDaoQueryBean.getGroupIds() != null && pObjectDaoQueryBean.getGroupIds().length > 0) {
			criteria.createAlias("groupsAuthorized", "ga", CriteriaSpecification.LEFT_JOIN);
			Criterion c1 = Restrictions.in("ga.id", pObjectDaoQueryBean.getGroupIds());
			if (pObjectDaoQueryBean.getOwnerIdsToExcludeForVisibilityIdsToForce() != null && pObjectDaoQueryBean.getOwnerIdsToExcludeForVisibilityIdsToForce().length > 0) {
				c1 = Restrictions.and(c1, Restrictions.not(Restrictions.in("owner.id", pObjectDaoQueryBean.getOwnerIdsToExcludeForVisibilityIdsToForce())));
			}
			if (c != null) {				
				c = Restrictions.or(c, c1);
			}
			else {
				c = c1;
			}
		}
		if (c != null) {
			criteria.add(c);
		}
		
		DetachedCriteria ownerCriteria = null;
		// Exclude public needs owned by persons that do not want to share their address if filtering by distance.
//		if (pObjectDaoQueryBean.getOwnerIds() != null && pObjectDaoQueryBean.getOwnerIds().length > 0 &&
//			pObjectDaoQueryBean.getMaxDistanceKm() != null) {
//			ownerCriteria = criteria.createCriteria("owner", CriteriaSpecification.INNER_JOIN);
//			ownerCriteria = criteria.createAlias("owner", "o", CriteriaSpecification.INNER_JOIN);
//			c = Restrictions.in("o.id", pObjectDaoQueryBean.getOwnerIds());
//			if (pObjectDaoQueryBean.getMaxDistanceKm() != null) {
//				c = Restrictions.or(c, Restrictions.eq("o.showContactDetailsToAll", Boolean.TRUE));
//			}
//			ownerCriteria.add(c);
//		}
		
		if (pObjectDaoQueryBean.getOwnerEnabled() != null) {
			if (ownerCriteria == null) {
//				ownerCriteria = criteria.createCriteria("owner", CriteriaSpecification.INNER_JOIN);
				ownerCriteria = criteria.createAlias("owner", "o", CriteriaSpecification.INNER_JOIN);
			}
			ownerCriteria.add(Restrictions.eq("o.enabled", pObjectDaoQueryBean.getOwnerEnabled()));
		}
		
		if (pObjectDaoQueryBean.getCategoryIds() != null && pObjectDaoQueryBean.getCategoryIds().length > 0) {
			criteria.createCriteria("category", CriteriaSpecification.INNER_JOIN).
				add(Restrictions.in("id", pObjectDaoQueryBean.getCategoryIds()));
		}

		if (pObjectDaoQueryBean.getCreationDateMin() != null) {
			criteria.add(Restrictions.gt("creationDate", pObjectDaoQueryBean.getCreationDateMin()));
		}

		if (pObjectDaoQueryBean.getMaxDistanceKm() != null) {
			// Calculate a rectangle and only consider persons in that rectangle for obvious performance reason.
			double maxDistanceKm = pObjectDaoQueryBean.getMaxDistanceKm().doubleValue();
			double originLatitude = pObjectDaoQueryBean.getOriginLatitude().doubleValue();
			double originLongitude = pObjectDaoQueryBean.getOriginLongitude().doubleValue();
			
			double deltaLongitude =  maxDistanceKm / Math.abs(Math.cos(Math.toRadians(originLatitude)) * GeoLocationConsts.ONE_DEGRE_LATITUDE_KM);
			double deltaLatitude = maxDistanceKm / GeoLocationConsts.ONE_DEGRE_LATITUDE_KM;
			
			double longitude1 = originLongitude - deltaLongitude;
			double longitude2 = originLongitude + deltaLongitude;
			
			double latitude1 = originLatitude - deltaLatitude; 
			double latitude2 = originLatitude + deltaLatitude;
			
			if (ownerCriteria == null) {
//				ownerCriteria = criteria.createCriteria("owner", CriteriaSpecification.INNER_JOIN);
				ownerCriteria = criteria.createAlias("owner", "o", CriteriaSpecification.INNER_JOIN);
			}			
			
			// Pre-filtering for performance reason. Records not in that square do not need
			// to be considered. 
			ownerCriteria.add(Restrictions.ge("o.addressHomeLatitude", Double.valueOf(latitude1)));
			ownerCriteria.add(Restrictions.le("o.addressHomeLatitude", Double.valueOf(latitude2)));
			ownerCriteria.add(Restrictions.ge("o.addressHomeLongitude", Double.valueOf(longitude1)));
			ownerCriteria.add(Restrictions.le("o.addressHomeLongitude", Double.valueOf(longitude2)));			
			
			// Fine grain the result - calculate the exact distance.
			// It would be too expensive to do that for all records, that is why we pre-filter with
			// a square above.
			final String sql = "(" + GeoLocationUtils.getDistanceFormula("?", "?", "?") + ") <= ?";
			
			final String[] propertyNames = {"o.addressHomeLatitude", "o.addressHomeLatitude", "o.addressHomeLongitude"};
			final Object[] values = {pObjectDaoQueryBean.getOriginLatitude(), pObjectDaoQueryBean.getOriginLatitude(),
					pObjectDaoQueryBean.getOriginLongitude(), pObjectDaoQueryBean.getMaxDistanceKm()};
			final Type[] types = {Hibernate.DOUBLE, Hibernate.DOUBLE, Hibernate.DOUBLE, Hibernate.DOUBLE};		
			
			ownerCriteria.add(CustomSqlCriterion.sqlRestriction(sql, propertyNames, values, types));
		}
		
		return criteria;	
	}
}

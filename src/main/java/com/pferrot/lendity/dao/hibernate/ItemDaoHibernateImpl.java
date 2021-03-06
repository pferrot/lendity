package com.pferrot.lendity.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.dao.ItemDao;
import com.pferrot.lendity.dao.bean.ItemDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.dao.hibernate.criterion.OrderBySql;
import com.pferrot.lendity.geolocation.GeoLocationUtils;
import com.pferrot.lendity.model.Item;

public class ItemDaoHibernateImpl extends ObjektDaoHibernateImpl implements ItemDao {

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

	public List<Item> findItemsList(final ItemDaoQueryBean pItemDaoQueryBean) {
		final DetachedCriteria criteria = getItemsDetachedCriteria(pItemDaoQueryBean);
		
		if (!StringUtils.isNullOrEmpty(pItemDaoQueryBean.getOrderBy())) {
			if ("random".equals(pItemDaoQueryBean.getOrderBy())) {
				// This is MySql specific !!!
				criteria.add(Restrictions.sqlRestriction("1=1 order by rand()"));
			}
			else if ("distance".equals(pItemDaoQueryBean.getOrderBy())) {
				final String ascOrDesc = Boolean.TRUE.equals(pItemDaoQueryBean.getOrderByAscending())?"asc":"desc";
				if (pItemDaoQueryBean.getOriginLatitude() != null && pItemDaoQueryBean.getOriginLongitude() != null) {
					// First order to make sure that persons where the distance cannot be calculated appear last.
					// Only check the latitude to simplify the query. If one of latitude/longitude is null, both should be null.
					criteria.addOrder(OrderBySql.sql("{0} is not null desc", new String[] {"o.addressHomeLatitude"}));
					// Actual sorting by distance.
					criteria.addOrder(
							OrderBySql.sql("(" + GeoLocationUtils.getDistanceFormula(pItemDaoQueryBean.getOriginLongitude(), pItemDaoQueryBean.getOriginLatitude()) + ") " + ascOrDesc,
												         new String[] {"o.addressHomeLatitude", "o.addressHomeLatitude", "o.addressHomeLongitude"})
					    );
				}	
				else {
					throw new RuntimeException("Cannot sort by distance when origin latitude/longitude are not defined");
				}
				
			}
			else {
				// Ascending.
				if (pItemDaoQueryBean.getOrderByAscending() == null || pItemDaoQueryBean.getOrderByAscending().booleanValue()) {
					criteria.addOrder(Order.asc(pItemDaoQueryBean.getOrderBy()).ignoreCase());
				}
				// Descending.
				else {
					criteria.addOrder(Order.desc(pItemDaoQueryBean.getOrderBy()).ignoreCase());
				}
			}
		}
		return getHibernateTemplate().findByCriteria(criteria, pItemDaoQueryBean.getFirstResult(), pItemDaoQueryBean.getMaxResults());	
	}
	
	public long countItems(final ItemDaoQueryBean pItemDaoQueryBean) {
		final DetachedCriteria criteria = getItemsDetachedCriteria(pItemDaoQueryBean);
		return rowCount(criteria);
	}
	
	protected DetachedCriteria getItemsDetachedCriteria(final ItemDaoQueryBean pItemDaoQueryBean) {
		DetachedCriteria criteria = getObjectsDetachedCriteria(pItemDaoQueryBean);
		
		if (pItemDaoQueryBean.getBorrowed() != null) {
			if (pItemDaoQueryBean.getBorrowed().booleanValue()) {
				criteria.add(Restrictions.isNotNull("borrowDate"));
			}
			else {
				criteria.add(Restrictions.isNull("borrowDate"));
			}
		}
		
		if (pItemDaoQueryBean.getBorrowerIds() != null && pItemDaoQueryBean.getBorrowerIds().length > 0) {
			final DetachedCriteria borrowerCriteria = criteria.createCriteria("borrower", CriteriaSpecification.INNER_JOIN);
			borrowerCriteria.add(Restrictions.in("id", pItemDaoQueryBean.getBorrowerIds()));
			if (pItemDaoQueryBean.getBorrowerEnabled() != null) {
				borrowerCriteria.add(Restrictions.eq("enabled", pItemDaoQueryBean.getBorrowerEnabled()));
			}
		}
		
		if (Boolean.TRUE.equals(pItemDaoQueryBean.getToLend())) {
			criteria.add(
					Restrictions.or(
							Restrictions.isNull("rentalFee"),
							Restrictions.le("rentalFee", Double.valueOf(0)
							)
					)
			);
		}

		if (Boolean.TRUE.equals(pItemDaoQueryBean.getToRent())) {
			criteria.add(Restrictions.gt("rentalFee", Double.valueOf(0)));
		}
		
		if (Boolean.TRUE.equals(pItemDaoQueryBean.getToGiveForFree())) {
			criteria.add(
					Restrictions.or(
							Restrictions.eq("toGiveForFree", Boolean.TRUE),
							Restrictions.le("salePrice", Double.valueOf(0)
							)
					)
			);
		}

		if (Boolean.TRUE.equals(pItemDaoQueryBean.getToSell())) {
			criteria.add(Restrictions.gt("salePrice", Double.valueOf(0)));
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

	public ListWithRowCount findItems(final ItemDaoQueryBean pItemDaoQueryBean) {
		final List list = findItemsList(pItemDaoQueryBean);
		final long count = countItems(pItemDaoQueryBean);
		
		return new ListWithRowCount(list, count);
	}

	@Override
	protected Class getObjectClass() {
		return Item.class;
	}
}

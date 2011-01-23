package com.pferrot.lendity.dao.hibernate.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.util.StringHelper;

/**
 * Some useful hibernate methods.
 *
 * @author pferrot
 *
 */
public class HibernateUtils {
	
	private final static Log log = LogFactory.getLog(HibernateUtils.class);
	
	private static SessionFactory sessionFactory;

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Given an input string, replace the property names
     * Usage: HibernateUtils.replacePropertyNames(sql, propertyNames, criteria, criteriaQuery);
     * Example: HibernateUtils.replacePropertyNames("SELECT {0} FROM entity WHERE {1}='biz'", new String[]{"foo","bat"}, criteria, criteriaQuery);
     * 
	 * @param pSql
	 * @param pPropertyNames
	 * @param pCriteria
	 * @param pCriteriaQuery
	 * @return
	 */
	public static String replacePropertyNames(final String pSql, final String[] pPropertyNames, final Criteria pCriteria, final CriteriaQuery pCriteriaQuery) {
		String retval = pSql;
		int x = 0;
		for(String propertyName: pPropertyNames) {
			// extract alias and colum name
			String[] columns = pCriteriaQuery.getColumnsUsingProjection(pCriteria, propertyName);
			StringBuffer fragment = new StringBuffer();

			// if we have multiple columns
			if (columns.length > 1) {
				fragment.append('(');
			}

			// get session factory
//			SessionFactoryImplementor factory = criteriaQuery.getFactory();

			// get column name
			for (int i = 0; i < columns.length; i++) {
				fragment.append(columns[i]);
			}

			if (columns.length > 1) {
				fragment.append(')');
			}

			// replace property with column name
			retval = StringHelper.replace(retval, "{" + x + "}", fragment.toString());
			x++;
		}
		if (log.isDebugEnabled()) {
			log.debug("SQL after propertyNames replacement: " + retval);
		}
		return retval;
	}
	
	/**
	 * Evict a given cache region.
	 *
	 * @param pRegion
	 */
	public static void evictQueryCacheRegion(final String pRegion) {
		getSessionFactory().getCache().evictQueryRegion(pRegion);
	}
	
}

package com.pferrot.lendity.dao.hibernate.criterion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Order;

import com.pferrot.lendity.dao.hibernate.utils.HibernateUtils;

/**
 * Extends org.hibernate.criterion.Order to allow ordering by an SQL formula passed by the user.
 * 
 * @author pferrot
 *
 */
public class OrderBySql extends Order {
	
	private final static Log log = LogFactory.getLog(OrderBySql.class);
	
    private String sqlFormula;
    private String[] propertyNames;
 
    protected OrderBySql(final String pSql, final String[] pPropertyNames) {
        super(pSql, true);
        this.sqlFormula = pSql;
        this.propertyNames = pPropertyNames;
    }
 
    public String toString() {
        return sqlFormula;
    }
 
    public String toSqlString(final Criteria pCriteria, final CriteriaQuery pCriteriaQuery) throws HibernateException {
    	return HibernateUtils.replacePropertyNames(sqlFormula, propertyNames, pCriteria, pCriteriaQuery);
    }

//    public String toSqlString2(Criteria criteria, CriteriaQuery criteriaQuery)
//         throws HibernateException {
//    	String propertyName = null;
//    	boolean ascending = true;
//    	boolean ignoreCase = true;
//    	
//             String [] columns = criteriaQuery.getColumnsUsingProjection(criteria, propertyName);
//             Type type = criteriaQuery.getTypeUsingProjection(criteria, propertyName);
//             StringBuffer fragment = new StringBuffer();
//             for ( int i=0; i<columns.length; i++ ) {
//                 SessionFactoryImplementor factory = criteriaQuery.getFactory();
//                 boolean lower = ignoreCase && type.sqlTypes( factory )[i]==Types.VARCHAR;
//                 if (lower) {
//                     fragment.append( factory.getDialect().getLowercaseFunction() )
//                         .append('(');
//                 }
//                 fragment.append( columns[i] );
//                 if (lower) fragment.append(')');
//                 fragment.append( ascending ? " asc" : " desc" );
//                 if ( i<columns.length-1 ) fragment.append(", ");
//             }
//             return fragment.toString();
//         }

 
    /**
     * Custom order
     *
     * @param sqlFormula an SQL formula that will be appended to the resulting SQL query
     * @return Order
     */
    public static Order sql(final String pSql) {
        return new OrderBySql(pSql, null);
    }
    
    public static Order sql(final String pSql, final String[] pPropertyNames) {
        return new OrderBySql(pSql, pPropertyNames);
    }
    
//    private String replacePropertyNames(final String pSqlFormula, final String[] pPropertyNames,
//    		final Criteria pCriteria, final CriteriaQuery pCriteriaQuery) {
//    	if (pPropertyNames == null) {
//    		return pSqlFormula;
//    	}
//    	
//    	String result = pSqlFormula;
//    	for (String propertyName: pPropertyNames) {
//    		String[] columns = pCriteriaQuery.getColumnsUsingProjection(pCriteria, propertyName);
//    		if (columns.length != 1) {
//    			throw new HibernateException("Expected 1 column, got " + columns.length);
//    		}
//    		result = result.replaceFirst("\\?", columns[0]);
//    	}
//    	
//    	if (log.isErrorEnabled()) {
//    		log.error("sqlFormula after parameters replacement: " + result);
//    	}
//    	
//    	return result;    	
//    }
}

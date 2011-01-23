package com.pferrot.lendity.dao.hibernate.criterion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.TypedValue;
import org.hibernate.type.Type;

import com.pferrot.lendity.dao.hibernate.utils.HibernateUtils;

/**
 * Adapted from http://javarobski.blogspot.com/2010/01/restrictionssqlrestriction-extract.html
 * to accept multiple property names in a single SQL statement.
 *
 * Usage: new CustomSqlCriterion(sql, propertyNames, ArrayHelper.EMPTY_OBJECT_ARRAY, ArrayHelper.EMPTY_TYPE_ARRAY);
 * Example: new CustomSqlCriterion("SELECT {0} FROM entity WHERE {1}='biz'", new String[]{"foo","bat"}, ArrayHelper.EMPTY_OBJECT_ARRAY, ArrayHelper.EMPTY_TYPE_ARRAY);
 *
 * @author spork
 *
 */
public class CustomSqlCriterion implements Criterion {
	
	private final static Log log = LogFactory.getLog(CustomSqlCriterion.class);
	
	private String sql;
	private String[] propertyNames;
	private TypedValue[] typedValues;

	public String toSqlString(final Criteria pCriteria, final CriteriaQuery pCriteriaQuery) throws HibernateException {
		return HibernateUtils.replacePropertyNames(sql, propertyNames, pCriteria, pCriteriaQuery);
	}

	public TypedValue[] getTypedValues(final Criteria pCriteria, final CriteriaQuery pCriteriaQuery) throws HibernateException {
		return typedValues;
	}

	public String toString() {
		return sql;
	}

	protected CustomSqlCriterion(final String pSql, final String[] pPropertyNames, final Object[] pValues, final Type[] pTypes) {
		this.sql = pSql;
		this.propertyNames = pPropertyNames;

		typedValues = new TypedValue[pValues.length];
		for (int i = 0; i < typedValues.length; i++) {
			typedValues[i] = new TypedValue(pTypes[i], pValues[i], EntityMode.POJO);
		}
	}
	
	public static CustomSqlCriterion sqlRestriction(final String pSql, final String[] pPropertyNames, final Object[] pValues, final Type[] pTypes) {
		return new CustomSqlCriterion(pSql, pPropertyNames, pValues, pTypes);
	}
}

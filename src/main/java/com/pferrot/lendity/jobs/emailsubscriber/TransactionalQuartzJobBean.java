package com.pferrot.lendity.jobs.emailsubscriber;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Hibernate/Spring can be a pain, because hibernate sessions are not propagated. For JSP pages there is an
 * interceptor taking care of it. Quartz tasks are outside of the scope of the view interceptor. So, this
 * class wraps a transaction around it and makes sure that hibernate POJOs can load lazily.
 *
 * Make sure that the spring reference to the hibernateSessionFactory is satisfied in the spring mapping.
 * 
 * http://guanyu.info/blog/?p=82
 *
 * @author Thomas Fischer
 */
public abstract class TransactionalQuartzJobBean extends QuartzJobBean {

	private final static Log log = LogFactory.getLog(TransactionalQuartzJobBean.class);

	// spring injected reference
	private SessionFactory hibernateSessionFactory;
	/**
	 * Most of this method is copied from the HibernateInterceptor.
	 */
	protected final void executeInternal(JobExecutionContext ctx) throws JobExecutionException {

		Session session = SessionFactoryUtils.getSession(hibernateSessionFactory, true);
		boolean existingTransaction = SessionFactoryUtils.isSessionTransactional(session, getHibernateSessionFactory());
		if (existingTransaction) {
			log.debug("Found thread-bound Session for TransactionalQuartzTask");
		}
		else {
			TransactionSynchronizationManager.bindResource(getHibernateSessionFactory(), new SessionHolder(session));
		}

		try {
			executeTransactional(ctx);
		}
		catch (HibernateException ex) {
			throw ex;
		}
		finally {
			if (existingTransaction) {
				log.debug("Not closing pre-bound Hibernate Session after TransactionalQuartzTask");
			}
			else {
				TransactionSynchronizationManager.unbindResource(getHibernateSessionFactory());
				SessionFactoryUtils.releaseSession(session, getHibernateSessionFactory());
			}
		}
	}

	/**
	 * Implementing classes, implement this method.
	 */
	protected abstract void executeTransactional(JobExecutionContext ctx) throws JobExecutionException;

	public SessionFactory getHibernateSessionFactory() {
		return hibernateSessionFactory;
	}

	public void setHibernateSessionFactory(SessionFactory hibernateSessionFactory) {
		this.hibernateSessionFactory = hibernateSessionFactory;
	}
}

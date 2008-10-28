/**
 * From myfaces-orchestra-goodies, found on code.google.com: http://code.google.com/p/myfaces-orchestra-goodies/
 * 
 * svn checkout http://myfaces-orchestra-goodies.googlecode.com/svn/trunk/ myfaces-orchestra-goodies-read-only
 */
package com.pferrot.orchestra.hibernate;

import org.apache.myfaces.orchestra.conversation.spring.PersistenceContext;
import org.apache.myfaces.orchestra.conversation.spring.PersistenceContextFactory;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Stack;

public class HibernatePersistenceContextFactory implements PersistenceContextFactory 
{
    private SessionFactory sessionFactory;

    public PersistenceContext create()
    {
        final Session em = openSesssion();
        em.setFlushMode(FlushMode.COMMIT);

        return new PersistenceContext()
        {
            private final Stack bindings = new Stack();

            public void bind()
            {
                synchronized(bindings)
                {
                    SessionHolder current = (SessionHolder)
                        TransactionSynchronizationManager.getResource(sessionFactory);

                    if (current != null)
                    {
                        TransactionSynchronizationManager.unbindResource(sessionFactory);
                    }

                    bindings.push(current);

                    TransactionSynchronizationManager.bindResource(sessionFactory,
                        new SessionHolder(em));
                }
            }

            public void unbind()
            {
                synchronized(bindings)
                {
                    if (TransactionSynchronizationManager.hasResource(sessionFactory))
                    {
                        TransactionSynchronizationManager.unbindResource(sessionFactory);
                    }

                    Object holder = null;
                    if (bindings.size() > 0)
                    {
                        holder = bindings.pop();
                    }
                    if (holder != null)
                    {
                        TransactionSynchronizationManager.bindResource(sessionFactory,
                            holder);
                    }
                }
            }

            public void close()
            {
                em.close();
            }
        };
    }

    public SessionFactory getEntityManagerFactory()
    {
        return sessionFactory;
    }

    public void setEntityManagerFactory(SessionFactory entityManagerFactory)
    {
        this.sessionFactory = entityManagerFactory;
    }

    protected Session openSesssion()
    {
        return sessionFactory.openSession();
    }
}

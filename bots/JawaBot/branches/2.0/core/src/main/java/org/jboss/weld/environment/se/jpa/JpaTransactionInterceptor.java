//package de.laliluna.transactions;
package org.jboss.weld.environment.se.jpa;

import javax.annotation.PostConstruct;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;

/**
 * Now, we can create a transaction interceptor. It will start a transaction before a transactional method is called and commit it after the method was executed.
 * In addition it will register an entity manager in the EntityManagerStore and unregister it afterwards. 
 * Finally, it will roll back transactions in case of an exception.
 * 
 * The annotations @Interceptor and @Transactional tell Weld that every time it finds a @Transactional annotation it needs to call the interceptor.
 * 
 * 
 * A simple transaction interceptor which registers an entity manager in a ThreadLocal and unregisters after the
 * method was called.
 * It does not support any kind of context propagation. If a transactional method calls another's bean transactional
 * method a new entity manager is created and added to the stack.
 *
 * @author Sebastian Hennebrueder
 */
@Interceptor
@JpaTransactional
public class JpaTransactionInterceptor {
		private final Logger log = LoggerFactory.getLogger( JpaTransactionInterceptor.class );

		@Inject
      private EntityManagerStore entityManagerStore;

		@AroundInvoke
      @PostConstruct
		public Object runInTransaction( InvocationContext invocationContext ) throws Exception {

            log.debug(" In "+JpaTransactionInterceptor.class.getSimpleName() 
                    +" - @AroundInvoke for " 
                    + invocationContext.getTarget().getClass().getSimpleName()
                    + invocationContext.getMethod().getName() );

				// Create / get EM.
				EntityManager em = entityManagerStore.createAndRegister();

				Object result = null;
				try {
                  log.debug("  Starting transaction...");
						em.getTransaction().begin();
                  log.debug("  Invoking the intercepted method...");
						result = invocationContext.proceed();
                  log.debug("  Committing transaction...");
						em.getTransaction().commit();
				}
				catch (Exception e) {
						try {
								if (em.getTransaction().isActive()) {
										em.getTransaction().rollback();
										log.debug("Rolled back transaction");
								}
						}
						catch (HibernateException e1) {
								log.warn("Rollback of transaction failed: " + e1);
						}
						throw e;
				}
				finally {
						if (em != null) {
								entityManagerStore.unregister(em);
								em.close();
						}
				}

				return result;
		}
}// class

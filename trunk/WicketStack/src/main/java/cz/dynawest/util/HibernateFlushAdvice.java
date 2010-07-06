/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.dynawest.util;

import java.lang.reflect.Method;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * AOP advisor, ktery flushne Hibernate context.
 * @author Ondrej Zizka
 */
public class HibernateFlushAdvice implements org.springframework.aop.MethodBeforeAdvice {

	// EMF.
	private EntityManagerFactory emf;
	public EntityManagerFactory getEntityManagerFactory() {		return emf;	}
	public void setEntityManagerFactory(EntityManagerFactory emf) {		this.emf = emf;	}


	// Before method flush HBN.
	public void before(Method arg0, Object[] arg1, Object arg2) throws Throwable {
		EntityManager em = emf.createEntityManager();
		em.flush();
	}

}// class

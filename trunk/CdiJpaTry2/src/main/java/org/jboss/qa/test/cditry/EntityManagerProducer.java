package org.jboss.qa.test.cditry;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 *
 * @author Ondrej Zizka
 */
@ApplicationScoped
public class EntityManagerProducer {
		
		@PersistenceUnit(unitName="TestPU") private EntityManagerFactory emf;
		
		@Produces public EntityManager getEntityManager(){
				return emf.createEntityManager();
		}
		
}// class


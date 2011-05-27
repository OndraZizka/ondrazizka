package org.jboss.qa.test.cditry;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.qa.test.cditry.ent.Reservation;

/**
 * 
 * @author Ondrej Zizka
 */
@ApplicationScoped
public class JpaTestService {
		
		//@PersistenceContext private PersistenceContext pc;
		//@PersistenceContext(unitName="TestPU")
		@Inject
		private EntityManager em;
		
		public void doSomeJpaStuff(){
				
				Reservation res = em.find( Reservation.class, new Long(1) );
				
		}
		
}// class

package org.jboss.qa.test.cditry;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.jboss.qa.test.cditry.ent.Reservation;

/**
 *
 * @author Ondrej Zizka
 */
@ApplicationScoped
public class JpaTestService {
		
		//@PersistenceContext private PersistenceContext pc;
		@Inject EntityManager em;
		
		public void doSomeJpaStuff(){
				
				Reservation res = em.find( Reservation.class, new Long(1) );
				
		}
		
}// class


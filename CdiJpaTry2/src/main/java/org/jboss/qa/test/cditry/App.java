package org.jboss.qa.test.cditry;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.jboss.qa.test.cditry.ent.Reservation;

/**
 * Hello world!
 *
 */
@Singleton
public class App 
{
		//@Inject JpaTestService serv;
		
    public static void main( String[] args )
    {
				JpaTestService serv = AppWeld.WELD.instance().select(JpaTestService.class).get();
				serv.doSomeJpaStuff();
    }
}

package org.jboss.qa.test.cditry;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.events.ContainerInitialized;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class PlayerServiceBean {

		@Inject
		private EntityManager em;

		@MyTransactional
		public void createPlayer(Player player) {
			em.persist(player);
		}
	
	
		public static void main(String[] args) {		
				// start up the container
				WeldContainer weldContainer = new Weld().initialize();
				// fire an event to cause the entity manager factory initialize itself
				weldContainer.event().select(ContainerInitialized.class).fire(new ContainerInitialized());

				// get your bean
				PlayerServiceBean playerServiceBean = weldContainer.instance().select(PlayerServiceBean.class).get();

				playerServiceBean.createPlayer(new Player("Sean"));
		}

	
}// class
	


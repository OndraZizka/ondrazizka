package org.jboss.qa.test.cditry2;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author Ondrej Zizka
 */
@Entity
public class Player implements Serializable {
		
		@Id
		private String name;
		public String getName() {				return name;		}
		public void setName(String name) {				this.name = name;		}

		public Player() {
		}
		
		public Player(String name) {
				this.name = name;
		}
		
		
		
}// class


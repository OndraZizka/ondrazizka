package org.jboss.qa.test.cditry.ent;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author Ondrej Zizka
 */
@Entity
public class Reservation {
		
		@Id private long id;
		public long getId() {				return id;		}
		public void setId(long id) {				this.id = id;		}
		
		private String forUser;
		public String getForUser() {				return forUser;		}
		public void setForUser(String forUser) {				this.forUser = forUser;		}
		
		
}// class


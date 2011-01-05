
package cz.poh.web.newdesign.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Ondrej Zizka
 */
@Entity
public class CastoHledaneItem implements Serializable 
{
		private static final long serialVersionUID = 1L;


		@Id @GeneratedValue(strategy = GenerationType.AUTO)
		private Long id;


		private String label;
		private int searchCount;

		public CastoHledaneItem() { }


		public CastoHledaneItem(String label, int searchCount) {
				this.label = label;
				this.searchCount = searchCount;
		}
		
		

		public Long getId() {				return id;		}
		public void setId(Long id) {				this.id = id;		}
		public String getLabel() {				return label;		}
		public void setLabel(String label) {				this.label = label;		}
		public int getSearchCount() {				return searchCount;		}
		public void setSearchCount(int searchCount) {				this.searchCount = searchCount;		}




		@Override
		public int hashCode() {
				int hash = 0;
				hash += (id != null ? id.hashCode() : 0);
				return hash;
		}

		@Override
		public boolean equals(Object object) {
				// TODO: Warning - this method won't work in the case the id fields are not set
				if (!(object instanceof CastoHledaneItem)) {
						return false;
				}
				CastoHledaneItem other = (CastoHledaneItem) object;
				if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
						return false;
				}
				return true;
		}

		@Override
		public String toString() {
				return "cz.poh.web.newdesign.model.CastoHledaneItem[id=" + id + "]";
		}

}// class

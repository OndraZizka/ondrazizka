
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
public class Clanek implements Serializable
{
		private static final long serialVersionUID = 1L;


		@Id  @GeneratedValue(strategy = GenerationType.AUTO)
		private Long id;

		private String label;
		private String text;


		public Clanek() { }

		public Clanek(String label, String text) {
				this.label = label;
				this.text = text;
		}




		
		public Long getId() {				return id;		}
		public void setId(Long id) {				this.id = id;		}
		public String getLabel() {				return label;		}
		public void setLabel(String label) {				this.label = label;		}
		public String getText() {				return text;		}
		public void setText(String text) {				this.text = text;		}
		




		@Override
		public int hashCode() {
				int hash = 0;
				hash += (id != null ? id.hashCode() : 0);
				return hash;
		}

		@Override
		public boolean equals(Object object) {
				// TODO: Warning - this method won't work in the case the id fields are not set
				if (!(object instanceof Clanek)) {
						return false;
				}
				Clanek other = (Clanek) object;
				if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
						return false;
				}
				return true;
		}

		@Override
		public String toString() {
				return "Clanek[id=" + id + "]";
		}

}// class

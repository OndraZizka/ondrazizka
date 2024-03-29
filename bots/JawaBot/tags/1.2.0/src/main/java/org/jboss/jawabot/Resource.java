package org.jboss.jawabot;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAttribute;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Ondrej Zizka
 */
@Entity
//@javax.xml.bind.annotation.XmlRootElement
public class Resource implements Serializable {

	@Id
   @XmlAttribute
	private String name;
	public String getName() {		return name;	}

   @XmlAttribute
	private String project;
	public String getProject() {		return project;	}


	public Resource(String name, String project) {
		this.name = name;
		this.project = project;
	}
	public Resource() {	} // for JPA

	public String toString(){
		StringBuilder sb = new StringBuilder(this.name);
		if( ! StringUtils.isBlank( this.project ) ){
			sb.append(" (").append(this.project).append(")");
		}
		return sb.toString();
	}

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final Resource other = (Resource) obj;
      if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
         return false;
      }
      return true;
   }

   @Override
   public int hashCode() {
      int hash = 5;
      hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
      return hash;
   }




}

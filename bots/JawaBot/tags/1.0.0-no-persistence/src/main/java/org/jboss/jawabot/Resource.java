package org.jboss.jawabot;

import javax.persistence.Entity;
import javax.persistence.Id;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Ondrej Zizka
 */
@Entity
public class Resource {


	@Id
	private String name;
	public String getName() {		return name;	}

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

}

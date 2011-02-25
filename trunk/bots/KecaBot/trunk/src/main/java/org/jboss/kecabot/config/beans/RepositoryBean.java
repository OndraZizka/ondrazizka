
package org.jboss.kecabot.config.beans;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Ondrej Zizka
 */
public class RepositoryBean {

   private String name;
   @XmlAttribute public String getName() {      return name;   }
   public void setName(String name) {      this.name = name;   }

   private String url;
   @XmlAttribute
   public String getUrl() {      return url;   }
   public void setUrl(String url) {      this.url = url;   }

   private String type;
   @XmlAttribute
   public String getType() {      return type;   }
   public void setType(String type) {      this.type = type;   }
   

   @XmlElement(name="project") public List<ProjectBean> projects;
   @XmlTransient public List<ProjectBean> getProjects() {      return projects;   }
   public void setProjects(List<ProjectBean> projects) {      this.projects = projects;   }




   @Override
   public String toString() {
      return "Repo{ " + name + " " + url + " T: " + type + " prj: " + (projects == null ? "null" : projects.size()) + '}';
   }



   

}// class


package org.jboss.kecabot.config.beans;

import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author Ondrej Zizka
 */
public class ProjectBean {

   @XmlAttribute public String id;
   @XmlAttribute public String name;


   @Override
   public String toString() {
      return "ProjectBean{ " + "id: " + id + ", name: " + name + " }";
   }

   
}// class

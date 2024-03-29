
package org.jboss.jawabot.state.beans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ondrej Zizka
 */
@XmlRootElement(name="jawabotState")
public class StateBean {

   @XmlElementWrapper(name="reservations")
   @XmlElement(name="reservation")
   public List<ReservationBean> reservations = new ArrayList();

}// class

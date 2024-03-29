
package org.jboss.jawabot.state.beans;

import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.jboss.jawabot.state.DateStringAdapter;

/**
 *
 * @author Ondrej Zizka
 */
public class ReservationBean {

	/** Which user is this reservation for. */
   @XmlAttribute
	public String forUser;

	/** First day of the reservation. */
   @XmlAttribute
   @XmlJavaTypeAdapter( value=DateStringAdapter.class )
  	public Date from;

	/** Last day of the reservation. */
   @XmlAttribute
   @XmlJavaTypeAdapter( value=DateStringAdapter.class )
	public Date to;


   @XmlList
   @XmlElement
   public List<String> resources;



   // Const
   public ReservationBean() {   }
   public ReservationBean(String forUser, Date from, Date to) {
      this.forUser = forUser;
      this.from = from;
      this.to = to;
   }

   public ReservationBean(String forUser, Date from, Date to, List<String> resources) {
      this.forUser = forUser;
      this.from = from;
      this.to = to;
      this.resources = resources;
   }

   

}// class

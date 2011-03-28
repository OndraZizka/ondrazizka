
package org.jboss.jawabot;

import java.io.Serializable;
import java.util.Date;

/**
 * Adds a resource name, note and type to the Reservation:
 *
 *   <pre>public enum Type { NORMAL, COLLISION, MERGED }</pre>
 *
 * <ul>
 * <li>NORMAL - normal reservation.
 * <li>COLLISION - information about colliding existing reservation.
 * <li>MERGED - reservation was merged with existing reservation of the same user.
 * </ul>
 * 
 * @author Ondrej Zizka
 */
public class ReservationWrap /*extends Reservation*/ implements Serializable {

   
   public enum Type { NORMAL, COLLISION, MERGED }

   public final Type type;
   public Type getType() {      return type;   }

   public String resourceName;
   public String getResourceName() {      return resourceName;   }
   public void setResourceName(String resourceName) {      this.resourceName = resourceName;   }

   public String note;
   public String getNote() {      return note;   }
   public void setNote(String note) {      this.note = note;   }


   // Wrapped reservation
   public final Reservation resv;

   

   // -- Const --

   public ReservationWrap(String forUser, Date from, Date to, Type type) {
      this.resv = new Reservation(forUser, from, to);
      this.type = type;
   }
   
   public ReservationWrap(String resourceName, String forUser, Date from, Date to) {
      this(forUser, from, to, Type.NORMAL);
      this.resourceName = resourceName;
   }

   public ReservationWrap(Type type, Reservation resv) {
      this.type = type;
      this.resv = resv;
   }

   public ReservationWrap(Type type, Reservation resv, String resourceName) {
      this.type = type;
      this.resv = resv;
      this.resourceName = resourceName;
   }




   /** toString() */
   public String toString() {
      return this.type.name() +" "+ this.resv.toString();
   }

   /** asString() */
   public String asString() {
      return this.resv.toString() + " " + this.getResourceName();
   }


   // Delegates
   public Date getTo() {      return resv.getTo();   }
   public void setTo(Date to) {      resv.setTo(to);   }
   public Date getFrom() {      return resv.getFrom();   }
   public void setFrom(Date from) {      resv.setFrom(from);   }
   public String getForUser() {      return resv.getForUser();   }
   public void setForUser( String user ) { resv.setForUser(user);   }
   public boolean contains(Date date) {      return resv.contains(date);   }
   public int compareTo(Reservation reservation) {      return resv.compareTo(reservation);   }
   public boolean collidesWith(Reservation other) {      return resv.collidesWith(other);   }








}// class

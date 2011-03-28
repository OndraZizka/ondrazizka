
package org.jboss.jawabot;

import java.util.Date;

/**
 *
 * @author Ondrej Zizka
 */
public class ReservationWrap /*extends Reservation*/ {
   
   public enum Type { NORMAL, COLLISION, MERGED }

   public final Type type;
   public Type getType() {      return type;   }

   public final Reservation resv;


   // Const
   public ReservationWrap(String forUser, Date from, Date to, Type type) {
      this.resv = new Reservation(forUser, from, to);
      this.type = type;
   }

   public ReservationWrap(Type type, Reservation resv) {
      this.type = type;
      this.resv = resv;
   }




   /** toString() */
   public String toString() {
      return this.type.name() +" "+ resv.toString();
   }


   // Delegates
   public Date getTo() {      return resv.getTo();   }
   public Date getFrom() {      return resv.getFrom();   }
   public String getForUser() {      return resv.getForUser();   }
   public boolean contains(Date date) {      return resv.contains(date);   }
   public int compareTo(Reservation reservation) {      return resv.compareTo(reservation);   }
   public boolean collidesWith(Reservation other) {      return resv.collidesWith(other);   }








}// class

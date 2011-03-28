package org.jboss.jawabot;

import java.util.*;
import javax.persistence.Entity;
import org.apache.commons.lang.time.DateUtils;

/**
 *
 * @author Ondrej Zizka
 */
@Entity
class ReservationCalendar {

	private List<Reservation> reservations = new ArrayList();

	/**
	 * @returns a reservation which contains current date-time.
	 */
	Reservation getCurrentReservation() {

		Date now = DateUtils.truncate( new Date(), Calendar.DATE );
		
		for (Reservation reservation : reservations) {
			if( reservation.contains( now )){
				return reservation;
			}
		}
		return null;
	}


	/**
	 *
	 * @return
	 */
	public String printReservations() {
		StringBuilder sb = new StringBuilder();
		for (Reservation res : reservations) {
			sb.append( String.format("%s - %s : %s",
							DateParser.format( res.getFrom() ),
							DateParser.format( res.getTo() ),
							res.getForUser()
			));
			sb.append("\n");
		}
		return sb.toString();
	}



	void purgeOldReservations() {
		Date now = new Date();

		Iterator<Reservation> iterator = reservations.iterator();
		while( iterator.hasNext() ){
			Reservation res = iterator.next();
			if( now.after( res.getTo() ) ){
				// Old, remove.
				iterator.remove();
			}
		}

	}


	/**
	 * 
	 * @param other
	 * @return
	 */
	private Reservation findCollidingReservation( Reservation other )
	{
		for (Reservation reservation : reservations) {
			if( reservation.collidesWith( other )){
				return reservation;
			}
		}
		return null;
	}

	public boolean isFreeForPeriod( Date fromDate, Date toDate ){
		return null == findCollidingReservation( new Reservation(null, fromDate, toDate));
	}


	/**
	 * Creates reservation and returns null, or in case of collision, returns the colliding reservation.
	 * @param forUser
	 * @param from
	 * @param to
	 * @return
	 */
	public Reservation createReservation( String forUser, Date from, Date to ){

		Reservation newRes = new Reservation(forUser, from, to);
		Reservation colliding = findCollidingReservation(newRes);
		if( null != colliding ){
			return colliding;
		}

		this.reservations.add(newRes);
		return null;
		
	}

	/** Removes the given reservation from this calendar. */
	public boolean remove(Reservation curRes) {
		return this.reservations.remove(curRes);
	}


	
	private void cleanOldReservations(){

		Date yesterday = new Date( System.currentTimeMillis() - 24 * 60 * 60 * 1000 );

		for (Reservation reservation : reservations) {
			if( reservation.getTo().before( yesterday ) );
		}
	}

}

package org.jboss.jawabot;

import java.util.*;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

/**
 *
 * @author Ondrej Zizka
 */
public class ResourceManager {

	/** When the old reservations were last purged. */
	private Date lastPurge = new Date();


	/** Map of resources by name. */
	private Map<String, Resource> resources = new HashMap();

	public void addResource( Resource res ){ this.resources.put( res.getName(), res ); }

	public Resource getResource( String name ){ return this.resources.get(name); }

	public void loadResources( ResourcesLoader loader ){

		this.resources.clear();

		List<Resource> loadedResources = loader.getResources();
		for (Resource resource : loadedResources) {
			this.addResource(resource);
		}
		
	}


	/** Map of reservation calendars by resource. */
	private Map<Resource, ReservationCalendar> reservationCalendars = new HashMap();




	/**
	 * Book the resource for given user from... etc.
	 * @param resName
	 * @param userNick
	 * @param fromDate    - if null, replaced with today.
	 * @param toDate    - if null, replaced with today.
	 * @return
	 * @throws org.jboss.jawabot.JawaBotException
	 */
	public Reservation bookResource( String resName, String userNick, Date fromDate, Date toDate ) throws JawaBotException{

		Date today = DateUtils.truncate( new Date(), Calendar.DATE );
		if( null == fromDate )
			fromDate = today;
		if( null == toDate )
			toDate = today;

		// TODO: Some date parser class, with support for "today", "friday", "+14day" etc.



		// If fromDate is later than toDate,
		if( toDate.before(fromDate) ){
			//throw new JawaBotException("...");
			// Swap the dates.
			Date tmp = toDate;
			toDate = fromDate;
			fromDate = tmp;
		}


		/*Resource res = this.getResource(resName);
		if( null == res )
			throw new JawaBotException("Unknown resource: "+resName);/**/
		
		
		ReservationCalendar resCalendar = this.getCalendarForResource(resName);

		Reservation collidingReservation = resCalendar.createReservation(userNick, fromDate, toDate);

		if( null != collidingReservation ){
			return collidingReservation;
		}

		return null;

	}


	/**
	 * List resources in format <resName1>, ...
	 * @return
	 */
	public String listResources(){
		Set<String> keySet = this.resources.keySet();
		return StringUtils.join(keySet, ", ");
	}

	/**
	 * List resources in format <resName1>, ...
	 * @return
	 */
	public String printResourceReservations( String resName ) throws JawaBotException
	{
		ReservationCalendar calendar = this.getCalendarForResource(resName);
		return "Reservations for "+resName+":\n" + calendar.printReservations();
	}


	/**
	 * Finds resources which have no reservations during the given period.
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	List<Resource> findFreeResources(Date fromDate, Date toDate) {
		List<Resource> freeResources = new ArrayList();
		for( Resource res : this.resources.values() ){
			if( this.getCalendarForResource(res).isFreeForPeriod( fromDate, toDate ) ){
				freeResources.add(res);
			}
		}
		return freeResources;
	}


	/**
	 * Leave given resource, if in possesion of the given user.
	 * TODO: Better design.
	 * @param resName
	 * @param fromUser
	 */
	boolean leave(String resName, String fromUser) throws JawaBotException
	{
		ReservationCalendar calendar = this.getCalendarForResource(resName);
		Reservation curRes = calendar.getCurrentReservation();
		if( null == curRes ){
			throw new JawaBotException(resName+" was not reserved by anyone.");
			//return false;
		}

		if( ! StringUtils.equals( curRes.getForUser(), fromUser ) ){
			throw new JawaBotException(resName+" was reserved by "+curRes.getForUser()+", not "+fromUser+".");
		}

		calendar.remove( curRes ); // Should return true - we found it above.

		return true;
	}



	/**
	 * 
	 */
	void purgeOldReservations() {
		// Last purge must have happened in the previous day (well, yesterday).
		if( ! DateUtils.truncate(new Date(), Calendar.DATE).after( DateUtils.truncate(lastPurge, Calendar.DATE) ) ){
			return;
		}

		for( ReservationCalendar calendar : this.reservationCalendars.values() ){
			calendar.purgeOldReservations();
		}
	}




	/**
	 * Finds or creates a calendar for given resource.
	 * @param resName
	 * @return
	 * @throws org.jboss.jawabot.JawaBotException
	 */
	private ReservationCalendar getCalendarForResource(String resName) throws JawaBotException {

		Resource res = this.getResourceByName(resName);
		
		return getCalendarForResource(res);
	}


	private ReservationCalendar getCalendarForResource( Resource res ) {

		ReservationCalendar calendar = reservationCalendars.get( res );
		
		// Does not exist yet -> create.
		if( null == calendar ){
			calendar = new ReservationCalendar();
			reservationCalendars.put( res, calendar );
		}
		return calendar;
	}



	/**
	 * Return resource by name or throw an exception.
	 * @param resName
	 * @return
	 * @throws org.jboss.jawabot.JawaBotException
	 */
	Resource getResourceByName( String resName ) throws JawaBotException {
		Resource res = this.getResource(resName);
		if( null == res )
			throw new JawaBotException("Unknown resource: "+resName);
		return res;
	}


}// class









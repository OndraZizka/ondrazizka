package org.jboss.jawabot;

import cz.dynawest.util.DateUtils;
import java.util.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;









/** Wrapper Map */
//@XmlRootElement(name = "MyMap")





/**
 *
 * @author Ondrej Zizka
 */
@XmlRootElement
public class ResourceManager
{
   private static final Logger log = Logger.getLogger( JawaBot.class );


	/** When the old reservations were last purged. */
	private Date lastPurge = new Date();

	/** Map of resources - name -> resource pairs. */
	private Map<String, Resource> resources = new HashMap();

	/** Map of reservation calendars by resource. */
	private Map<Resource, ReservationCalendar> reservationCalendars = new HashMap();



   /** Returns a list of all resources. */
   //@XmlElement(name="resource")
   private Collection<Resource> getResourcesList() {
      List<Resource> resourcesList = Arrays.asList(resources.values().toArray(new Resource[0]));
      Collections.sort( resourcesList, new Comparator<Resource>() {
         @Override
         public int compare(Resource a, Resource b) {
            return a.getName().compareToIgnoreCase( b.getName() );
         }
      } );
      return resourcesList;
   }


   /** Adds given resource to the lookup map of resources (String -> Resource). */
	public void addResource( Resource res ){ this.resources.put( res.getName(), res ); }

   /** Looks up a resource of given name from the map. */
	public Resource getResource( String name ){ return this.resources.get(name); }

   /** Configuration is currently loaded using JAXB. */
   @Deprecated
	public void loadResources( ResourcesLoader loader ){
		this.resources.clear();

		List<Resource> loadedResources = loader.getResources();
		for( Resource resource : loadedResources ) {
			this.addResource(resource);
		}
	}


//   /** Returns a list of all reservation calendars. */
//   @XmlElement(name="reservation", required=true)
//   @XmlElementWrapper(name="reservations")
//   public List<ReservationCalendar> getReservationCalendarsAsList(){
//      return Arrays.asList( this.reservationCalendars.values().toArray(new ReservationCalendar[0]) );
//   }
//
//   /** Returns a list of all reservation calendars. */
//   @XmlElement(name="reservationB", required=true)
//   @XmlElementWrapper(name="reservationsB")
//   public List<Reservation> getReservationsAsList(){
//      List<Reservation> res = new ArrayList<Reservation>();
//      Collection<ReservationCalendar> cals = this.reservationCalendars.values();
//      for (ReservationCalendar cal : cals) {
//         res.addAll( cal.getReservations() );
//      }
//      return res;
//   }

   /** Returns a list of all reservation calendars. */
   @XmlElement(name="reservations")
   @XmlJavaTypeAdapter( value = ReservCalendarMapAdaptor.class )
   Map<Resource, ReservationCalendar> getReservationCalendars(){
      return this.reservationCalendars;
   }
   void setReservationCalendars(Map<Resource, ReservationCalendar> cals){
      //System.out.println("AAA"+cals.keySet());
      this.reservationCalendars = cals;
   }






	/**
	 * Book the resource for given user from... etc.
	 * @param resName
	 * @param userNick
	 * @param fromDate    - if null, replaced with today.
	 * @param toDate    - if null, replaced with today.
	 * @return
	 * @throws org.jboss.jawabot.JawaBotException
	 */
	public ReservationWrap bookResource( String resName, String userNick, Date fromDate, Date toDate ) throws UnknownResourceException {

      {
         Date today = DateUtils.truncate( new Date(), Calendar.DATE );
         if( null == fromDate )
            fromDate = today;
         if( null == toDate )
            toDate = today;
      }

		// TODO: Some date parser class, with support for "today", "friday", "+14day" etc.



		// If fromDate is later than toDate,
		if( toDate.before(fromDate) ){
			//throw new JawaBotException("...");
			// Swap the dates.
			Date tmp = toDate;
			toDate = fromDate;
			fromDate = tmp;
		}

      return doBookResource(resName, userNick, fromDate, toDate);
   }



   /**  Perform the actual booking - now, having the dates checked. */
   private ReservationWrap doBookResource( String resName, String userNick, Date fromDate, Date toDate ) throws UnknownResourceException
   {

		/*Resource res = this.getResource(resName);
		if( null == res )
			throw new JawaBotException("Unknown resource: "+resName);/**/

      // TODO: bookResources() for more resources.
      //       Quite easy - just a two-pass process - 1) Check,  2)  Reserve.
		
		ReservationCalendar resCalendar = this.getCalendarForResource(resName);

		ReservationWrap resultingReservation = null;

      // Try to resolve colision with self 5-times.
      for( int i = 0; i < 5; i++ ) {
         log.debug("Creating reservation: "+new Reservation(userNick, fromDate, toDate));///

         resultingReservation = resCalendar.createReservationWrap(userNick, fromDate, toDate);

         // Reservation created - 
         if( resultingReservation.getType() == ReservationWrap.Type.NORMAL )
            // NORMAL for first pass, MERGED for successive.
            return new ReservationWrap(
                    i == 0 ? ReservationWrap.Type.NORMAL : ReservationWrap.Type.MERGED,
                    resultingReservation.resv
            );

         log.debug("Collision with: "+resultingReservation);///

         // Other user - COLLISION.
         if( ! userNick.equals( resultingReservation.getForUser() ) )
            return resultingReservation;

         // Handle collision with own resv.
         resCalendar.remove(resultingReservation.resv);
         fromDate = DateUtils.getEarlier(fromDate, resultingReservation.getFrom());
         toDate = DateUtils.getLater(toDate, resultingReservation.getTo());
         // ... and create new merged reservation in next turn.
      }

      // If 5-times isn't enough, return even self-colision.
		return resultingReservation;

	}





	/**
	 * List resources in format <resName1>, ...
	 * @return
	 */
	public String listResourcesAsString(){
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
      if( calendar.getReservations().size() == 0 )
         return "No reservations for "+resName+".";
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
	 * Leave given resource, if in possession of the given user.
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
    * Leave all resources  in possession of given user.
    * @param fromUserNorm
    * TODO: Test
    */
   List<Resource> leaveAll(String fromUserNorm) {
      List<Resource> leftResources = new ArrayList();

      for( Map.Entry<Resource, ReservationCalendar> ent : this.reservationCalendars.entrySet() ){
         ReservationCalendar cal = ent.getValue();
         Reservation resv = cal.getCurrentReservation();
         if( null == resv )
            continue;
         if( StringUtils.equals(fromUserNorm, resv.getForUser() ) ){
            cal.remove( resv );
            leftResources.add( ent.getKey() );
         }
      }

      return leftResources;
   }




	/**
	 *  Deletes all reservations which ended yesterday.
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
	private ReservationCalendar getCalendarForResource(String resName) throws UnknownResourceException {

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
	Resource getResourceByName( String resName ) throws UnknownResourceException {
		Resource res = this.getResource(resName);
		if( null == res )
			throw new UnknownResourceException("Unknown resource: "+resName);
		return res;
	}


   /** Deletes all reservations (but leaves the resources). */
   void clearReservations() {
      this.reservationCalendars.clear();
   }

}






//   --------  Dump ground  --------   //


class MapWrapper {
   @XmlElement(name = "resource")
   //@XmlList
   public List<MapWrapperItem> getItems() { return this.items; }
   private final List<MapWrapperItem> items = new ArrayList();
   public void clear(){
      this.items.clear();
   }
}




/** Wrapper Map Item */
class MapWrapperItem {
   @XmlAttribute(name="name") public String resource;
   @XmlElement(name="reservations")
   public ReservationCalendar reservations;
   //@XmlList
   public List<Reservation> getReservationsAsList(){ return reservations.getReservations(); }
   

   public MapWrapperItem() { }

   public MapWrapperItem(Resource key, ReservationCalendar value) {
      this.resource = key.getName();
      this.reservations = value;
   }
}



/** Adaptor */
class  ReservCalendarMapAdaptor extends XmlAdapter<MapWrapper, Map<Resource, ReservationCalendar>> {

   @Override
   public MapWrapper marshal(Map<Resource, ReservationCalendar> valueForXml) throws Exception {
      MapWrapper myWrapper = new MapWrapper();
      List<MapWrapperItem> aList = myWrapper.getItems();
      for (Map.Entry<Resource, ReservationCalendar> e : valueForXml.entrySet()) {
         aList.add(new MapWrapperItem(e.getKey(), e.getValue()));
      }
      return myWrapper;
   }

   @Override
   public Map unmarshal(MapWrapper val) throws Exception {

      Map map = new HashMap();
      for (MapWrapperItem item : val.getItems() ) {
         //TODO: Resource res = JawaBotApp.getInstance().getBot().getResourceManager().getResourceByName( item.key ) );
         //Resource res = Main.getBot().getResourceManager().getResourceByName( item.resource );
         Resource res = new Resource(item.resource, "");
         map.put( res, item.reservations );
      }
      val.clear();
      return map;
   }

}

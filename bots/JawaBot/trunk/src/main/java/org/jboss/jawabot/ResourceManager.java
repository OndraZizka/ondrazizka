package org.jboss.jawabot;

import java.util.Map.Entry;
import org.jboss.jawabot.ReservationWrap.Type;
import org.jboss.jawabot.ex.UnknownResourceException;
import org.jboss.jawabot.ex.JawaBotException;
import cz.dynawest.util.DateUtils;
import cz.dynawest.util.sync.ReadersAndModifierSynchronizerReentrant;
import cz.dynawest.util.sync.ReadersAndModifiersSynchronizer;
import java.util.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;




/**
 * Central storage of resources and reservations.
 *
 * No need to synchronize yet - all done from a single PIRC thread.
 *
 * TODO: Clean up the JAXB annotations - pecial classes were created for JAXB serialization.
 * 
 * @author Ondrej Zizka
 */
@XmlRootElement
public class ResourceManager
{
   private static final Logger log = Logger.getLogger( ResourceManager.class );

   private ReadersAndModifiersSynchronizer rwlock = new ReadersAndModifierSynchronizerReentrant();



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

   /**  Looks up a resource of given name from the map.  @returns null if not found.  */
	public Resource findResourceByName( String name ){ return this.resources.get(name); }

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
    * TODO: Book multiple resources.
    *       Quite easy - just a two-pass process - 1) Check,  2)  Reserve.
    *
    * @param resNamesStr A coma-separated names of resources.
    */
   public ReservationsBookingResult bookResources( final String resNamesStr, String userNick, Date fromDate, Date toDate ) throws UnknownResourceException
   {
      Date today = DateUtils.truncate( new Date(), Calendar.DATE );
      if( null == fromDate )
         fromDate = today;
      if( null == toDate )
         toDate = today;

		// If fromDate is later than toDate,
		if( toDate.before(fromDate) ){
			//throw new JawaBotException("...");
			// Swap the dates.
			Date tmp = toDate;
			toDate = fromDate;
			fromDate = tmp;
		}


      String[] resNames = StringUtils.split(resNamesStr, ',');

      /*if( resNames.length > 1)
         // TODO
         throw new UnsupportedOperationException("Booking multiple resources at once is not yet implemented. JBQA-2877");
      return bookResource( resNames[0], userNick, fromDate, toDate );
      */
      return doBookResources( resNames, userNick, fromDate, toDate );
   }


	/**
	 * Book the resource for given user from... etc.
	 * @param resName   Resource name.
	 * @param fromDate  If null, replaced with today.
	 * @param toDate    If null, replaced with today.
	 * @return
	 * @throws org.jboss.jawabot.JawaBotException
    * @deprecated  in favor of bookResources()
	 */
	ReservationWrap bookResource( String resName, String userNick, Date fromDate, Date toDate ) throws UnknownResourceException {

      return doBookResource(resName, userNick, fromDate, toDate);
   }

   


   
   /**
    *  Performs the actual booking of multiple resources.
    *
    *  Based on the type of the result:
    *    COLLISION: All are of type COLLISION.
    *    NORMAL:    All are of type NORMAL.
    *    MERGED:    A mix of type NORMAL and MERGED.
    */
   private ReservationsBookingResult doBookResources( String[] resNames, String forUser, Date fromDate, Date toDate ) throws UnknownResourceException
   {

      Reservation resClaim = new Reservation(forUser, fromDate, toDate);

      List<ReservationWrap> resultingResvs = new ArrayList( resNames.length );

      // Check if all the resources are free for the given period.
      for( String resName : resNames ){
         log.debug("Checking "+resName+"...");
         ReservationCalendar resCalendar = this.getCalendarForResource(resName);
         Reservation collidingRes = resCalendar.findCollidingReservation(resClaim, true);
         if( null != collidingRes ){
            //return new ReservationWrap(ReservationWrap.Type.COLLISION, collidingRes);
            //return new ReservationsBookingResult( resClaim, resultingResvs );
            //return new ReservationsBookingResult( ReservationWrap.Type.COLLISION, collidingRes );
            resultingResvs.add( new ReservationWrap( Type.COLLISION, collidingRes, resName ) );
         }
      }
      // If there are some collisions, return a list of these.
      // TODO: "take force" - would reserve those without collisions, not caring about the others.
      if( resultingResvs.size() != 0 )
         return new ReservationsBookingResult( resNames, resClaim, ReservationWrap.Type.COLLISION, resultingResvs );


      // Now actually book ther esources.
      boolean anyMerged = false;
      for( String resName : resNames ){
         log.debug("Booking "+resName+"... anyMarged: "+anyMerged);
         // TODO: Doing unnecessary checks. Extract the booking from doBookResource().
         //this.doBookResource(resName, userNick, fromDate, toDate);

         ReservationCalendar resCalendar = this.getCalendarForResource(resName);
         ReservationWrap retRes = resCalendar.bookReservationWrapMergeOwner(forUser, fromDate, toDate);
         retRes.setResourceName(resName);
         if( retRes.type == ReservationWrap.Type.COLLISION ){
            log.error("We shouldn't get collisions - those were already checked. A bug? A concurrency error?");
         }
         if( retRes.type == Type.MERGED){
            log.info("MERGED.");
            anyMerged |= true;
         }
         resultingResvs.add( retRes );
      }

      //return new ReservationWrap(ReservationWrap.Type.NORMAL, resClaim);
      return new ReservationsBookingResult( resNames, resClaim, anyMerged ? Type.MERGED : Type.NORMAL, resultingResvs );

	}



   /**
    * @param user  Which user's reservations to return.
    * @returns  a list of reservations
    */
   List<ReservationWrap> getReservationsForUser(String user)
   {
      if( user == null )
         return Collections.EMPTY_LIST;

      List<ReservationWrap> usersReserv = new LinkedList();

      // For each resource, get all reservations for the given user.
      Set<Entry<Resource, ReservationCalendar>> entrySet = this.getReservationCalendars().entrySet();
      for( Entry<Resource, ReservationCalendar> entry : entrySet ) {
         ReservationCalendar calendar = entry.getValue();
         List<Reservation> reservations = calendar.getReservations();
         for( Reservation reservation : reservations ) {
            if( user.equals( reservation.getForUser() ) )
               usersReserv.add( new ReservationWrap( Type.NORMAL, reservation, entry.getKey().getName() ) );
         }
      }

      return usersReserv;
   }




   


   /**
    *  Envelope for a result of multiple resources reservation:
    *  ReservationWrap.Type + a list of `ReservationWrap`s.
    *    COLLISION: All should be of type COLLISION.
    *    NORMAL:    All should be of type NORMAL.
    *    MERGED:    A mix of type NORMAL and MERGED.
    */
   public class ReservationsBookingResult {
      public final Reservation claimedResv;
      public final String[] resourceNames;
      public final ReservationWrap.Type type;
      public final List<ReservationWrap> resultingReservations;

      // Const

      /** Create from a list of reservations. */
      public ReservationsBookingResult( String[] resNames, Reservation claimedRes, Type type, List<ReservationWrap> resultingReservations) {
         this.claimedResv = claimedRes;
         this.resourceNames = resNames;
         this.type = type;
         this.resultingReservations = resultingReservations;
      }

      /** Create from a single reservation. */
      public ReservationsBookingResult( String[] resNames, Reservation claimedRes, Type type, Reservation resv) {
         this.claimedResv = claimedRes;
         this.resourceNames = resNames;
         this.type = type;
         ReservationWrap resvWrap = new ReservationWrap(type, resv);
         this.resultingReservations = Arrays.asList( new ReservationWrap[]{resvWrap} );
      }

      @Deprecated public String formatMailSubject(){
         return "";
      }
   }




   /**
    * Perform the actual booking - now, having the dates checked.
    * @deprecated  Use ReservationCalendar::bookReservationWrapMergeOwner().
    */
   private ReservationWrap doBookResource( String resName, String userNick, Date fromDate, Date toDate ) throws UnknownResourceException
   {

      ReservationCalendar resCalendar = this.getCalendarForResource(resName);

      ReservationWrap resultingReservation = null;

      // Try to resolve colision with self 5-times.
      for( int i = 0; i < 5; i++ ) {
         log.debug("Creating reservation: "+new Reservation(userNick, fromDate, toDate));///

         // BOOK IT!
         resultingReservation = resCalendar.bookReservationWrap(userNick, fromDate, toDate);

         // Reservation created -
         if( resultingReservation.getType() == ReservationWrap.Type.NORMAL ){
            // NORMAL for first pass, MERGED for successive.
            return new ReservationWrap(
                    i == 0 ? ReservationWrap.Type.NORMAL : ReservationWrap.Type.MERGED,
                    resultingReservation.resv
            );
         }

         log.debug("Collision with: "+resultingReservation);///

         // Other user - COLLISION.
         if( ! userNick.equals( resultingReservation.getForUser() ) )
            return resultingReservation;

         // Handle collision with own resv.
         if( i < 4 ){ // Don't remove the last merged (should it ever happen).
            resCalendar.remove(resultingReservation.resv);
            fromDate = DateUtils.getEarlier(fromDate, resultingReservation.getFrom());
            toDate = DateUtils.getLater(toDate, resultingReservation.getTo());
         }
         // ... and create new merged reservation in next turn.
      }

      // If 5-times isn't enough, return even self-colision.
      return resultingReservation;

	}








   

	/**
	 * List resources in format <resName1>, ...
	 */
	public String listResourcesAsString(){
		Set<String> keySet = this.resources.keySet();
      SortedSet<String> sorted = new TreeSet<String>(keySet);
		return StringUtils.join(sorted, ", ");
	}

	/**
	 * List resource reservations, one per line.
	 */
	public String printResourceReservations( String resName ) throws JawaBotException
	{
		ReservationCalendar calendar = this.getCalendarForResource(resName);
      return this.printReservationsOfCalendar(calendar, resName);
	}

	/**
	 * List resource reservations, one per line.
	 */
	public String printResourceReservations( Resource res ) throws JawaBotException
	{
      ReservationCalendar calendar = this.getCalendarForResource(res);
      return this.printReservationsOfCalendar(calendar, res.getName());
	}

	/**
	 * List resource reservations, one per line.
	 */
	protected static String printReservationsOfCalendar( ReservationCalendar calendar, String resName ) throws JawaBotException
	{
      if( calendar.getReservations().size() == 0 )
         return "No reservations for "+resName+".";
      else
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
		Resource res = this.findResourceByName(resName);
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

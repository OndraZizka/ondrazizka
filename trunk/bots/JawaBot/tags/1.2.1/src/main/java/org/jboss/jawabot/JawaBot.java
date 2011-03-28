package org.jboss.jawabot;

import java.text.ParseException;
import java.util.*;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.jboss.jawabot.config.beans.ConfigBean;
import org.jboss.jawabot.state.beans.StateBean;
import org.jibble.pircbot.PircBot;
import org.apache.log4j.Logger;
import org.jboss.jawabot.config.JaxbConfigPersister;
import org.jboss.jawabot.config.beans.ServerBean;
import org.jboss.jawabot.state.JaxbStatePersister;
import org.jboss.jawabot.state.beans.ReservationBean;
import org.jibble.pircbot.NickAlreadyInUseException;



@XmlRootElement
public class JawaBot extends PircBot
{
   private static final Logger log = Logger.getLogger( JawaBot.class );

   
	//final String BOT_NICK = "JawaBot";
	final String USUAL_NICK = "jawabot";

	//final String SMTP_HOSTNAME = "smtp.corp.redhat.com";
	//final String ANNOUNCE_EMAIL_TO = "jboss-qa-brno@redhat.com";
	//final String ANNOUNCE_EMAIL_FROM = "jawabot-no-reply@redhat.com";

	// Default channel to announce reservations if sent from PM.
	//final String ANNOUNCE_DEFAULT_CHANNEL = "#jbosssoaqa";


	
	// Things shown in the "help" command reply.
	private static final String VERSION = "1.2.1";
	private static final String PROJECT_DOC_URL = "http://wiki.brq.redhat.com/ozizka/JawaBot";


   private ConfigBean config;
   public ConfigBean getConfig() {      return config;   }


   private boolean initialized = false;
   public boolean isInitialized() {      return initialized;   }


   //@XmlElement(name="resourceManager")
	ResourceManager resourceManager = new ResourceManager();
   ResourceManager getResourceManager() {      return resourceManager;   }



   /** [JAXB] Returns a list of all reservation calendars. */
   // Moved to here to omit one XML element level.
   @XmlElement
   @XmlJavaTypeAdapter( value = ReservCalendarMapAdaptor.class )
   Map<Resource, ReservationCalendar> getReservationCalendars(){
      return this.resourceManager.getReservationCalendars();
   }
   void setReservationCalendars( Map<Resource, ReservationCalendar> cals ){
      this.resourceManager.setReservationCalendars( cals );
   }




	private int quitPassword = new Random().nextInt(1000);


   /** Creates the bot, loads the configuration, initializes and returns the bot. */
   public static JawaBot create() throws JawaBotException{
      JaxbConfigPersister re = new JaxbConfigPersister();
      JawaBot bot = re.load();
      bot.init();
      return bot;
   }
	

	/** Const */
	public JawaBot() {
      //this.setName( BOT_NICK );

      // Log the quit password.
      log.info("");
      log.info("                              ***  QUIT PASSWORD: "+this.quitPassword+"  ***");
      log.info("");
	}



	/** Init - load resources reservations. */
	public void init() throws JawaBotIOException, UnknownResourceException {
      log.info("Initializing...");
      if( this.initialized )
         log.warn("Already initialized.");

		//this.resourceManager.loadResources( new StaticResourcesLoader() ); // Moved to config persister.

      StateBean state = new JaxbStatePersister().load();
      this.applyState( state );
      
      this.initialized = true;
	}


   /**
    * Connects the servers, joins the channels... 
    */
   public void connectAndJoin() throws JawaBotException {
      log.info("Connecting...");
      if( this.isConnected() )
         log.error("Already connected.");

      ConfigBean cnf = this.getConfig();

      /* PircBot only supports one server connection.
      // Connect to all servers.
      List<Exception> exs = new ArrayList();
      for( ServerBean server : cnf.irc.servers ){
         try{
            // Connect to the server
            this.connect( server.host ); // Red Hat

            // Join the default channels
            for( String channel : server.autoJoinChannels ){
               this.joinChannel(channel); // debugging
            }
         }
         catch( Exception ex ){
            String msg = "Exception when connecting to the server "+server.host+": "+ex;
            log.error( msg );
            exs.add( new JawaBotException(msg, ex));
         }
      }

      // All connections failed.
      if( cnf.irc.servers.size() == exs.size() )
         throw new JawaBotException("Connecting to all servers failed, see previous messages.");
     */

      if( cnf.irc.servers.size() == 0 )
         throw new JawaBotException("No IRC servers configured.");

      ServerBean server = cnf.irc.servers.get(0);

      String nickToTry = cnf.irc.defaultNick;

      // Connect to the server
      nickTry: try{
         for( int i = 1; i <= 5; i++ ){  // 5 - max Nick tries.
            log.info("Trying nick '"+nickToTry+"'...");
            try {
               this.setName( nickToTry  );
               this.connect( server.host );
               break nickTry;
            }catch (NickAlreadyInUseException ex){
               log.warn("Nick already in use.");
               nickToTry = cnf.irc.defaultNick + "-" + i;
            }
         }
      }
      catch( Exception ex ){
         String msg = "Exception when connecting to the server "+server.host+": "+ex;
         throw new JawaBotException(msg, ex);
      }


      log.info("Joining channels...");

      // Join the default channels
      for( String channel : server.autoJoinChannels ){
         log.info(" * joining '"+channel+"'");
         this.joinChannel(channel);
      }
      this.joinChannel("#some");

      log.info("Connecting done.");
   }






	/**
	 * onMessage
	 */
	@Override
	public void onMessage(String channel, String sender, String login, String hostname, String message)
   {
      if( ! this.isInitialized() ){
         log.warn("Called onMessage(), but not initialized yet.");
         return;
      }


      // Either process a command or search for Jira IDs.
      boolean wasCommand = false;

      String msgNormalized = message.trim().toLowerCase();

      // Check for presence of bot nick prolog.
      boolean startsWithUsualNick = msgNormalized.startsWith( USUAL_NICK.toLowerCase() );
      boolean startsWithBotNick = msgNormalized.startsWith( this.getNick().toLowerCase() );

      // If the prolog is present,
      if( startsWithUsualNick || startsWithBotNick ){

         // remove it,
         int prologEnd;
         if( startsWithUsualNick && startsWithBotNick ){
            prologEnd = Math.max( USUAL_NICK.length(), this.getNick().length() );
         }
         else {
            prologEnd = startsWithUsualNick ? USUAL_NICK.length() : this.getNick().length();
         }

         // Get the rest of the message sans eventual starting colon.
         String command = msgNormalized.substring( prologEnd );
         command = StringUtils.removeStart(command, ":").trim();
         command = StringUtils.removeStart(command, ",").trim();

         // and process the command.
         wasCommand = handleJawaBotCommand(channel, sender, command);

      }


      // Not a command?
      if( !wasCommand ){
      }

   }



   /** Private IRC message - no channel, only from user. */
	@Override
	protected void onPrivateMessage(String sender, String login, String hostname, String message) {
      handleJawaBotCommand(null, sender, message.trim());
	}

   



	/**
	 * Parses one or two dates from the given string in the form "YYYY-MM-DD[ YYYY-MM-DD]".
	 * If the second is earlier than the first, they are swapped.
	 *
	 * @param paramsStr   string in the form "YYYY-MM-DD[ YYYY-MM-DD]"
	 * @returns A reservation object with dates filled with the dates from the string.
	 * @throws java.text.ParseException
	 */
	private Reservation parseDates( String paramsStr ) throws ParseException{

         String parts[] = paramsStr.split(" ");

         String resName = parts[0];
         String dateFromStr = parts.length > 1 ? parts[1] : null;
         String dateToStr = parts.length > 2 ? parts[2] : null;


         // Parse the dates. TODO: Date parser.
         Date fromDate;
         try {
            fromDate = DateParser.parse(dateFromStr);
         } catch (ParseException ex) {
            throw new ParseException( "Invalid date: "+dateFromStr+"' Try YYYY-MM-DD.", 1);
         }

         Date toDate;
         try {
            toDate = DateParser.parse(dateToStr);
         } catch (ParseException ex) {
            throw new ParseException( "Invalid date: "+dateFromStr+"' Try YYYY-MM-DD.", 1);
         }

         // If fromDate is later than toDate, swap the dates.
         if( toDate.before(fromDate) ){
            Date tmp = toDate;
            toDate = fromDate;
            fromDate = tmp;
         }

         return new Reservation(null, fromDate, toDate);

	}


	/**
	 * Handles a command, which is (assumably) sent as PM to the bot.
	 *
	 * @param fromChannel  Channel (#...) from which the message was received. Can be null for private messages.
	 * @param fromUser     Nick from which the message was received.
	 * @param command  The command - the relevant part - i.e. ignoring
	 *                 "jawabot" etc at the beginning of the messagge.
	 *
	 * @returns  true if the request was valid JiraBot command.
	 */
	boolean handleJawaBotCommand( String fromChannel, String fromUser, String command )
   {
      log.debug( String.format("%s %s %s", fromChannel, fromUser, command) );

		boolean wasValidCommand = false;

		// Is private message?
		boolean isFromPrivateMessage = null == fromChannel;

		// Reply either to PM or to a channel.
		String replyTo = isFromPrivateMessage ? fromUser : fromChannel;
      //System.out.println("isFromPrivateMessage: "+isFromPrivateMessage);///

      // Use only first part consisting of lowercase latin alphabetic chars as user name.
      // This normalizes user names like ozizka-dinner, ozizka_wfh or ozizka1.
      int nonAlnum = StringUtils.indexOfAnyBut(fromUser, "abcdefghijklmnopqrstuvwxyz");
      if( nonAlnum < 1 ) nonAlnum = fromUser.length();
		final String fromUserNorm = fromUser.substring(0, nonAlnum);

		command = command.toLowerCase();

		final String DATE_FORMAT = "yyyy-MM-dd";


		// Take / Keep
		if( !isFromPrivateMessage && ( command.startsWith("take") || command.startsWith("keep") ) ) {
			wasValidCommand = true;
			do {
				command = command.substring(4).trim();
            if( "".equals( command ) ){
               sendMessage(replyTo, "Take what? Try 'list' for a list of resources.");
               break;
            }

				
				String parts[] = command.split(" ");
				String resName = parts[0];
				/*
				String dateFromStr = parts.length > 1 ? parts[1] : null;
				String dateToStr = parts.length > 2 ? parts[2] : null;
				// Parse the dates. TODO: Date parser.
				Date fromDate;
				try {
					fromDate = DateParser.parse(dateFromStr);
				} catch (ParseException ex) {
					sendMessage(replyTo, "Invalid date: "+dateFromStr+"' Try YYYY-MM-DD.");
					break;
				}
				Date toDate;
				try {
					toDate = DateParser.parse(dateToStr);
				} catch (ParseException ex) {
					sendMessage(replyTo, "Invalid date: '"+dateToStr+"' Try YYYY-MM-DD.");
					break;
				}
				// If fromDate is later than toDate, swap the dates.
				if( toDate.before(fromDate) ){
					Date tmp = toDate;
					toDate = fromDate;
					fromDate = tmp;
				}
				 */
				Reservation datesHolder;
				try{
					command = command.substring( resName.length() );
					datesHolder = parseDates(command);
				}catch(ParseException ex){
					sendMessage(replyTo, ex.getMessage()); break;
				}
				Date fromDate = datesHolder.getFrom();
				Date toDate = datesHolder.getTo();


				// Make a reservation.
				try {
               boolean keep = false;

					ReservationWrap resultingReservation = resourceManager.bookResource(resName, fromUserNorm, fromDate, toDate);

					// Collision with another reservation.
					if( resultingReservation.type == ReservationWrap.Type.COLLISION ){
						sendMessage(replyTo, String.format("Reservation collision!  %s is reserved for %s from %s to %s.",
										resName,
										resultingReservation.getForUser(),
										DateParser.format( resultingReservation.getFrom() ),
										DateParser.format( resultingReservation.getTo() )
						) );
                  break; // Konecna.
					}

               // Collision with own reservation.
               if( resultingReservation.type == ReservationWrap.Type.MERGED ){
                  keep = true;
               }

					// No collision or merged with older, we're free to go.
					{

						// Send the IRC reply.
						final String RESERVATION_OK_FORMAT = "%s was succesfully %s for %s from %s to %s.";
						String announceTo = StringUtils.defaultString(fromChannel, this.getConfig().settings.announceDefaultChannel);
						sendMessage(announceTo,
										String.format(RESERVATION_OK_FORMAT,
										resName,
                              keep ? "kept" : "reserved",
										fromUserNorm,
										DateParser.format( resultingReservation.getFrom() ),
										DateParser.format( resultingReservation.getTo() )
						) );

                  // Send the mail announcement, take care of errors.
                  announceTakeOnMailingList( resourceManager.getResource(resName), resultingReservation );

					}
					break;
				}
				catch (JawaBotException ex) {
					sendMessage(replyTo, ""+ex.getMessage());
               log.error( "Exception during 'take': "+ex, ex );
					break;
				}
				
			} while( false );
		}


		// Find
		else if( command.startsWith("find") || command.startsWith("free") ) {
			wasValidCommand = true;

			command = command.substring(4).trim();
			do{
				Reservation datesHolder;
				try{
					datesHolder = parseDates(command);
				}catch(ParseException ex){
					sendMessage(replyTo, ex.getMessage()); break;
				}
				Date fromDate = datesHolder.getFrom();
				Date toDate = datesHolder.getTo();

				List<Resource> freeResources = this.resourceManager.findFreeResources( fromDate, toDate );

				String message = String.format("Free for %s to %s: %s",
														DateParser.format(fromDate),
														DateParser.format(toDate),
														StringUtils.join(freeResources, ", ") );
				sendMessage(replyTo, message);

			}while( false );
		}



		// Leave (resource)
		else if( !isFromPrivateMessage && command.startsWith("leave") ) cmd_leave: {
			wasValidCommand = true;

			command = command.substring(5).trim();
         if( "".equals( command )){
            sendMessage(replyTo, "Leave what? Try 'list', 'list <resource>' or 'help'. (and 'list <user>' in the future).");
            break cmd_leave;
         }


			String parts[] = command.split(" ");
			String resName = parts[0];

         String leftResourcesStr = null;

         // Try to leave, or
         try {
            if( "all".equals(resName) ){
               log.debug("Leaving ALL resources.");
               List<Resource> leftResources = this.resourceManager.leaveAll(fromUserNorm);
               leftResourcesStr = StringUtils.join( leftResources, ", " );
            }
            else {
               log.debug("Leaving one resource: "+resName);
               this.resourceManager.leave(resName, fromUserNorm);
               leftResourcesStr = resName;
            }
         } catch (JawaBotException ex) {
            log.debug("Can't leave.", ex);
            //sendMessage(replyTo, String.format("You can't leave "+resName+"") );
            sendMessage(replyTo, "You can't leave "+resName+": "+ex.getMessage() );
            break cmd_leave;
         }

         // Announce on IRC.
         String announceToChannel = StringUtils.defaultString( fromChannel, this.getConfig().settings.announceDefaultChannel );
         sendMessage(announceToChannel,
                     String.format("%s has left %s.",
                     fromUserNorm,
                     leftResourcesStr
         ) );

         // Announce on mailing list.
         trySendMail(fromUser, "Leaving "+leftResourcesStr, "", announceToChannel);
         


		}// cmd_leave



		// List
		else if( command.startsWith("list") ) {
			wasValidCommand = true;

			do{
				String[] parts = command.split(" ");
				if( parts.length == 1 ){
					sendMessage(replyTo, this.resourceManager.listResourcesAsString());
					break;
				}
				if( parts.length == 2 ){
					String resName = parts[1];
					try {
						String list = this.resourceManager.printResourceReservations( resName );
						for( String line : list.split("\n") ){
							sendMessage(replyTo, line);
						}
					}
					catch( JawaBotException ex ){
						sendMessage(replyTo, ex.getMessage());
					}
					break;
				}

			}while( false );
		}




      // Save the state.
		else if( !isFromPrivateMessage && command.startsWith("save") ) {
			wasValidCommand = true;
			sendMessage(replyTo, "Saving state.");
			this.save();
		}





		// Leave channel.
		else if( !isFromPrivateMessage && command.startsWith("please leave") ) {
			wasValidCommand = true;
			sendMessage(replyTo, "Bye everyone. I'll be around; if you miss me later, /invite me.");
			this.partChannel(fromChannel, "Persona non grata.");
		}


		// Die - PM only.
		else if (
                  ( isFromPrivateMessage && command.startsWith("quit " + quitPassword) )
						/* ||
                  ( this.getConfig().getSettingBool(UNSECURED_SHUTDOWN) && command.startsWith("diedie") ) /**/
              )
		{
			wasValidCommand = true;
			sendMessage(replyTo, "Bye, shutting down.");
			//this.partChannel(from, "Warp core overload."); // From private message.
			this.disconnect();
		}

      
		// About or Help.
		else if( command.startsWith("about") || command.startsWith("help") ) {
			wasValidCommand = true;
			sendMessage(replyTo,
							"Hi, I'm a bot which makes reservations of resources, mainly Brno lab machines. Version: "+VERSION);
			sendMessage(replyTo,
							"If you want me in your channel, invite me, usually done by typing '/invite " + this.getNick() + "' in that channel.");
			sendMessage(replyTo,
							"If you don't like me, kick me off. Or say '"+this.getNick()+" please leave'.");
			sendMessage(replyTo,
							"Basic usage: list [<resource>] | find <from-date> [<to-date>] | (take/keep) <resource> | leave <resource>");
			sendMessage(replyTo,
							"For more info, see "+PROJECT_DOC_URL);
		}


		// Purge old reservations.
		doCleanUp();




		return wasValidCommand;

	}// handleJiraBotCommand()




	/** Sends an announcement mail to a mailing list (currently jboss-qa-brno).
	 *
	 * @param resource
	 * @param reservation
	 * @throws org.jboss.jawabot.JawaBotException
	 */
	private void announceTakeOnMailingList( Resource resource, ReservationWrap reservation ) {
      
      ConfigBean cnf = this.getConfig();

      String subject = String.format("%s %s from %s to %s (JawaBot "+VERSION+")",
                              (reservation.type == ReservationWrap.Type.NORMAL) ? "Taking" : "Keeping",
										resource.getName(),
										DateParser.format( reservation.getFrom() ),
										DateParser.format( reservation.getTo() )
                       );

      trySendMail( reservation.getForUser(), subject, "", cnf.settings.announceDefaultChannel);

	}


   /** Sends a mail announcement about user's action. */
   private void sendMail( String fromUser, String subject, String message ) throws JawaBotException {

      ConfigBean cnf = this.getConfig();

      log.debug( String.format("Sending mail: host %s, to %s, from %s <%s>",
			cnf.settings.smtpHost,
			cnf.settings.announceEmailTo,
         fromUser,
			cnf.settings.announceEmailFrom
      ) );

		try {
			SimpleEmail email = new SimpleEmail();
			email.setHostName( cnf.settings.smtpHost );
			email.addTo( cnf.settings.announceEmailTo );
			email.setFrom( cnf.settings.announceEmailFrom, fromUser+" via JawaBot");
			email.setSubject( subject );
         message = ( StringUtils.isBlank(message) ? "" : message + "\n\n");
			email.setMsg(message + "This message was generated by JawaBot:  http://wiki.brq.redhat.com/ozizka/JawaBot");
			email.send();
		} catch (EmailException ex) {
			throw new JawaBotException("Can't mail to "+cnf.settings.announceEmailTo+": "+ex.getMessage(), ex);
		}
   }



   private void trySendMail( String fromUser, String subject, String message, String fallbackErrorMsgChannel ){
      try{
         // Send the mail announcement.
         sendMail(fromUser, subject, message);
      }
      catch (JawaBotException ex){
         String excMessage = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
         String reply = excMessage; //"Unable to send announcement email: "+excMessage;
         log.error(reply);
         sendMessage(fallbackErrorMsgChannel, reply);
      }
   }







	/**
	 * Clean up old reservations.
	 */
	private void doCleanUp(){
		log.info("Cleaning up.");
		this.resourceManager.purgeOldReservations();
		
	}



	@Override
	protected void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel) {
      //if( this.getConfig().getSettingBool(SETID_ACCEPT_INVITATION))
			this.joinChannel(channel);
	}


   /*
	@Override
	protected void onPart(String channel, String sender, String login, String hostname) {
			System.out.println("Channels: " + this.getChannels().length  + " - " + Arrays.toString(this.getChannels()));

			// Quit if no channels left.
			if( this.getChannels().length == 0 ) {
					this.disconnect();
			}
	}/**/



	@Override
	protected void onDisconnect() {
      log.info("onDisconnect().");
		// TODO: Reconnect on disconnect - to get over network outages.
		this.dispose();
      synchronized(this){
         this.notifyAll();
      }
	}



   /**
    * Configures this bot according to the given config bean (possibly read from XML).
    * @param config
    */
   public void applyConfig( ConfigBean config )
   {
      this.config = config;

      // Settings.
      this.setVerbose( config.settings.verbose ); // Enable debugging output.

      // Resources.
      for( Resource res : config.getResources() ){
         //this.getResourceManager().addResource( new Resource( res.getName(), res.getProject() ));
         this.getResourceManager().addResource( res );
      }

      // TODO: Resource groups.

      // TODO: User groups.

   }

   

   /**
    * Extracts configuration data into a ConfigBean to be persisted.
    * @return
    */
   public ConfigBean extractConfig() {
      throw new UnsupportedOperationException("Not yet implemented");
   }





   /** Updates the bot to according to the state stored in the bean. */
   private void applyState( StateBean state ) throws UnknownResourceException {

      //List<UnknownResourceException> exs = new ArrayList();
      List<String> resNotFound = new ArrayList();

      // Apply reservations.
      this.resourceManager.clearReservations();
      for( ReservationBean resv : state.reservations ){
         for( String resName : resv.resources ){
            try {
               this.resourceManager.bookResource(resName, resv.forUser, resv.from, resv.to);
            } catch (UnknownResourceException ex) {
               resNotFound.add(resName);
            }
         }
      }

      if( resNotFound.size() != 0 )
         throw new UnknownResourceException("Some resources referenced in the persisted state were not found in configuration: "+StringUtils.join( resNotFound, ", ") );
      
   }


   
   /** Extracts the state bean from the bot's current state. */
   public StateBean extractState() {

      StateBean state = new StateBean();

      // For each resource...
      for( Map.Entry<Resource, ReservationCalendar> en : this.getResourceManager().getReservationCalendars().entrySet() ){
         Resource res = en.getKey();
         ReservationCalendar cal = en.getValue();

         List<String> resourceAsList = Arrays.asList( new String[]{ res.getName() } );

         for( Reservation resv : cal.getReservations() ){
            state.reservations.add( new ReservationBean( resv.getForUser(), resv.getFrom(), resv.getTo(), resourceAsList ) );
         }         
      }

      return state;

      //throw new UnsupportedOperationException("Not yet implemented");
   }


   

   /**
    * TODO
    */
   private void save() {
      throw new UnsupportedOperationException("Not yet implemented");
   }



		
}// class



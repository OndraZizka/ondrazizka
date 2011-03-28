package org.jboss.jawabot;

import java.io.File;
import java.io.IOException;
import org.jboss.jawabot.ex.UnknownResourceException;
import org.jboss.jawabot.ex.JawaBotIOException;
import org.jboss.jawabot.ex.JawaBotException;
import java.util.*;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.jboss.jawabot.config.beans.ConfigBean;
import org.jboss.jawabot.state.beans.StateBean;
import org.jibble.pircbot.PircBot;
import org.apache.log4j.Logger;
import org.jboss.jawabot.ResourceManager.ReservationsBookingResult;
import org.jboss.jawabot.config.JaxbConfigPersister;
import org.jboss.jawabot.config.beans.ServerBean;
import org.jboss.jawabot.state.JaxbStatePersister;
import org.jboss.jawabot.state.beans.ReservationBean;
import org.jibble.pircbot.NickAlreadyInUseException;



/**
 * JawaBot implementation.
 *
 * TODO: Move actions from handleCommand() to some CommandHandlerImpl?
 *
 * @author ondra
 */
@XmlRootElement
public class JawaBot extends PircBot
{
   private static final Logger log = Logger.getLogger( JawaBot.class );

   
	final String USUAL_NICK = "jawabot";



	// Things shown in the "help" command reply.
	public static final String VERSION = "1.4.3";
	public static final String PROJECT_DOC_URL = "https://docspace.corp.redhat.com/clearspace/docs/DOC-29621";


   private ConfigBean config;
   public ConfigBean getConfig() {      return config;   }


   private boolean initialized = false;
   public boolean isInitialized() {      return initialized;   }

   private CommandHandler commandHandler;


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
      log.info("                        ***  QUIT PASSWORD: "+this.quitPassword+"  ***");
      log.info("");
	}



	/**
    * Init - loads the state (resources reservations) and stores the quit password.
    */
	public synchronized void init() throws JawaBotIOException, UnknownResourceException {
      log.info("Initializing...");
      if( this.initialized )
         log.warn("Already initialized.");

		//this.resourceManager.loadResources( new StaticResourcesLoader() ); // Moved to config persister.

      StateBean state = new JaxbStatePersister().load();
      this.applyState( state );

      
      // Store the quit password.
      try {
         File passFile = new File("quit" + this.quitPassword + ".txt");
         FileUtils.touch( passFile );
         FileUtils.forceDeleteOnExit( passFile );
      } catch (IOException ex) {
         log.error("Can't write password file: "+ex, ex);
      }

      // Create a command handler.
      this.commandHandler = new CommandHandlerImpl(this);
      
      this.initialized = true;
	}



   /**
    * Connects the server(s), joins the channels...
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
      
   }// connectAndJoin()






	/**
	 * Callback to handle a channel message:
    * Checks whether the msg starts with bot's nickname, and if so,
    * calls handleJawaBotCommand(), giving the channel name,
    * calling user's nick and the message.
	 */
	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message)
   {
      if( ! this.isInitialized() ){
         log.warn("Called onMessage(), but not initialized yet.");
         return;
      }


      // Either process a command or search for Jira IDs.
      boolean wasCommand = false;

      String msgNorm = message.trim().toLowerCase();

      // Check for presence of bot nick prolog.
      boolean startsWithUsualNick = isMsgForNick( msgNorm, USUAL_NICK );
      boolean startsWithBotNick = isMsgForNick( msgNorm, this.getNick() );

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
         String command = msgNorm.substring( prologEnd );
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
	 * Handles a command, which is (assumably) sent as PM to the bot.
	 *
	 * @param fromChannel  Channel (#...) from which the message was received. Can be null for private messages.
	 * @param fromUser     Nick from which the message was received.
	 * @param command  The command - the relevant part - i.e. ignoring
	 *                 "jawabot" etc at the beginning of the messagge.
	 *
	 * @returns  true if the request was valid JiraBot command.
	 */
	private boolean handleJawaBotCommand( String fromChannel, String fromUser, final String commandOrig )
   {
      log.debug( String.format("%s %s %s", fromChannel, fromUser, commandOrig) );


      // Command context. TODO: Make use of it in all commands.
      CommandContext ctx = new CommandContext(fromUser, fromChannel);

      // Command handler reply.
      CommandReply reply = null; // TBD: Temporarily being set to null, until all commands are moved.
      

		boolean wasValidCommand = false;
		boolean wasValidSyntax  = true;
		boolean stateChanged = false; // TODO: Watch state changes in the reservationsManager.

		// Is private message?
		boolean isFromPrivateMessage = null == fromChannel;

		// Reply either to PM or to a channel.
		String replyTo = isFromPrivateMessage ? fromUser : fromChannel;
      //System.out.println("isFromPrivateMessage: "+isFromPrivateMessage);///

		String command = commandOrig.toLowerCase();

      
		// Take / Keep
		if( command.startsWith("take") || command.startsWith("keep") ) {
			wasValidCommand = true;

         String params = command.substring(4).trim();
         reply = commandHandler.onTake( ctx, params );
         
		}// take



		// Find
		else if( command.startsWith("find") || command.startsWith("free") ) {
			wasValidCommand = true;

			String params = commandOrig.substring(4).trim();
         reply = this.commandHandler.onFind( ctx, params );

		}



		// Find
		else if( command.startsWith("my") ) {
			wasValidCommand = true;

			String params = commandOrig.substring(2).trim();
         reply = this.commandHandler.onList( ctx, ctx.fromUserNorm );
         
		}



		// Leave (resource)
		else if( command.startsWith("leave") ) cmd_leave: {
			wasValidCommand = true;

			String params = command.substring(5).trim();
         reply = commandHandler.onLeave( ctx, params );

		}// cmd_leave


      
		// List
		else if( command.startsWith("list") ) {
			wasValidCommand = true;
         
         String params = command.substring(4).trim();
         reply = commandHandler.onList( ctx, params );
		}



      // Save the state.
		else if( !isFromPrivateMessage && command.startsWith("save") ) {
			wasValidCommand = true;
         try {
            this.saveState();
   			sendMessage(replyTo, "State saved.");
         } catch( JawaBotIOException ex ) {
            String msg = "Error saving state: "+ex.getMessage();
            sendMessage(replyTo, msg);
            log.error( msg, ex);
         }
		}



		// Join a channel.
		else if( command.startsWith("join") ) {
			wasValidCommand = true;
         reply = commandHandler.onJoin( ctx, command.substring(4).trim() );
		}


		// Leave the current channel.
		else if( !isFromPrivateMessage && command.startsWith("please leave") ) {
			wasValidCommand = true;
			sendMessage(replyTo, "Bye everyone. I'll be around; if you miss me later, /invite me.");
			this.partChannel(fromChannel, "Persona non grata.");
		}


		// Die - PM only.
		else if (  ( isFromPrivateMessage && command.startsWith("quit " + quitPassword) )
						 ||
                 ( this.getConfig().settings.unsecuredShutdown && command.startsWith("diedie") )
              )
		{
			wasValidCommand = true;
         stateChanged = true;
			sendMessage(replyTo, "Bye, shutting down.");
			//this.partChannel(from, "Warp core overload."); // From private message.
			this.disconnect();
		}

      
		// About or Help.
		else if( command.startsWith("about") || command.startsWith("help") ) {
			wasValidCommand = true;
         reply = commandHandler.onHelp( ctx, command );
		}



      log.debug("Done processing command. Reply is: "+reply);

      
      // Temporarily in an if; after moving, throw if null.
      if( reply != null ){
         // Temporary - copy the flag.
         stateChanged = reply.stateChanged;
         wasValidSyntax = ! reply.reportInvalidSyntax;

         // Send IRC messages.

         // Default and debug channel.
         reply.additionalAnnounceChannels.add( config.settings.announceDefaultChannel );
         reply.additionalAnnounceChannels.add( config.settings.debugChannel );

         boolean noDangerOfReplyDubbing = ctx.isPrivate || ! reply.additionalAnnounceChannels.contains( replyTo );

         // Send IRC messages.
         for( CommandReplyMessage msg : reply.ircMessages ){
            if( msg.isAnnouncement )
            for( String sendTo : reply.additionalAnnounceChannels ){
               sendMessage( sendTo, msg.text );
            }
            // Prevent duplication: To send this reply, it must not go to the channel where it already was sent to.
            if( msg.isReply && ( noDangerOfReplyDubbing || ! msg.isAnnouncement ) ){
               sendMessage( replyTo, msg.text );
            }
         }

         // Send mail announcements.
         for( MailData mail : reply.mailAnnouncements ){
            trySendMail( mail, ctx.fromUserNorm, ctx.fromChannel );
         }
      }
      

      // If the state changed, save.
      // TODO: Watch state changes in the reservationsManager.
      if( stateChanged ){
         try {
            this.saveState();
            String msg = String.format("State saved after %s on %s did command: %s", fromUser, fromChannel, commandOrig );
            log.info( msg );
   			sendDebugMessage( msg );
         } catch (JawaBotIOException ex) {
            String msg = "Error saving state: "+ex.getMessage();
            log.error( msg, ex );
            sendDebugMessage( msg );
         }
      }


		// Purge old reservations.
		doCleanUp();


      // Invalid command?
      if( ! wasValidCommand ){
         sendMessage( replyTo, "Invalid command, see "+this.PROJECT_DOC_URL );
      }

      // Invalid syntax?
      if( ! wasValidSyntax ){
         sendMessage( replyTo, "Invalid command syntax, see "+this.PROJECT_DOC_URL );
      }

		return wasValidCommand;

	}// handleJiraBotCommand()






   
   
	/**
    * Sends an announcement mail to a mailing list (currently jboss-qa-brno).
    * @deprecated in favor of #announceTakeOnMailingList( ReservationsBookingResult bookingResult, String customComment)
	 */
	private void announceTakeOnMailingList( Resource resource, ReservationWrap reservation, String customComment )
   {
      ConfigBean cnf = this.getConfig();
      String subject = JawaBotUtils.formatReservationInfoLine(resource.getName(), reservation);
      trySendMail( new MailData( subject, customComment ), reservation.getForUser(), cnf.settings.announceDefaultChannel);
	}



	/**
    * Sends an announcement mail to a mailing list.
    * @deprecated  in favor of CommandHandlerImpl.createTakeAnnouncementMail() .
	 */
   private void announceTakeOnMailingList( ReservationsBookingResult bookingResult, String customComment )
   {
      ConfigBean cnf = this.getConfig();
      String subject = JawaBotUtils.formatReservationInfoLine( bookingResult );

      // Message body.

      // Custom comment.
      StringBuilder sb = new StringBuilder();
      if( null != customComment )
         sb.append(customComment).append("\n");

      // List the reservations.
      if( bookingResult.resultingReservations.size() > 1 ){
         for( ReservationWrap resvWrap : bookingResult.resultingReservations ){
            sb.append( JawaBotUtils.formatReservationInfoLine(resvWrap.resourceName, resvWrap) );
            sb.append("\n");
         }
      }

      String messageBody = sb.toString();
      
      trySendMail( new MailData( subject, messageBody ), bookingResult.claimedResv.getForUser(), cnf.settings.announceDefaultChannel );
   }



   /**
    * Tries to send a mail; eventual failure is announced on the given channel.
    */
   private void trySendMail( MailData mail, String fromUser, String fallbackErrorMsgChannel ){
      try{
         // Send the mail announcement.
         sendMail( fromUser, mail );
      }
      catch (JawaBotException ex){
         String excMessage = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
         String reply = excMessage; //"Unable to send announcement email: "+excMessage;
         log.error(reply);
         sendMessage(fallbackErrorMsgChannel, reply);
      }
   }


   /**
    * Sends a mail announcement about user's action.
    */
   private void sendMail( String fromUser, MailData mail ) throws JawaBotException {

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
			email.addTo( cnf.settings.announceEmailTo ); // TODO: Let it depend on the resource's group.
			email.setFrom( cnf.settings.announceEmailFrom, fromUser+" via JawaBot");
			email.setSubject( mail.subject );
         String messageBody = ( StringUtils.isBlank(mail.messageBody) ? "" : mail.messageBody + "\n\n");
			email.setMsg(messageBody + "Possible sender's e-mail: "+fromUser+"@redhat.com\nThis message was generated by JawaBot "+VERSION+".\n"+PROJECT_DOC_URL);
			email.send();
		} catch (EmailException ex) {
			throw new JawaBotException("Can't mail to "+cnf.settings.announceEmailTo+": "+ex.getMessage(), ex);
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
               this.resourceManager.bookResources(resName, resv.forUser, resv.from, resv.to);
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
    * Load the state.
    */
   private void loadState() throws JawaBotIOException, UnknownResourceException {
      JaxbStatePersister sp = new JaxbStatePersister();
      log.info("Loading via: "+sp);
      StateBean state = sp.load();
      this.applyState( state );
   }


   /**
    * Save the state.
    */
   private void saveState() throws JawaBotIOException {
      JaxbStatePersister sp = new JaxbStatePersister();
      log.info("Saving via: "+sp);
      sp.save( this.extractState() );
   }


   /** Send a message to the debug channel. */
   private void sendDebugMessage(String msg) {
      this.sendMessage( this.getConfig().settings.debugChannel, msg );
   }



   /**
    * Does not support multiple nicks.
    * Returns false if the message consists only of the nick.
    *
    * @param msg   IRC message, like "ozizka: How are you?"
    * @param nick  User nick, like "ozizka".
    * @returns true if the message is intended for the given nick.
    *
    * TODO: Return the position of the end of the prolog (start of the actual message).
    */
   private boolean isMsgForNick( String msg, String nick ) {
      if( null == msg || msg.equals("") || null == nick || nick.equals("") )
         return false;

      return msg.startsWith( nick.toLowerCase() )
          // At least one char besides the nick.
          && msg.length() > nick.length() + 2
          // Char after the usual nick is something that "terminates the nick".
          && StringUtils.contains(" ,:", msg.charAt( nick.length() ) );
   }




		
}// class



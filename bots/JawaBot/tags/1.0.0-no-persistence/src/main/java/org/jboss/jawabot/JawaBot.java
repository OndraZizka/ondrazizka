package org.jboss.jawabot;

import java.text.ParseException;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.jibble.pircbot.PircBot;
import org.apache.commons.mail.SimpleEmail;




public class JawaBot extends PircBot {

	
	final String BOT_NICK = "JawaBot";
	final String USUAL_NICK = "jawabot";

	final String SMTP_HOSTNAME = "mail.corp.redhat.com";
	final String ANNOUNCE_EMAIL_TO = "jboss-qa-brno@redhat.com";
	final String ANNOUNCE_EMAIL_FROM = "jawabot-no-reply@redhat.com";

	// Default channel to announce reservations if sent from PM.
	final String ANNOUNCE_DEFAULT_CHANNEL = "#jboss-qa-brno";

	
	// Things shown in the "help" command reply.
	private static final String VERSION = "1.0.00";
	private static final String PROJECT_DOC_URL = "http://wiki.brq.redhat.com/ozizka/JawaBot";

	private ResourceManager resourceManager = new ResourceManager();

	private int quitPassword = new Random().nextInt(1000);


	

	/** Const */
	public JawaBot() {
			this.setName( BOT_NICK );

			this.init();

			// Log the quit password.
			System.out.println("\n\n  *** QUIT PASSWORD: " + quitPassword + " ***\n");
	}



	/** Init - load resources. */
	private void init() {
		this.resourceManager.loadResources( new StaticResourcesLoader() );
	}



	/**
	 * onMessage
	 */
	@Override
	public void onMessage(String channel, String sender, String login, String hostname, String message) {

			// Either process a command or search for Jira IDs.
			boolean wasCommand = false;

			String msgNormalized = message.trim().toLowerCase();

			// Check for presence of bot nick prolog.
			boolean startsWithUsualNick = msgNormalized.startsWith(USUAL_NICK.toLowerCase());
			boolean startsWithBotNick = msgNormalized.startsWith(BOT_NICK.toLowerCase());

			// If the prolog is present,
			if( startsWithUsualNick || startsWithBotNick ){

				// remove it,
				int prologEnd;
				if( startsWithUsualNick && startsWithBotNick ){
					prologEnd = Math.max( USUAL_NICK.length(), BOT_NICK.length() );
				}
				else {
					prologEnd = startsWithUsualNick ? USUAL_NICK.length() : BOT_NICK.length();
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


	@Override
	protected void onPrivateMessage(String sender, String login, String hostname, String message) {
			handleJawaBotCommand(null, sender, message.trim());
	}




	/**
	 * Parses one or two dates from the given string in the form "YYYY-MM-DD[ YYYY-MM-DD]".
	 * If the second
	 *
	 * @param paramsStr
	 * @return
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
	boolean handleJawaBotCommand( String fromChannel, String fromUser, String command ) {

		boolean wasValidCommand = false;

		boolean isFromPrivateMessage = null == fromChannel;
		String replyTo = isFromPrivateMessage ? fromUser : fromChannel;
		System.out.println("isFromPrivateMessage: "+isFromPrivateMessage);///

		command = command.toLowerCase();

		final String DATE_FORMAT = "yyyy-MM-dd";


		// Take
		if( !isFromPrivateMessage && command.startsWith("take") ) {
			wasValidCommand = true;
			do {
				command = command.substring(4).trim();
				
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
					Reservation collidingReservation = resourceManager.bookResource(resName, fromUser, fromDate, toDate);

					// Collision with another reservation.
					if( null != collidingReservation ){
						sendMessage(replyTo, String.format("Reservation collision!  %s is reserved for %s from %s to %s.",
										resName,
										collidingReservation.getForUser(),
										DateParser.format( collidingReservation.getFrom() ),
										DateParser.format( collidingReservation.getTo() )
						) );
					}

					// No collision, we're free to go.
					else{

						// Send the IRC reply.
						final String RESERVATION_OK_FORMAT = "%s was succesfully reserved for %s from %s to %s.";
						String announceTo = StringUtils.defaultString(fromChannel, ANNOUNCE_DEFAULT_CHANNEL); // TODO: Something smarter?
						sendMessage(announceTo,
										String.format(RESERVATION_OK_FORMAT,
										resName,
										fromUser,
										DateParser.format( fromDate ),
										DateParser.format( toDate )
						) );

						try{
							// Send the mail announcement.
							announceOnMailingList( resourceManager.getResource(resName), new Reservation(fromUser, fromDate, toDate) );
						}
						catch (JawaBotException ex){
							String excMessage = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
							sendMessage(announceTo, "Unable to send announcement email to "+ANNOUNCE_EMAIL_TO+": "+excMessage);
						}

					}
					break;
				}
				catch (JawaBotException ex) {
					sendMessage(replyTo, ex.getMessage());
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
		else if( !isFromPrivateMessage && command.startsWith("leave") ) {
			wasValidCommand = true;

			command = command.substring(5).trim();
			String parts[] = command.split(" ");
			String resName = parts[0];

			do{
				// Try to leave, or
				try {
					this.resourceManager.leave(resName, fromUser);
				} catch (JawaBotException ex) {
					//sendMessage(replyTo, String.format("You can't leave "+resName+"") );
					sendMessage(replyTo, "You can't leave "+resName+": "+ex.getMessage() );
					break;
				}

				String announceTo = StringUtils.defaultString(fromChannel, "#jboss-qa-brno"); // TODO: Something smarter?
				sendMessage(announceTo,
								String.format("%s has left %s.",
								fromUser,
								resName
				) );
			}while( false );
		}



		// List
		else if( command.startsWith("list") ) {
			wasValidCommand = true;

			do{
				String[] parts = command.split(" ");
				if( parts.length == 1 ){
					sendMessage(replyTo, this.resourceManager.listResources());
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


		// Find free machine for given date range.
		else if( command.startsWith("find") ) {
			wasValidCommand = true;
			// TODO
		}




		// Leave channel.
		else if( !isFromPrivateMessage && command.startsWith("please leave") ) {
			wasValidCommand = true;
			sendMessage(replyTo, "Bye everyone. I'll be around; if you miss me later, /invite me.");
			this.partChannel(fromChannel, "Persona non grata.");
		}

		// Die - PM only.
		else if ( isFromPrivateMessage && command.startsWith("quit " + quitPassword)
						 || command.startsWith("diedie") /* Top secret :) */ )
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
							"If you want me in your channel, invite me, usually done by typing '/invite " + BOT_NICK + "' in that channel.");
			sendMessage(replyTo,
							"If you don't like me, kick me off. Or say '"+BOT_NICK+" please leave'.");
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
	private void announceOnMailingList( Resource resource, Reservation reservation) throws JawaBotException {

		try {
			SimpleEmail email = new SimpleEmail();
			email.setHostName(SMTP_HOSTNAME);
			email.addTo(ANNOUNCE_EMAIL_TO);
			email.setFrom(ANNOUNCE_EMAIL_FROM, reservation.getForUser()+" via "+BOT_NICK);
			email.setSubject( String.format("Taking %s from %s to %s",
										resource.getName(),
										DateParser.format( reservation.getFrom() ),
										DateParser.format( reservation.getTo() )
							));
			email.setMsg("This message was generated by JawaBot:  http://wiki.brq.redhat.com/ozizka/JawaBot");
			email.send();
		} catch (EmailException ex) {
			throw new JawaBotException("Error when sending announcement mail: "+ex.getMessage(), ex);
		}

	}





	/**
	 * Clean up old reservations.
	 */
	private void doCleanUp(){
		
		this.resourceManager.purgeOldReservations();
		
	}



	@Override
	protected void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel) {
			this.joinChannel(channel);
	}


	@Override
	protected void onPart(String channel, String sender, String login, String hostname) {
			// Disconnect if the bot isn't in any channel.
			System.out.println("Channels: " + this.getChannels().length  + " - " + Arrays.toString(this.getChannels()));

			// Quit if no channels left.
			/*if( this.getChannels().length == 0 ) {
					this.disconnect();
			}/**/
	}

	@Override
	protected void onDisconnect() {
		// TODO: Reconnect on disconnect - to get over network outages.
		//IssueCache.getSingleton().stopExporyThread();
		this.dispose();
	}
		
}// class



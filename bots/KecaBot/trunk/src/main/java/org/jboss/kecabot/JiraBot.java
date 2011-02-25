package org.jboss.kecabot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.kecabot.config.JaxbConfigReader;
import org.jboss.kecabot.config.JiraBotIOException;
import org.jboss.kecabot.config.beans.ConfigBean;
import org.jboss.kecabot.config.beans.RepositoryBean;
import org.jboss.kecabot.config.beans.ServerBean;
//import org.jboss.jirabot.repo.RepositoryParser;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;



/**
 * JiraBot - An IRC Bot showing information from issue trackers,
 *           mainly from Jira.
 *
 * @author Ondrej Zizka
 */
public class JiraBot extends PircBot
{
   private static final Logger log = Logger.getLogger( JiraBot.class );

   private static final String VERSION;
   static{
      String version = null;
      try { version = JiraBot.class.getPackage().getImplementationVersion(); } catch( Throwable ex ){ }
      if( version == null ) version = "";
      VERSION = StringUtils.defaultString( version, "" );
   }


   private static final String PROJECT_DOC_URL = "https://docspace.corp.redhat.com/clearspace/docs/DOC-20386";

   //final String JIRA_KEY_REGEX = "([A-Z]{2,}\\-[0-9]+)";
   private static final String JIRA_KEY_REGEX = "((?<![-_.A-Z])[A-Z]{$minChars,}-[0-9]++)(?!(-|\\.[0-9A-Za-z]))";
   private static final int MIN_ISSUE_PREFIX_LEN = 2;
   private static Pattern JIRA_KEY_PATTERN = Pattern.compile( JIRA_KEY_REGEX.replace("$minChars",  "" + MIN_ISSUE_PREFIX_LEN) );
   //private static Pattern JIRA_KEY_PATTERN = Pattern.compile("([A-Z]{3,}\\-[0-9]+(?!\\.))"); // Not followed by "." -> Bug: "ABC-123." treated as "ABC-12".

   //private static final int MAX_JIRA_IDS_PER_REQUEST = 3;       // TODO: Move to the configuration.
   //private static final int DEFAULT_REPEAT_DELAY_SECONDS = 300;         // TODO: Move to the configuration.
   private static final int DEFAULT_CACHED_ISSUES_TIMEOUT_MINUTES = 60; // TODO: Move to the configuration.


   private ConfigBean config = null;


   // local references for convenience, so getSingleton needn't be called all over the place
   //private IssueCache issueCache = IssueCache.getSingleton();

   //private RepositoryParser repositoryParser = new RepositoryParser();

   private final ChannelsStatusStore channelStatusStore = new ChannelsStatusStore();


   int quitPassword = new Random().nextInt(1000);
   private static final String BOT_NICK = "KecaBot";
   private static final String USUAL_NICK = "kecabot";


   /** When true, the bot was shut down deliberately and should not re-connect automatically. */
   private boolean shuttingDown = false;
   public boolean isShuttingDown() {			return shuttingDown;		}
   private void setShuttingDown(boolean shuttingDown) {			this.shuttingDown = shuttingDown;		}



   /**  Const  */
   public JiraBot() {
      this.setName( BOT_NICK );

      // Log the quit password.
      System.out.println("\n\n  *** QUIT PASSWORD: " + quitPassword + " ***\n");

      // Save the quit passwd to file.
      try {
         FileUtils.writeStringToFile(new File("quit.txt"), "" + quitPassword);
      } catch (IOException ex) {
         log.error( "Failed storing quit password: "+ex.getMessage(), ex );
      }
   }


   /** Initializes this JiraBot. Should happen before connecting. */
   void init( Options options ) throws JiraBotIOException {
      String profile = options.getProfile();
      String fileName = StringUtils.isEmpty( profile ) ? "JiraBotConfig.xml" : ("JiraBotConfig-" + profile + ".xml");
      ConfigBean config_ = new JaxbConfigReader(fileName).load();
      this.applyConfig( config_ );

   }


   /** Starts the bot. Method to hide the connect method (to make it more like state machine). */
   void start() throws JiraBotException {
      this.connect();
   }

   

   /**
    * Connect to the default server and join the default channels.
    *
    * TODO: Move data to configuration and read the conf in some init() method.
    */
   private void connect() throws JiraBotException {

      if( this.isConnected() ){
         throw new JiraBotException("Already connected.");
      }

      try {

         // Connect to the server
         if( this.config.irc.servers.size() == 0 )
            throw new JiraBotException("No servers configured.");
         ServerBean server = config.irc.servers.get(0);
         this.log("Connecting to " + server.host + " using nick '"+ this.getName() +"'.");
         this.connect( server.host );

         // Join the default channels
         for( String channel : server.autoJoinChannels ){
           this.log("Joining "+channel);
           this.joinChannel( channel );
         }
      }
      catch (IOException ex) {
         this.log(ex.toString());
         throw new JiraBotException("Can't connect: "+ex.getMessage(), ex);
      }
      catch (NickAlreadyInUseException ex) {
         this.log("Nick already in use. "+ex.getMessage());
         throw new JiraBotException("Nick already in use. "+ex.getMessage());
      }
      catch (IrcException ex) {
         this.log(ex.toString());
         throw new JiraBotException("Can't connect: "+ex.getMessage(), ex);
      }
   }

		
    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message) {

				// Either process a command or search for Jira IDs.
				boolean wasCommand = false;


				String msgNormalized = message.trim().toLowerCase();

				// Check for presence of bot nick prolog.
				boolean startsWithJiraBot = msgNormalized.startsWith(USUAL_NICK.toLowerCase());
				boolean startsWithBotNick = msgNormalized.startsWith(BOT_NICK.toLowerCase());

				// If the prolog is present,
				if( startsWithJiraBot || startsWithBotNick ){

					// remove it,
					int prologEnd;
					if( startsWithJiraBot && startsWithBotNick ){
						prologEnd = Math.max( USUAL_NICK.length(), BOT_NICK.length() );
					}
					else {
						prologEnd = startsWithJiraBot ? USUAL_NICK.length() : BOT_NICK.length();
					}

					String command = msgNormalized.substring( prologEnd );
					command = StringUtils.removeStart(command, ":").trim();

					// and process the command.
					wasCommand = handleBotCommand(channel, command, false);

				}


				// Not a command?  Search for Jira IDs.
				if( !wasCommand ){
				}

				// TODO: Perhaps create some class for request info, carrying information like
				//       isPrivateMsg, from, isCommand, commandName, command details, list of jiras found, etc.
				//       parse the message into it's object, and then pass such object to the handlers.
				//       (We don't need such sophisticated approach now :)

    }


    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        this.handleBotCommand(sender, message.trim(), true);
    }






		/**
		 * Handles a command, which is (assumably) sent as PM to the bot.
		 *
		 * @param replyTo     Nick or channel (#...) from which the message was received.
		 * @param command  The command - the relevant part - i.e. ignoring
		 *                 "jirabot" etc at the beginning of the messagge.
		 *
		 * @returns  true if the request was valid JiraBot command.
		 */
    boolean handleBotCommand( String from, String command, boolean isFromPrivateMessage ) {

			boolean wasValidCommand = false;

         String replyTo = this.config.settings.debug ? this.config.settings.debugChannel : from;

			command = command.toLowerCase();

         // Join a channel.
         if( command.startsWith("join") ) {
            wasValidCommand = true;

            if( ! this.config.settings.allowJoinCommand ){
               this.sendMessage( replyTo, "join command not allowed - use /invite (if you are an op) or allow in JiraBot settings.");
            } else {
               String channel = command.substring(4).trim();
               if( ! channel.startsWith("#") )
                  channel = "#" + channel;
               if( ! StringUtils.isAlphanumeric( channel.substring(1) ) ){
                  this.sendMessage( replyTo, "Invalid channel name - must be alphanumeric: "+channel );
               }else{
                  this.sendMessage( replyTo, "Joining channel: "+channel );
                  this.joinChannel( channel );
               }
            }
         }


			// Leave - channel only.
			else if( !isFromPrivateMessage && command.startsWith("please leave") ) {
				wasValidCommand = true;
				this.sendMessage( replyTo, "Bye everyone. I'll be around; if you miss me later, /invite me.");
				this.partChannel( replyTo, "Persona non grata.");
			}

			// Die - PM only.
			else if ( isFromPrivateMessage && command.startsWith("diedie") /* Top secret :) */ )
			{
				wasValidCommand = true;
				this.sendMessage( replyTo, "Gotcha!");
				this.sendMessage("#embjopr", replyTo+" is trying to kill me! Heeelp!");
			}

			// Quit - PM only.
			else if ( isFromPrivateMessage && command.startsWith("quit " + quitPassword) )
			{
				this.sendMessage( replyTo, "Bye, shutting down.");
				this.setShuttingDown(true);
				// /part channels with a message?
				this.disconnect();
			}


			// About or Help.
			else if( command.startsWith("about") || command.startsWith("help") ) {
				wasValidCommand = true;
				this.sendMessage( replyTo,
								"Hi, I'm a bot which brings some useful info about JIRA issues to IRC channels. Version: "+VERSION);
				this.sendMessage( replyTo,
								"If you want me in your channel, invite me, usually done by typing '/invite " + BOT_NICK + "' in that channel.");
				this.sendMessage( replyTo,
								"If you don't like me, kick me off. Or say 'jirabot please leave'.");
				this.sendMessage( replyTo,
								"For more info, see "+PROJECT_DOC_URL);
			}

			// Specially for Rado:  Thanks.
			else if( command.startsWith("thank") ) {
				wasValidCommand = true;
				this.sendMessage( replyTo, "yw");
			}
			else if( command.contains("good") ) {
				wasValidCommand = true;
				this.sendMessage( replyTo, "Oh yes indeed.");
			}

			return wasValidCommand;

    }// handleJiraBotCommand()


		
    @Override
    protected void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel) {
        this.joinChannel(channel);
    }


    @Override
    protected void onPart(String channel, String sender, String login, String hostname) {
        // Disconnect if the bot isn't in any channel.
        System.out.println("Channels: " + this.getChannels().length  + " - " + Arrays.toString(this.getChannels()));
    }

    @Override
    protected void onDisconnect() {
			// Unintentional
			if( !this.isShuttingDown() ){
				this.log("Disconnected unintentionally, re-connecting...");
				try {
					this.connect();
				} catch (JiraBotException ex) {
					this.log("Re-connect attempt failed: "+ex.getMessage());
				}
			}
			// Intentional
			else {
				log.info("Disconnected intentionally, stopping threads...");
				this.dispose();
            log.info("PircBot#dispose() done.");
			}
    }



    /**
     *   Apply the data from the config to this JiraBot.
     *   That means, join to channels not currently joined, etc.
     */
    private void applyConfig(ConfigBean config_) {

       //synchronized(this.configLock){
         this.config = config_;
       //}

       this.setName( StringUtils.defaultIfEmpty( this.config.irc.nick, BOT_NICK ) );

       
   }// applyConfig()

  
}// class



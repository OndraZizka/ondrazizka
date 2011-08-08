
package org.jboss.jawabot.irc;

import java.util.Arrays;
import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import org.jboss.jawabot.JawaBot;
import org.jboss.jawabot.JawaBotApp;

/**
 *  This is for commands for whole app, like bot control, app control etc.
 *  Handles received text commands, using CommandContext as input 
 *  and CommandReply as output.
 *  
 *
 * @author Ondrej Zizka
 */
public class CommandHandlerImpl implements CommandHandler
{
   private static final Logger log = LoggerFactory.getLogger( CommandHandlerImpl.class );

   
   
   JawaIrcBot ircBot;

   public JawaIrcBot getIrcBot() { return ircBot; }
   private JawaBot getJawaBot() { return ircBot.getJawaBot(); }
   
   

   
   /** Const */
   public CommandHandlerImpl( JawaIrcBot bot ) {
      this.ircBot = bot;
   }




   @Override
   public CommandReply onJoin( CommandContext ctx, String params ) {
      CommandReply reply = new CommandReply();
      reply.wasSuccessful = true;
      
      do{
         if( "".equals( params ) ){
            reply.reportInvalidSyntax = true;
            break;
         }

         String channelToJoin = params;
         if( '#' != channelToJoin.charAt(0) )
            channelToJoin = '#'+channelToJoin;

         // Already there?
         if( Arrays.asList( this.getIrcBot().getChannels() ).contains( channelToJoin ) ){
            reply.addReply( "Already was at "+channelToJoin );
            break;
         }

         // Join.
         reply.addReply( "Joining channel: "+channelToJoin );
         this.getIrcBot().joinChannel( channelToJoin );
      }
      while( false );

      return reply;
   }


   
   @Override
   public CommandReply onPleaseLeave(CommandContext ctx, String params) {
      CommandReply reply = new CommandReply();
      reply.wasSuccessful = true;
      //return reply;
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public CommandReply onQuit(CommandContext ctx, String params) {
      CommandReply reply = new CommandReply();
      reply.wasSuccessful = true;
      //return reply;
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public CommandReply onHelp(CommandContext ctx, String params) {
      CommandReply reply = new CommandReply();
      reply.wasSuccessful = true;

      reply.addReply("Hi, I'm an universal IRC bot with web interface. Version: "+JawaBotApp.VERSION);
      reply.addReply("If you want me in your channel, type 'join #my-channel', or type '/invite " + this.getIrcBot().getNick() + "' in that channel.");
      reply.addReply("If you don't like me, kick me off. Or say '"+this.getIrcBot().getNick()+" please leave'.");
      //reply.addReply("Basic usage: list [<resource>] ");
      //reply.addReply("             find [<from-date> [<to-date>]] ");
      //reply.addReply("             ( take | keep ) <resource[,resource]> [<from> [<to-date>]] # <comment to mail>");
      //reply.addReply("             leave ( <resource[,resource]> | all )");
      reply.addReply("For more info, see " + JawaBotApp.PROJECT_DOC_URL);

      return reply;
   }



}// class

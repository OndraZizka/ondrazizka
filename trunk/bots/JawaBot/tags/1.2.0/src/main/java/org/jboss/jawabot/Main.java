package org.jboss.jawabot;

import java.io.IOException;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.jboss.jawabot.config.JaxbConfigPersister;
import org.jibble.pircbot.IrcException;

public class Main
{
   private static final Logger log = Logger.getLogger(Main.class);
   /*static{
      org.apache.log4j.BasicConfigurator.configure();
   }*/

   private static JawaBot bot;

   public static JawaBot getBot() {
      return bot;
   }

   public static void main(String[] args) throws JawaBotException {

      log.error("TEST");

      System.out.println("Jawa IRC Bot");
      log.info("Jawa IRC Bot");

      try {
         doSomeIdling();
      } catch (IOException ex) {
         ex.printStackTrace();
      } catch (IrcException ex) {
         ex.printStackTrace();
      }

      log.info("Main#main() end.");
   }

   
   private static void doSomeIdling() throws IOException, IrcException, JawaBotException {

      //JawaBot bot = new JawaBot();
      JawaBot bot = new JaxbConfigPersister("JawaBotConfig-debug.xml").load();
      Main.bot = bot;

      bot.init();
      bot.connectAndJoin();

      // Wait for the end.
      try {
         synchronized(bot){
            bot.wait();
         }
      } catch (InterruptedException ex) {
         log.info("Interrupted.");
      }

   }
}

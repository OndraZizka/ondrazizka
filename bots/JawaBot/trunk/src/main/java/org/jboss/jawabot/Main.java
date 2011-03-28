package org.jboss.jawabot;

import org.jboss.jawabot.ex.JawaBotException;
import org.apache.log4j.Logger;
import org.jboss.jawabot.config.JaxbConfigPersister;

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

      log.debug("Main#main() start.");

      String configFilePath = System.getProperty("config", "JawaBotConfig.xml");


      try {
         createAndRunTheBot( configFilePath );
      } catch ( JawaBotException ex ) {
         ex.printStackTrace();
      }

      log.debug("Main#main() end.");
   }


   /**
    * Creates the bot according to the given config file, initializes it, runs it,
    * and waits until the bot thread shuts down.
    *
    * @param configFilePath    Path to the configuration file.
    * @throws JawaBotException
    */
   private static void createAndRunTheBot( final String configFilePath ) throws JawaBotException {

      JawaBot bot = new JaxbConfigPersister(configFilePath).load();
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

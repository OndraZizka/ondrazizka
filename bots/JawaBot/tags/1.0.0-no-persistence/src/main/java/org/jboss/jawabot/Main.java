package org.jboss.jawabot;

import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.jibble.pircbot.IrcException;

public class Main 
{
    public static final Pattern JIRA_KEY_PATTERN = Pattern.compile("([A-Z]+\\-[0-9]+)", Pattern.CASE_INSENSITIVE);

		private static final Logger log = Logger.getLogger( Main.class );

    public static void main( String[] args ) {

        System.out.println( "Jawa IRC Bot" );
				log.info("Jawa IRC Bot");

        try {
            doSomeIdling();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (IrcException ex) {
            ex.printStackTrace();
        }
    }

		
    private static void doSomeIdling() throws IOException, IrcException {
			
        JawaBot bot = new JawaBot();
        bot.setVerbose(true); // Enable debugging output.

				// Connect to the server
				bot.connect("porky.stuttgart.redhat.com"); // Red Hat

				// Join the default channels
				bot.joinChannel("#some"); // debugging
				//bot.joinChannel("#embjopr");
				bot.joinChannel("#jbosssoaqa");
				bot.joinChannel("#jboss-qa-brno");
    }
}

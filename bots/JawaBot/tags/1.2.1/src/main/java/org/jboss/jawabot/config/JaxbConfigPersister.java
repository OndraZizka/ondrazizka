
package org.jboss.jawabot.config;

import org.jboss.jawabot.JawaBotIOException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;
import org.jboss.jawabot.JawaBot;
import org.jboss.jawabot.config.beans.*;

/**
 *
 * @author Ondrej Zizka
 */
public class JaxbConfigPersister implements ConfigPersister
{
   private static final Logger log = Logger.getLogger(JaxbConfigPersister.class);


   private String filePath = "JawaBotConfig.xml";
   public Writer writer = null;

   // Const

   /** Default config file path: "JawaBotConfig.xml" */
   public JaxbConfigPersister() { }

   /** Configures the bot according to the given config file. */
   public JaxbConfigPersister( String filePath ) {
      this.filePath = filePath;
   }

   /** The given writer will be used when writing via JAXB. */
   public JaxbConfigPersister(Writer soutCopyingFileWriter) {
      this.writer = soutCopyingFileWriter;
   }


   
   /**
    * Loads the configuration from the file (set by setFilePath).
    * @returns new JawaBot configured according to the loaded configuration.
    * TODO: Move to the JawaBot class.
    */
   public JawaBot load() throws JawaBotIOException
   {
      log.info("Loading config from: "+this.filePath);

      try{
         //FileReader reader = new FileReader(filePath);

         InputStream is = JawaBot.class.getClassLoader().getResourceAsStream(this.filePath);
         InputStreamReader reader = new InputStreamReader( is );


         JAXBContext jc = JAXBContext.newInstance( ConfigBean.class );
         Unmarshaller mc = jc.createUnmarshaller();
         ConfigBean config = (ConfigBean) mc.unmarshal(reader);

         JawaBot bot = new JawaBot();
         bot.applyConfig( config );
         return bot;
      }
		catch (/*JAXB*/Exception ex) {
			throw new JawaBotIOException( ex );
		}
   }


   /**
    *
    * @param bot
    */
   public void merge( JawaBot bot ) throws JawaBotIOException {
      throw new UnsupportedOperationException();
   }


   /**
    *
    * @param bot
    * TODO: Move to the JawaBot class.
    */
	public void save( JawaBot bot ) throws JawaBotIOException {

      // Get the config
      ConfigBean config = bot.extractConfig();

      // Store it to a XML.
		try {
         Writer writer = this.writer;
         if( null == writer )
            writer = new FileWriter(filePath);

         JAXBContext jc = JAXBContext.newInstance( ConfigBean.class );
         Marshaller mc = jc.createMarshaller();
         mc.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			mc.marshal( config, writer );
		}
		catch (/*JAXB*/Exception ex) {
			throw new JawaBotIOException( ex );
		}
      
	}


}// class

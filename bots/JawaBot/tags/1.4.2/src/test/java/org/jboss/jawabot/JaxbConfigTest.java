
package org.jboss.jawabot;

import org.jboss.jawabot.ex.JawaBotIOException;
import org.jboss.jawabot.ex.JawaBotException;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.jboss.jawabot.config.*;
import junit.framework.TestCase;








/**
 *
 * @author Ondrej Zizka
 */
@SuppressWarnings("static-access")
public class JaxbConfigTest extends TestCase {

   public void XtestConfigWrite() throws JawaBotIOException, JawaBotException, IOException {

      JawaBot bot = new JawaBot();

      // Write the XML.
      ConfigPersister cp = new JaxbConfigPersister( new SoutCopyingFileWriter("testConfig.xml") );
      cp.save( bot );
      
   }


   public void testConfigRead() throws JawaBotIOException, JawaBotException, IOException, JAXBException
   {
      JaxbConfigPersister re = new JaxbConfigPersister();
      JawaBot bot = re.load();

      /*
      JaxbStateWriter wr = new JaxbStateWriter();
      Writer writer = new SoutCopyingFileWriter("testConfig.xml");
      wr.write(bot, writer );
      */
   }



}// class



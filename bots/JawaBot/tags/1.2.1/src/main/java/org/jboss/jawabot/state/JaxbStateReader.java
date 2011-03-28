
package org.jboss.jawabot.state;

import java.io.FileReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.jboss.jawabot.JawaBot;

/**
 *
 * @author Ondrej Zizka
 */
public class JaxbStateReader {

   public JawaBot read(FileReader reader) throws JAXBException {
      JAXBContext jc = JAXBContext.newInstance( JawaBot.class );
      Unmarshaller mc = jc.createUnmarshaller();
      //mc.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      return (JawaBot) mc.unmarshal(reader);
   }

}// class

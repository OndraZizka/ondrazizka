
package org.jboss.jawabot.state;

import org.jboss.jawabot.ex.JawaBotIOException;
import org.jboss.jawabot.config.*;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.jboss.jawabot.JawaBot;

/**
 *
 * @author Ondrej Zizka
 */
public class JaxbStateWriter {

	public static void write( JawaBot bot, Writer writer ) throws JawaBotIOException {
		try {
         JAXBContext jc = JAXBContext.newInstance( JawaBot.class );
			Marshaller mc = jc.createMarshaller();
         mc.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			mc.marshal( bot, writer );
		}
		catch (/*JAXB*/Exception ex) {
			throw new JawaBotIOException( ex );
		}
	}

}// class

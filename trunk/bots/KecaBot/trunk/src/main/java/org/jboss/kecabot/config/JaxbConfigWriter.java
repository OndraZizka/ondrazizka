
package org.jboss.kecabot.config;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.jboss.kecabot.JiraBot;
import org.jboss.kecabot.config.beans.ConfigBean;

/**
 *
 * @author Ondrej Zizka
 */
public class JaxbConfigWriter {

	public static void write( JiraBot bot, File toFile ) throws JiraBotIOException {
		try {

			JAXBContext jc = JAXBContext.newInstance( ConfigBean.class );
			Marshaller mc = jc.createMarshaller();
			mc.marshal(new Object(), System.out);

		}
		catch (JAXBException ex) {
			throw new JiraBotIOException( ex );
		}
	}

}// class

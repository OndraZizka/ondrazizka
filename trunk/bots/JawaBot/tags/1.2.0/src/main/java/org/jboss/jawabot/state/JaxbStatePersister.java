
package org.jboss.jawabot.state;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;
import org.jboss.jawabot.JawaBotIOException;
import org.jboss.jawabot.config.beans.*;
import org.jboss.jawabot.state.beans.StateBean;

/**
 *
 * @author Ondrej Zizka
 */
public class JaxbStatePersister implements StatePersister
{
   private static final Logger log = Logger.getLogger(JaxbStatePersister.class);
   

   private String filePath = "JawaBotState.xml";
   public void setFilePath(String filePath) {      this.filePath = filePath;   }
   

   private Writer writer = null;
   public void setWriter(Writer writer) {      this.writer = writer;   }


   // Const

   /** Sets "JawaBotState.xml" as default persistence file. */
   public JaxbStatePersister() { }

   /** Sets given persistence file. */
   public JaxbStatePersister( String filePath ) { this.filePath = filePath; }

   /** Sets given writer to be used when writing the XML output. */
   public JaxbStatePersister(Writer writer) {
      this.writer = writer;
   }


   
   /**
    * 
    * @return
    */
   public StateBean load() throws JawaBotIOException
   {
      try{
         FileReader reader = new FileReader(this.filePath);

         JAXBContext jc = JAXBContext.newInstance( StateBean.class );
         Unmarshaller mc = jc.createUnmarshaller();
         StateBean state = (StateBean) mc.unmarshal(reader);
         return state;
      }
      catch ( FileNotFoundException ex) {
         //throw new JawaBotIOException( "State file not found: "+filePath, ex );
         log.info("State file not found - starting clean. "+this.filePath);
         return new StateBean(); // Empty.
      }
		catch ( JAXBException ex) {
			throw new JawaBotIOException( "Error reading state file '"+this.filePath+"': "+ex.getMessage(), ex );
		}
   }


   /**
    *
    * @param bot
    */
   public void merge( StateBean state ) throws JawaBotIOException {
      throw new UnsupportedOperationException();
   }


   /**
    *
    * @param bot
    */
	public void save( StateBean state ) throws JawaBotIOException {

      // Store it to a XML.
		try {
         Writer writer = this.writer;
         if( null == writer )
            writer = new FileWriter(filePath);

         JAXBContext jc = JAXBContext.newInstance( StateBean.class );
         Marshaller mc = jc.createMarshaller();
         mc.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			mc.marshal( state, writer );
		}
		catch (/*JAXB*/Exception ex) {
			throw new JawaBotIOException( ex );
		}
      
	}


}// class

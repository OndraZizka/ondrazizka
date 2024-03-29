
package org.jboss.jawabot;

import org.jboss.jawabot.ex.JawaBotIOException;
import org.jboss.jawabot.ex.JawaBotException;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import java.io.Writer;
import java.util.Date;
import org.apache.commons.lang.time.DateUtils;
import org.jboss.jawabot.plugin.reserv.bus.Resource;
import org.jboss.jawabot.plugin.reserv.bus.ResourceManager;
import org.jboss.jawabot.plugin.reserv.state.JaxbStatePersister;
import org.jboss.jawabot.plugin.reserv.state.beans.ReservationBean;
import org.jboss.jawabot.plugin.reserv.state.beans.StateBean;
import org.jboss.jawabot.testbase.JawaBotTestBase;
import org.jboss.jawabot.testbase.SoutCopyingFileWriter;


/**
 *
 * @author Ondrej Zizka
 */
@SuppressWarnings("static-access")
public class JaxbStateTest extends JawaBotTestBase {

   public void testStateExtract() throws JawaBotIOException, JawaBotException, IOException {

      ResourceManager rm = new ResourceManager();
      // TODO: Apply config. Before I did it through JawaBot.applyConfig().
      rm.addResource( new Resource("jawa01", "projectA") );
      rm.addResource( new Resource("jawa02", "projectA") );

      // Make some reservations...
      Date today = new Date();
      Date today5 = DateUtils.addDays( today, 5 );
      Date today6 = DateUtils.addDays( today, 6 );
      Date today10 = DateUtils.addDays( today, 10 );
      
      rm.bookResources("jawa01", "ozizka", today, today5 );
      rm.bookResources("jawa01", "oskutka", today6, today10 );
      rm.bookResources("jawa02", "ozizka", today, today10 );

      // Write the XML.
      Writer writer = new SoutCopyingFileWriter("target/testStateSave.xml");
      JaxbStatePersister sp = new JaxbStatePersister();
      sp.setWriter(writer);
      //sp.save( cb.extractState() );
            
   }


   static String SAMPLE_STATE_XML = "src/test/resources/JawaBotState-sample.xml";

   public void testJaxbRead() throws JawaBotIOException, JawaBotException, IOException, JAXBException
   {
      JaxbStatePersister sp = new JaxbStatePersister( SAMPLE_STATE_XML );
      StateBean state = sp.load();
      assertEquals( 2, state.reservations.size() );
      assertEquals( "ozizka", state.reservations.get(0).forUser );
      //Calendar date = Calendar.getInstance();
      //date.set(2009, 11-1, 31, 0,0,0);
      //assertTrue( state.reservations.get(0).from.equals(  date.getTime() ) );
      assertEquals( "pslavice", state.reservations.get(1).forUser );
   }

   public void testJaxbReadWrite() throws JawaBotIOException, JawaBotException, IOException, JAXBException
   {
      JaxbStatePersister sp = new JaxbStatePersister( SAMPLE_STATE_XML );
      StateBean state = sp.load();

      sp.setWriter( new SoutCopyingFileWriter("target/testJaxbReadWrite.xml") );
      sp.save( state );

      sp.setFilePath( "target/testJaxbReadWrite.xml" );
      StateBean newState = sp.load();

      assertEquals( state.reservations.size(), newState.reservations.size() );
      ReservationBean resBean = state.reservations.get(0);
      ReservationBean newResBean = newState.reservations.get(0);
      assertEquals( resBean.forUser, newResBean.forUser );
      assertEquals( resBean.from, newResBean.from );
      assertEquals( resBean.to, newResBean.to );
      assertEquals( resBean.resources.size(), newResBean.resources.size() );

   }



}// class

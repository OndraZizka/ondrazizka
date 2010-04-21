
package cz.dynawest.wicket.chat;


import java.io.IOException;
import java.io.InputStream;
import java.util.logging.*;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;


/**
 *
 * @author Ondrej Zizka
 */
public class JTexyResourceStream extends AbstractResourceStream
{
  //private static final Logger log = Logger.getLogger( JTexyResourceStream.class.getName() );

  public InputStream getInputStream() throws ResourceStreamNotFoundException {
    throw new UnsupportedOperationException( "Not supported yet." );
  }


  public void close() throws IOException {
    //
  }

}// class JTexyResourceStream

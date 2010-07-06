
package cz.oz.wicket.stack;


import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;


/**
 *
 * @author Ondrej Zizka
 */
public class Header extends Panel
{
  private static final Logger log = Logger.getLogger( Header.class.getName() );

  public Header( String id ) {
    super( id );

    int viewCount = 7; // TODO

    // Sobota 8. května '10
    add( new Label("datum", new SimpleDateFormat("EEEE d. MMM ''y", new Locale( "cs_CZ", "cz")).format( new Date() )) );
    add( new Label("svatek", "Svátek má Ctibor, zítra Hermus") ); // TODO
    add( new Label("counter", "" + viewCount ) );
  }



}// class Header

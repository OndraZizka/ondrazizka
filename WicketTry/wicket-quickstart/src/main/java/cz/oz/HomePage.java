
package cz.oz;


import java.util.logging.*;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;


/**
 *
 * @author Ondrej Zizka
 */
public class HomePage extends BaseLayoutPage
{
  private static final Logger log = Logger.getLogger( HomePage.class.getName() );

  public HomePage( PageParameters parameters ) {
    super( parameters );

    add( new Label( "content", "Obsah clanku.") );
  }


}// class HomePage

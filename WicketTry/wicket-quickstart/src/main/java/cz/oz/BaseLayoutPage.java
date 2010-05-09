
package cz.oz;


import java.util.logging.*;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.include.Include;


/**
 *
 * @author Ondrej Zizka
 */
public class BaseLayoutPage extends WebPage
{
  private static final Logger log = Logger.getLogger( BaseLayoutPage.class.getName() );

  public BaseLayoutPage( PageParameters parameters ) {
    super( parameters );

    add( new Header( "header" ) );
    //add( new Header( "content" )); // The rest is done in the inherited classes.

    add( new MenuPanel( "menu" ) );
    add( new Include( "donate", "static/DonatePanel.html" ) );
    add( new Include( "counters", "static/CountersPanel.html" ) );
  }


  public WicketApplication getApp(){
    return (WicketApplication) this.getApplication();
  }

  


}// class BaseLayoutPage

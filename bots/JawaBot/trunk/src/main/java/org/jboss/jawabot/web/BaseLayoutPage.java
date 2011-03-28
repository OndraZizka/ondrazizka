
package org.jboss.jawabot.web;


import java.util.logging.*;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;


/**
 *
 * @author Ondrej Zizka
 */
public class BaseLayoutPage extends WebPage
{
  private static final Logger log = Logger.getLogger( BaseLayoutPage.class.getName() );

  public BaseLayoutPage( PageParameters parameters ) {
    super( parameters );

    //add( new Header( "content" )); // The rest is done in the inherited classes.

    add( new MenuPanel( "menu" ) );
  }


  public WicketApplication getApp(){
    return (WicketApplication) this.getApplication();
  }


  public JawaBotSession getSession(){
     return (JawaBotSession) super.getSession();
  }
  


}// class BaseLayoutPage

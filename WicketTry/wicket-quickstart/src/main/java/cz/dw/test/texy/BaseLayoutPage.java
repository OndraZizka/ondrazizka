
package cz.dw.test.texy;


import java.util.*;
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

    add( new Header( "header" ));
    //add( new Header( "content" )); // The rest is done in the inherited classes.
  }

  


}// class BaseLayoutPage

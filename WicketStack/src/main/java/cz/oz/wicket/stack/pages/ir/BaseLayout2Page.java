
package cz.oz.wicket.stack.pages.ir;


import cz.oz.wicket.stack.StackApp;
import cz.oz.wicket.stack.pages.BaseLayoutPage;
import java.util.logging.*;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;


/**
 *
 * @author Ondrej Zizka
 */
public class BaseLayout2Page extends WebPage
{
  private static final Logger log = Logger.getLogger( BaseLayoutPage.class.getName() );

  public BaseLayout2Page( PageParameters parameters ) {
    super( parameters );

  }


  public StackApp getApp(){
    return (StackApp) this.getApplication();
  }

  


}// class BaseLayoutPage

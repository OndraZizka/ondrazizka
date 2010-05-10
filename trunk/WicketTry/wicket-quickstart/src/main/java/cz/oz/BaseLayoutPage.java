
package cz.oz;


import java.io.File;
import java.util.logging.*;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.include.Include;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;


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
    //add( new Panel( "donate", new Model<String>( FileUtils.readFileToString(new File("./static/DonatePanel.html")) ) ) );
    //add( new Panel( "counters", "./static/CountersPanel.html" ) );
    add( new DonatePanel("donate") );
    add( new CountersPanel("counters") );
  }


  public WicketApplication getApp(){
    return (WicketApplication) this.getApplication();
  }

  


}// class BaseLayoutPage

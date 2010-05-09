package cz.oz;

import cz.dynawest.jtexy.TexyException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.protocol.http.WebApplication;
import org.quartz.Scheduler;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see cz.dw.test.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{
  private static final Logger log = Logger.getLogger( WicketApplication.class.getName() );


  private JTexyContentProvider jtexyProvider;
  public JTexyContentProvider getJtexyProvider() {    return jtexyProvider;  }


  /**
   * Constructor
   */
  public WicketApplication() {
  }


  // Init
  @Override protected void init() {
    System.out.println( "---- init() ----" );

		//http://localhost:8080/wicket/stranky/test/foo.texy?dw:path=xxx
		mount( new PathUrlCodingStrategy("stranky", ShowPathPage.class));

    String jtexyStorePath = this.getInitParameter("jtexyStorePath");
    try {
      this.jtexyProvider = new JTexyContentProvider( jtexyStorePath );
    }
    catch( TexyException ex ) {
      log.log( Level.SEVERE, null, ex );
    }
  }




  // Shutdown
  @Override protected void onDestroy() {
    System.out.println( "---- onDestroy() ----" );
  }




  /**
   * @see org.apache.wicket.Application#getHomePage()
   */
  public Class getHomePage() {
    return HomePage.class;
  }


}

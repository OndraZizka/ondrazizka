package cz.oz.wicket.stack;

import cz.oz.*;
import cz.dw.test.pages.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see cz.dw.test.Start#main(String[])
 */
public class StackApp extends WebApplication
{
  private static final Logger log = Logger.getLogger( StackApp.class.getName() );

  
  // Spring Application Context
  private static ApplicationContext ac;
  public static ApplicationContext getAppContext(){ return ac; }
  public static Object getBean(String name) { return ac.getBean(name); }

  // Options
  private final Properties options = new Properties();
  public Properties getOptions() { return options; }
  

  /**
   * Constructor
   */
  public StackApp() {
  }


  // Init
  @Override protected void init() {
    System.out.println( "---- Wicket init() ----" );

    String sProfile = null;

    // -- Spring Bean Factory -- //

    // Pokud je nastavený parametr profile, pošleme jej do Spring konfigurace.
    if( null != sProfile )
      System.setProperty( "profile", sProfile );

    ac = new ClassPathXmlApplicationContext("spring/SpringBeans.xml");


    // Options
    this.options.putAll( (Properties)ac.getBean( "properties" ) );

    // DAO factory
    //this.daoFactory = (DaoFactory) this.ac.getBean("daoFactory");


    // -- Wicket stuff -- //

		//mount( new PathUrlCodingStrategy("page1", Page1.class));

  }




  // Shutdown
  @Override protected void onDestroy() {
    System.out.println( "---- Wicket onDestroy() ----" );
  }




  /**
   * @see org.apache.wicket.Application#getHomePage()
   */
  public Class getHomePage() {
    return HomePage.class;
  }


}

package cz.oz.wicket.stack;

import cz.oz.wicket.stack.pages.home.HomePage;
import cz.oz.*;
import cz.oz.wicket.stack.dao.DaoFactory;
import cz.dynawest.logging.LoggingUtils;
import cz.oz.wicket.stack.pages.ir.BaseLayout2Page;
import cz.oz.wicket.stack.pages.form.FormPage;
//import cz.oz.wicket.stack.pages.hbn.HibernateStatsPage;
import cz.oz.wicket.stack.pages.i18n.TranslatedPage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.BookmarkablePageRequestTargetUrlCodingStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see cz.dw.test.Start#main(String[])
 */
public class StackApp extends WebApplication
{
  private static Logger log = Logger.getLogger( StackApp.class.getName() );

  
  // Spring Application Context
  private static ApplicationContext ac;
  public static ApplicationContext getAppContext(){ return ac; }
  public static Object getBean(String name) { return ac.getBean(name); }

  // Options
  private final Properties options = new Properties();
  public Properties getOptions() { return options; }
  
  private static final Properties APP_PARAMS = new Properties();
  
  // DAO factory
  private static DaoFactory daoFactory;
  public static DaoFactory getDaoFactory() {    return daoFactory;  }
  


  /**
   * Constructor
   */
  public StackApp() {
  }


  // Init
  @Override protected void init() {
    System.out.println( "---- Wicket init() ----" );

    String sProfile = "default";

    initLogging( sProfile );

    // -- Spring Bean Factory -- //

    // Pokud je nastavený parametr profile, pošleme jej do Spring konfigurace.
    if( null != sProfile )
      System.setProperty( "profile", sProfile );

    ac = new ClassPathXmlApplicationContext("spring/SpringBeans.xml");


    // Options
    this.options.putAll( (Properties)ac.getBean( "properties" ) );

    // DAO factory
    this.daoFactory = (DaoFactory) this.ac.getBean("daoFactory");


    // -- Wicket stuff -- //

		mount( new BookmarkablePageRequestTargetUrlCodingStrategy( "form", FormPage.class, null));
		//mount( new BookmarkablePageRequestTargetUrlCodingStrategy( "hbn", HibernateStatsPage.class, null));
		mount( new BookmarkablePageRequestTargetUrlCodingStrategy( "i18n", TranslatedPage.class, null));
		mount( new BookmarkablePageRequestTargetUrlCodingStrategy( "new", BaseLayout2Page.class, null));

  }




  /** init() helper. */
  private void loadConfig()
  {
    // Který soubor načíst?  Defaultně "nastaveni.properties".
    String sConfigFile = "settings.properties";
    // Zkusíme načíst ze systémových proměnných (nastaveno např. přes -D...=...).
    sConfigFile = System.getProperty( "application.config.file", sConfigFile );
    // Parametry cmdline mají absolutní přednost.
    //sConfigFile = this.applicationParams.getProperty( "application.config.file", sConfigFile );

    try {
      log.info("Načítám konfiguraci ze souboru "+sConfigFile+" ...");
      this.options.load( new FileInputStream( sConfigFile ) );
    }
    catch( FileNotFoundException ex ) {
      log.log( Level.WARNING,
              "Konfigurační soubor nebyl nalezen: "+sConfigFile, ex );
    }
    catch( IOException ex ) {
      log.log( Level.WARNING,
              "Chyba při čtení konfiguračního souboru souboru: "+sConfigFile, ex );
    }
  }


  /** init() helper. */
  private String determineProfile()
  {
    String sProfile = System.getenv("profile");
		String sProfileSetBy = "envvar 'profile'";
    if( null == sProfile ){
      sProfile = this.APP_PARAMS.getProperty( "profile" );
			sProfileSetBy = "app param '--profile <profile-name>'";
    }
    if( null == sProfile ){
      sProfile = this.options.getProperty( "profile" );
			sProfileSetBy = "app option 'profile'";
    }
    System.out.println( "Zvolený profil: "+ (sProfile == null ? "(žádný)" : sProfile) );
		System.out.println( "Nastaven z: "+sProfileSetBy );

    return sProfile;
  }


  /** init() helper. */
  private void initLogging( String sProfile )
  {
    /*
    String logConfigFile = System.getProperty("java.util.logging.config.file", "conf/"+sProfile+"/logging.properties");
    try {
			log.info("Načítám konfiguraci logování ze souboru: "+logConfigFile);
			log.info("(nastaveno v systémové proměnné java.util.logging.config.file)");
      LogManager.getLogManager().readConfiguration(new FileInputStream(logConfigFile));
    }catch(IOException ex){
      System.out.println("Chyba při načítání nastavení logování ze souboru ["+logConfigFile+"]. Bude použito výchozí.");
    }
     */
    // TODO: Use this instead.
    LoggingUtils.initLogging( "conf/"+sProfile+"/logging.properties" );

    log = Logger.getLogger(StackApp.class.getName());
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

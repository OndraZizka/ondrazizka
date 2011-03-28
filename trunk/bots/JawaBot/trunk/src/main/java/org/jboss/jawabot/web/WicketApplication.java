package org.jboss.jawabot.web;

import cz.dynawest.wicket.NonVersionedHybridUrlCodingStrategy;
import java.util.logging.Logger;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebSession;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see cz.dw.test.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{
   private static final Logger log = Logger.getLogger( WicketApplication.class.getName() );


   /**
   * Constructor
   */
   public WicketApplication() {
   }


   /**
   * @see org.apache.wicket.Application#getHomePage()
   */
   @Override  public Class getHomePage() {
      return HomePage.class;
   }


   // Init
   @Override
   protected void init() {
      System.out.println("---- init() ----");
      mountBookmarkablePage("res",   ResourcePage.class);
      mountBookmarkablePage("take",  TakePage.class);
      mountBookmarkablePage("leave", LeavePage.class);
      //mountBookmarkablePage("login", LoginPage.class);
      mount(new NonVersionedHybridUrlCodingStrategy("login", LoginPage.class));
   }


  // Shutdown
  @Override protected void onDestroy() {
    System.out.println( "---- onDestroy() ----" );
  }


   @Override
   public Session newSession(Request request, Response response) {
      return new JawaBotSession(request);
   }

}

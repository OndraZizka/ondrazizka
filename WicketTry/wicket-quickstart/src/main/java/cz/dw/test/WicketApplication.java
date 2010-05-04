package cz.dw.test;

import cz.dw.test.texy.ShowPathPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.quartz.Scheduler;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see cz.dw.test.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{

  Scheduler scheduler;

  /**
   * Constructor
   */
  public WicketApplication() {
  }

  // Init
  @Override
  protected void init() {
    System.out.println( "---- init() ----" );
  }


  // Shutdown
  @Override
  protected void onDestroy() {
    System.out.println( "---- onDestroy() ----" );
  }




  /**
   * @see org.apache.wicket.Application#getHomePage()
   */
  public Class getHomePage() {
    //return HomePage.class;
    //return Page1.class;
    //return GuestBook.class;
    //return CreatePerson.class;
    return ShowPathPage.class;
  }


}
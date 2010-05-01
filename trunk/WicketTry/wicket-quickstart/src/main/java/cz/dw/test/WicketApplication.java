package cz.dw.test;

import cz.dw.test.gb.GuestBook;
import cz.dw.test.listinlist.CreatePerson;
import cz.dw.test.pages.Page1;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see cz.dw.test.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{

  /**
   * Constructor
   */
  public WicketApplication() {
  }


  /**
   * @see org.apache.wicket.Application#getHomePage()
   */
  public Class getHomePage() {
    //return HomePage.class;
    //return Page1.class;
    //return GuestBook.class;
    //return CreatePerson.class;
    return CreatePerson.class;
  }


}

package cz.dw.test;

import cz.dw.test.listinlist.CreatePerson;
import org.apache.wicket.protocol.http.WebApplication;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

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
    try{
      this.scheduler = StdSchedulerFactory.getDefaultScheduler();
      scheduler.start();
      JobDetail job = new JobDetail("job1", "group1", MyJobClass.class);
      Trigger trigger = new SimpleTrigger("trigger1", "group1", 10, 1000*2);
      scheduler.scheduleJob(job, trigger);
    } catch( SchedulerException se ) {
        se.printStackTrace();
    }
  }


  // Shutdown
  @Override
  protected void onDestroy() {
    System.out.println( "---- onDestroy() ----" );
    try {
      scheduler.shutdown();
    }
    catch( SchedulerException ex ) {
      ex.printStackTrace();
    }
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

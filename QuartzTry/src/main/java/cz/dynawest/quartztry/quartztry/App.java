package cz.dynawest.quartztry.quartztry;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Hello world!
 *
 */
public class App 
{
  public static void main( String[] args ) throws InterruptedException
  {
    System.out.println( "Hello World!" );

    try {
      // Grab the Scheduler instance from the Factory.
      Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
      scheduler.start();

      // Define job metadata.
      JobDetail job = new JobDetail("job1", "group1", MyJobClass.class);

      // Define a Trigger that will fire "now"
      //Trigger trigger = new SimpleTrigger("trigger1", "group1", new Date());
      // Or this one will repeat 10 times after 100 ms.
      Trigger trigger = new SimpleTrigger("trigger1", "group1", 100, 10);

      // Schedule the job with the trigger
      scheduler.scheduleJob(job, trigger);

      Thread.sleep( 15000 );

      // Shutdown
      scheduler.shutdown();

    } catch( SchedulerException se ) {
        se.printStackTrace();
    }


  }
}



package cz.dynawest.quartztry.quartztry;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Ondrej Zizka
 */
public class MyJobClass implements Job {

	private static final Logger log = LoggerFactory.getLogger(MyJobClass.class);


  private static int counter = 0;
  private static int inst = 0;

  public MyJobClass() {
    //System.out.println( "INSTANTIATED "+ ++inst +" times!" );
		log.info("  INSTANTIATED "+ ++inst +" times!");
  }

  public void execute( JobExecutionContext context ) throws JobExecutionException {
		try { Thread.sleep(2500); } catch (InterruptedException ex) { }
    log.info("  EXECUTED "+ ++counter +" times!");
  }
}
// class MyJobClass

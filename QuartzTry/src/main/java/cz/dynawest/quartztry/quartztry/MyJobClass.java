
package cz.dynawest.quartztry.quartztry;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**
 *
 * @author Ondrej Zizka
 */
public class MyJobClass implements Job {

  private static int counter = 0;
  private static int inst = 0;

  public MyJobClass() {
    System.out.println( "INSTANTIATED "+ ++inst +" times!" );
  }

  public void execute( JobExecutionContext context ) throws JobExecutionException {
    System.out.println( "EXECUTED "+ ++counter +" times!" );
    //throw new UnsupportedOperationException("WTF.");
  }
}
// class MyJobClass


package cz.oz.wicket.stack.jmx;


import java.util.logging.*;


/**
 *
 * @author Ondrej Zizka
 */
public class JmxTestMBeanImpl implements JmxTestMBean
{
  private static final Logger log = Logger.getLogger( JmxTestMBeanImpl.class.getName() );


  private int usersTotal;
  @Override public int getUsersTotal() {    return usersTotal;  }
  @Override public void setUsersTotal( int usersTotal ) {    this.usersTotal = usersTotal;  }

  
  public void sayHello(){

    System.out.println( "*** sayHello() called. ***" );

  }

  public static void main(String[] args){
    new JmxTestMBeanImpl().sayHello();
  }


}// class JmxTestMBeanImpl

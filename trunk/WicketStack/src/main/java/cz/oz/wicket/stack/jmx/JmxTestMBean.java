
package cz.oz.wicket.stack.jmx;

import javax.swing.JToolTip;

/**
 *
 * @author Administrator
 */
public interface JmxTestMBean {

    public int getUsersTotal();
    public void setUsersTotal( int usersTotal );


    public void sayHello();

}// class JmxTestMBean

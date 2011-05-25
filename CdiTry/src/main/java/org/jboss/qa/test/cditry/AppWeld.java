package org.jboss.qa.test.cditry;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 *
 * @author Ondrej Zizka
 */
public class AppWeld {
   public static void main(String[] args) {
      WeldContainer weld = new Weld().initialize();
      App myApp = weld.instance().select(App.class).get();
      myApp.main(args);
   }
}// class

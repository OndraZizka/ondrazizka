package org.jboss.qa.test.cditry;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 *
 * @author Ondrej Zizka
 */
public class AppWeld {
		
	 public final static WeldContainer WELD = new Weld().initialize();
	 
	 
   public static void main(String[] args) {
      App myApp = WELD.instance().select(App.class).get();
      myApp.main(args);
   }
}// class

package org.jboss.qa.test.cditry.decorator;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 *  Demonstration of @Decorator and @Delegate.
 */
@Singleton
public class App 
{
		static WeldContainer wc;
		
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
				wc = new Weld().initialize();
				wc.instance().select(App.class).get().main2(args);
    }
		
		// This should get injected by above code line.
		@Inject MyFoo myFoo;

		private void main2(String[] args) {
				myFoo.doFoo();
		}

}// class

interface IFoo {
		public void doFoo();
}

class MyFoo implements IFoo {
		public void doFoo() {
				System.out.println("MyFoo not overriden.");
		}
}

@Decorator
class MyFooDelegate implements IFoo {
		@Inject @Delegate IFoo foo;

		public void doFoo() {
				System.out.println("Oh, MyFoo was really overriden!");
		}
}
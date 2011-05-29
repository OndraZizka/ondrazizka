package org.jboss.qa.test.cditry.threads;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.se.contexts.ThreadScoped;

/**
 *  Demonstration of @ThreadScoped.
 *  MyService should get a new MyBean for each thread.
 *  App creates several MyThreads, which inject MyService and call it's quack,
 *  which should return different message
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
		
		@Inject MyThread mt1;
		@Inject MyThread mt2;

		private void main2(String[] args) {
				//new Thread( new MyThread(), "thread A" ).start();
				//new Thread( new MyThread(), "thread B" ).start();
				/*Instance<MyThread> in = wc.instance().select(MyThread.class, Foo.class.getAnnotation(Foo.class));
				MyThread t1 = in.get();
				MyThread t2 = in.get();
				 */
				new Thread( mt1, "thread A" ).start();
				new Thread( mt2, "thread B" ).start();
		}

}


@Qualifier @Retention(RetentionPolicy.RUNTIME) @Inherited @interface Foo {}
@Qualifier @Retention(RetentionPolicy.RUNTIME) @Inherited @interface NonFoo {}

/*
class MyThreadProducer {
		@Produces @NonFoo MyThread mt;
		@Inject void init(){
				this.mt = new MyThread();
		}
}
 */


//@Default
@Foo
class MyThread implements Runnable {
		
		@Inject MyService myService;
		
		@Override
		public void run() {
				System.out.println("  Thread " + Thread.currentThread().getName() + " / " +  myService.quack()  );
		}
}


@ApplicationScoped
class MyService {
		
		@Inject MyBean myBean;
		
		public String quack(){
			//System.out.println("  Thread " + Thread.currentThread().getName() + " has this MyBean: " + this.myBean );
			return "MyBean #" + myBean.hashCode();
		}
}


@ThreadScoped
class MyBean {
}
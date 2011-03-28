
package org.jboss.jawabot.web;

import org.apache.wicket.application.ReloadingClassLoader;
import org.apache.wicket.protocol.http.ReloadingWicketServlet;
import org.apache.wicket.protocol.http.WicketServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import org.mortbay.jetty.Handler;
import org.apache.wicket.protocol.http.WicketFilter;
import org.mortbay.jetty.handler.HandlerWrapper;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHandler;


/**
 * 
		<init-param>
			<param-name>applicationClassName</param-name>
			<param-value>cz.dw.test.WicketApplication</param-value>
		</init-param>
 * 
 * @author Ondrej Zizka
 */
public class RunInJetty {

	public static void main( String[] args ){
      run();
   }

   public static void run()
   {

		Server server = new Server(8080);
		Context ctx = new Context( server, "/", Context.NO_SECURITY | Context.SESSIONS );

      // Static content.
      /* I think the piece of the puzzle you are missing is that your
      static content is not being served. You need to add a servlet
      that will serve the html, images etc for you: */
      ServletHolder defaultSH = new ServletHolder(new org.mortbay.jetty.servlet.DefaultServlet());
      defaultSH.setInitParameter("resourceBase", "./src/main/java/org/jboss/jawabot/web/files");
      ctx.addServlet(defaultSH, "/css/*");
      ctx.addServlet(defaultSH, "/js/*");


      // Wicket.
		final ServletHolder wicketSH = new ServletHolder(new MyReloadingWicketServlet());
		wicketSH.setInitParameter("applicationClassName", WicketApplication.class.getName() );
      ctx.addServlet( wicketSH, "/*" );

      
      /*/
      FilterHolder filterHolder = new FilterHolder( new WicketFilter() );
      filterHolder.setInitParameter("applicationClassName", cz.dw.test.WicketApplication.class.getName() );
      root.addFilter( filterHolder, "/*" , Handler.ALL );
      /**/


      // Another approach.
      /* Does not work, because ResourceHandler can't chain the previous handlers - not a HandlerContainer.
      ResourceHandler resourceHandler = new ResourceHandler();
      resourceHandler.setResourceBase("./src/main/java/org/jboss/jawabot/web/files");
      //HandlerWrapper handlerWrapper = new HandlerWrapper();
      //handlerWrapper.setHandler( resourceHandler );
      //ctx.addHandler( handlerWrapper );
      ctx.addHandler( resourceHandler );
      ctx.addHandler(new ServletHandler(){{ addServlet(wicketSH); }});
      /**/

		
		try {
			server.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}// class


class MyReloadingWicketServlet extends ReloadingWicketServlet {
   static
 	{
 		ReloadingClassLoader.excludePattern("org.apache.wicket.*");
 		ReloadingClassLoader.includePattern("org.jboss.jawabot.*");
 	}
}
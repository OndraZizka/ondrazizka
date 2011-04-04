
package org.jboss.jawabot.web;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.application.ReloadingClassLoader;
import org.apache.wicket.protocol.http.ReloadingWicketServlet;
import org.apache.wicket.protocol.http.WicketServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.*;



/**
 * 
		<init-param>
			<param-name>applicationClassName</param-name>
			<param-value>cz.dw.test.WicketApplication</param-value>
		</init-param>
 * 
 * @author Ondrej Zizka
 */
public class RunInJetty
{
   private static final Logger log = Logger.getLogger(RunInJetty.class);

   
   public static void main( String[] args ){
      run();
   }

   public static void run()
   {

      Server server = new Server( 8080 );
      Context ctx = new Context( server, "/", Context.NO_SECURITY | Context.SESSIONS );

      
      
      // Static content.
      
      /* I think the piece of the puzzle you are missing is that your
      static content is not being served. You need to add a servlet
      that will serve the html, images etc for you: */
      ServletHolder defaultSH = new ServletHolder( new org.mortbay.jetty.servlet.DefaultServlet() );
      
      
      String staticDirPath = System.getProperty( "jawabot.web.staticFiles");
      File staticDir;
      if( null != staticDirPath ){
         log.debug("  Using env var jawabot.web.staticFiles .");
         staticDir = new File(staticDirPath);
      }
      else {
         // Use classes path as default
         URL staticDirURL = RunInJetty.class.getResource("/org/jboss/jawabot/web/RunInJetty.class");
         try {
            staticDir = new File( staticDirURL.toURI() );
         } catch( URISyntaxException ex ) {
            log.error("  Can't read a file from URI: " + staticDirURL );
            staticDir = new File("");
         }
      }
      
      String staticDirCanonPath = "";
      try {
         staticDirCanonPath = staticDir.getCanonicalPath();
      } catch (IOException ex) {
         log.error("  Non-existent path: " + staticDir.getPath());
      }
      
      log.info("  Static files are in: "
              + "\n        " + staticDir.getAbsolutePath()
              + "\n   ==   " + StringUtils.defaultIfEmpty(staticDirCanonPath, "non-existent path") );

      defaultSH.setInitParameter( "resourceBase", staticDirCanonPath );
      ctx.addServlet( defaultSH, "/css/*" );
      ctx.addServlet( defaultSH, "/img/*" );
      ctx.addServlet( defaultSH, "/js/*" );
      ctx.addServlet( defaultSH, "/favicon.ico" );


      
      
      // Wicket.
      final ServletHolder wicketSH = new ServletHolder( new MyReloadingWicketServlet() );
      wicketSH.setInitParameter( "applicationClassName", WicketApplication.class.getName() );
      ctx.addServlet( wicketSH, "/*" );
      //ctx.setAttribute( JawaBotApp.ID.JAWABOT, JawaBotApp.getJawaBot() );


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
      } catch ( Exception ex ) {
         ex.printStackTrace();
      }

   }

}// class



class MyReloadingWicketServlet extends ReloadingWicketServlet 
{
    static {
        ReloadingClassLoader.excludePattern( "org.apache.wicket.*" );
        ReloadingClassLoader.includePattern( "org.jboss.jawabot.*" );
    }
}
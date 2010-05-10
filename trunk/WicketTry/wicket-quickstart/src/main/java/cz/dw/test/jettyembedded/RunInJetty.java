
package cz.dw.test.jettyembedded;

import org.apache.wicket.protocol.http.WicketServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import org.mortbay.jetty.Handler;
import org.apache.wicket.protocol.http.WicketFilter;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.FilterHolder;


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

		Server server = new Server(8080);
		Context ctx = new Context( server, "/", Context.SESSIONS );
    //ctx.setResourceBase("./");

    ServletHolder defaultSH = new ServletHolder( new DefaultServlet() );
    defaultSH.setInitParameter( "resourceBase", "./src/main/webapp");
    ctx.addServlet( defaultSH, "/css/*" );
    ctx.addServlet( defaultSH, "/img/*" );
    ctx.addServlet( defaultSH, "/js/*" );

		/**/
		ServletHolder servletHolder = new ServletHolder(new WicketServlet());
		servletHolder.setInitParameter("applicationClassName", cz.oz.WicketApplication.class.getName() );
		servletHolder.setInitParameter("jtexyStorePath", "d:\\web\\ondra.zizka.cz\\real\\new\\stranky\\" );
		ctx.addServlet( servletHolder, "/*" );
    /**/

    /*/
    FilterHolder filterHolder = new FilterHolder( new WicketFilter() );
    filterHolder.setInitParameter("applicationClassName", cz.dw.test.WicketApplication.class.getName() );
    ctx.addFilter( filterHolder, "/*" , Handler.ALL );
    /**/
    
		try {
			server.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}// class

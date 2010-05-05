
package cz.dw.test.jettyembedded;

import org.apache.wicket.protocol.http.WicketServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

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
		Context root = new Context( server, "/", Context.SESSIONS );


		/**/
		ServletHolder servletHolder = new ServletHolder(new WicketServlet());
		servletHolder.setInitParameter("applicationClassName", cz.dw.test.WicketApplication.class.getName() );
		root.addServlet( servletHolder, "/*" );
		/**/

		/*/
		FilterHolder filterHolder = new FilterHolder( new WicketFilter() );
		filterHolder.setInitParameter("applicationClassName", cz.dw.test.WicketApplication.class.getName() );
		root.addFilter( filterHolder, "/*" , Handler.ALL );
		/**/
		
		try {
			server.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}// class


package cz.poh.web.newdesign;


import java.io.Serializable;
import java.util.logging.*;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;


/**
 *
 * @author Ondrej Zizka
 */
public class BasePageLayout3 extends WebPage implements Serializable
{
  private static final Logger log = Logger.getLogger( BasePageLayout3.class.getName() );

		public BasePageLayout3() {
				super();

				add( new HledaniPanel("hledaniPanel") );
		}




  


}// class BasePageLayout

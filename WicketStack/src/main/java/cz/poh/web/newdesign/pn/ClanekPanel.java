
package cz.poh.web.newdesign.pn;

import cz.poh.web.newdesign.model.Clanek;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author ondra
 */
public final class ClanekPanel extends Panel
{

    public ClanekPanel( String id, Clanek clanek ) {
        super(id);

				add( new Label("label", clanek.getLabel() ));

				add( new Label("htmlContent", clanek.getText() ).setEscapeModelStrings(false) );
    }


		/** Poor man's HTML validation. */
		public static boolean validateHtml( String htmlCode ){

				int diff = StringUtils.countMatches( htmlCode , "<")  - StringUtils.countMatches( htmlCode , ">");
				if( diff != 0) return false;

				if( htmlCode.matches("<[^>]<"))  return false;
				if( htmlCode.matches(">[^<]>"))  return false;

				return true;
		}

}// class Clanek

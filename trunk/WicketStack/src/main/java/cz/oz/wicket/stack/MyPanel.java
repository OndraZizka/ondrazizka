
package cz.oz.wicket.stack;

import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author ondra
 */
public class MyPanel extends Panel
{

    public MyPanel( String id ) {
        super(id);
    }

		public StackApp getApp(){
				return (StackApp) this.getApplication();
		}

}// class MyPanel

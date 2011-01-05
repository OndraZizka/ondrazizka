
package cz.poh.web.newdesign.pg.hp;

import cz.poh.web.newdesign.BasePageLayout3;
import cz.poh.web.newdesign.pn.HledaniPanel;
import cz.poh.web.newdesign.pn.PersonalMenuPanel;
import cz.poh.web.newdesign.pn.UnloggedBottomMenuPanel;


/**
 *
 * @author Ondrej Zizka
 */
public class HomePage extends BasePageLayout3 {


		private String testContent = "This is a test content.";



		public HomePage() {

				// Content

				add( new HledaniPanel("hledaniPanel").setRenderBodyOnly(true) );

				add( new AboutAtHomePagePanel("aboutPanel").setRenderBodyOnly(true) );
				
				add( new NejnovejsiZmenyPanel("nejnovejsiZmeny").setRenderBodyOnly(true) );

				add( new NejnovejsiClanekPanel("nejnovejsiClanek").setRenderBodyOnly(true) );

				add( new StavServeruPanel("stavServeru").setRenderBodyOnly(true) );


				// Sidebar

				add( new PersonalMenuPanel("personalMenu").setRenderBodyOnly(true) );

				add( new UnloggedBottomMenuPanel("unloggedBottomMenu").setRenderBodyOnly(true) );

		}


		// <editor-fold defaultstate="collapsed" desc="get/set">
		public String getTestContent() {
				return testContent;
		}

		public void setTestContent(String testContent) {
				this.testContent = testContent;
		}// </editor-fold>


}// class

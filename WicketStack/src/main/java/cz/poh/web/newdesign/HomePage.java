
package cz.poh.web.newdesign;


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
				
				add( new NejnovejsiZmeny("nejnovejsiZmeny").setRenderBodyOnly(true) );

				add( new NejnovejsiZmeny("nejnovejsiClanek").setRenderBodyOnly(true) );

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

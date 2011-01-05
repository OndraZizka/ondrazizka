
package cz.poh.web.newdesign;


/**
 *
 * @author Ondrej Zizka
 */
public class HomePage extends BasePageLayout3 {


		private String testContent = "This is a test content.";



		public HomePage() {

				add( new HledaniPanel("hledaniPanel") );

		}


		// <editor-fold defaultstate="collapsed" desc="get/set">
		public String getTestContent() {
				return testContent;
		}

		public void setTestContent(String testContent) {
				this.testContent = testContent;
		}// </editor-fold>


}// class

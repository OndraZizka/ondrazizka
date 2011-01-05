
package cz.poh.web.newdesign;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author Ondrej Zizka
 */
public class HomePage extends BasePageLayout3 {


		private String testContent = "This is a test content.";



		public HomePage() {

				add(new Label("testContent", new PropertyModel<String>(this, "testContent")));


		}


		public String getTestContent() {				return testContent;		}
		public void setTestContent(String testContent) {				this.testContent = testContent;		}



}// class


package cz.dw.test.texy;


import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;


/**
 *
 * @author Ondrej Zizka
 */
public class ShowPathPage extends WebPage
{

  private String path = "someInitialValue.texy";
	public String getContentPath() {		return path;	}
	public void setContentPath(String path) {		this.path = path;	}
 


  public ShowPathPage() {

		if( null == getPageParameters() ){
			this.path = "No page parameters.";
		}
		else{
			this.path = getPageParameters().getString(PathUrlCodingStrategy.PATH_PARAM_NAME, "'dw:path' param not set.");
		}
    add( new Label( "path", new PropertyModel<String>(this, "contentPath")) );
		
  }



}// class ShowPathPage


package cz.oz;


import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;


/**
 *
 * @author Ondrej Zizka
 */
public class ShowPathPage extends BaseLayoutPage
{

  private String path = "someInitialValue.texy";
	public String getTexyPath() {		return path;	}
	public void setTexyPath(String path) {		this.path = path;	}




  public ShowPathPage(PageParameters pageParams) {
    super( pageParams );

		this.path = pageParams.getString("dw:path");

    add( new Label( "contentFilePath", new PropertyModel<String>(this, "path")) );

    add( new Label( "content", "Content" ) );

  }



}// class ShowPathPage


package cz.oz;


import cz.dynawest.jtexy.TexyException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;


/**
 *
 * @author Ondrej Zizka
 */
public class ShowPathPage extends BaseLayoutPage
{

  private String texyPath = "someInitialValue.texy";
	public String getTexyPath() {		return texyPath;	}
	public void setTexyPath(String path) {		this.texyPath = path;	}



  /**
   * Const
   */
  public ShowPathPage(PageParameters pageParams) {
    super( pageParams );

		this.texyPath = pageParams.getString("dw:path");

    String texyContent;
    try {
      texyContent = getApp().getJtexyProvider().getContent( texyPath );
    }
    catch( Exception ex ) {
      Logger.getLogger( ShowPathPage.class.getName() ).log( Level.SEVERE, null, ex );
      texyContent = "Can't get the content: "+ex.getMessage();
    }


    add( new Label( "contentFilePath", new PropertyModel<String>(this, "texyPath")) );

    add( new Label( "content", texyContent ).setEscapeModelStrings(false) );

  }



}// class ShowPathPage

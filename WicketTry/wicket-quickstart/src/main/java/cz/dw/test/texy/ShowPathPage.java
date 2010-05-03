
package cz.dw.test.texy;


import java.util.logging.*;
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

  public ShowPathPage() {
    add( new Label( "path", new PropertyModel<String>(this, "path")) );
  }
 

}// class ShowPathPage

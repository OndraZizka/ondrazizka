
package cz.oz.wicket.stack.pages.form;


import cz.oz.wicket.stack.dao.TestEntityDao;
import cz.oz.*;
import cz.oz.wicket.stack.StackApp;
import cz.oz.wicket.stack.pages.BaseLayoutPage;
import java.util.Arrays;
import java.util.logging.*;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;


/**
 *
 * @author Ondrej Zizka
 */
public class FormPage extends BaseLayoutPage
{
  private static final Logger log = Logger.getLogger( FormPage.class.getName() );



  public enum Choice { ONE, TWO, THREE }

  class FormData{
    public String text;
    public String longText;
    public String number;
    public boolean checkbox;
    public Choice choice;
  }


  private FormData data = new FormData();


  public FormPage( PageParameters parameters ) {
    super( parameters );


    TestEntityDao dao = StackApp.getDaoFactory().getTestEntityDao();

    //add( new Label( "content", "Obsah clanku.") );
    add( new Form("form", new CompoundPropertyModel( this.data ))
      .add( new TextField("text") )
      .add( new TextArea( "longText") )
      .add( new TextField("number").add(new RangeValidator(0L, 5L)) )
      .add( new CheckBox("checkbox"))
      .add( new DropDownChoice("choice",
         Arrays.asList( Choice.values() ), new EnumChoiceRenderer() )
       )
    );
  }


}// class FormPage

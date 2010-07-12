
package cz.oz.wicket.stack.pages.form;


import cz.oz.wicket.stack.dao.TestEntityDao;
import cz.oz.*;
import cz.oz.wicket.stack.StackApp;
import cz.oz.wicket.stack.pages.BaseLayoutPage;
import java.util.Arrays;
import java.util.logging.*;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
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
    public String text = "This is text.";
    public String longText = "This is long text.";
    public long number = 4;
    public boolean checkbox = true;
    public Choice choice = Choice.TWO;
  }


  private FormData data = new FormData();


  public FormPage( PageParameters parameters ) {
    super( parameters );


    TestEntityDao dao = StackApp.getDaoFactory().getTestEntityDao();

    //add( new Label( "content", "Obsah clanku.") );

    add( new FeedbackPanel("feedback") );

    Form form = new Form("form", new CompoundPropertyModel( this.data )){
      @Override protected void onSubmit() {
        info("Ool Korrect.");
      }
      @Override protected void onError() {
        error("Nay!");
      }
    };
    add( form );
      form.add( new TextField("text") );
      form.add( new TextArea("longText") );
      form.add( new TextField("number").add(new RangeValidator(0L, 5L)) );

      final DropDownChoice ddChoice = 
        new DropDownChoice("choice", Arrays.asList( Choice.values() ), new EnumChoiceRenderer(this) );
      ddChoice.setOutputMarkupId(true);

      form.add( new AjaxCheckBox("checkbox"){
        @Override
        protected void onUpdate( AjaxRequestTarget target ) {
          target.addComponent( ddChoice );
          ddChoice.setEnabled( this.getModelObject() );
        }
      });
      
      form.add( ddChoice );
    
  }


}// class FormPage

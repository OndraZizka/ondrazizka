
package cz.oz.wicket.stack.pages.home;


import cz.dw.wicket.stack.dao.TestEntityDao;
import cz.oz.*;
import cz.oz.wicket.stack.StackApp;
import cz.oz.wicket.stack.pages.BaseLayoutPage;
import java.util.logging.*;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;


/**
 *
 * @author Ondrej Zizka
 */
public class HomePage extends BaseLayoutPage
{
  private static final Logger log = Logger.getLogger( HomePage.class.getName() );

  public HomePage( PageParameters parameters ) {
    super( parameters );


    TestEntityDao dao = StackApp.getDaoFactory().getTestEntityDao();

    //add( new Label( "content", "Obsah clanku.") );
    add( new RepeatingView("content") )
      .add( new Label( "1", "Obsah clanku.") )
      .add( new Label( "2", dao.createSyntheticTestEntity().getValue() ) )
      .add( new Label( "value", new CompoundPropertyModel<TestEntityDao>( dao.createSyntheticTestEntity() ) ) );
    ;
  }


}// class HomePage

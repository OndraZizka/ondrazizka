
package cz.dw.test.ddmenu;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

/**
 *
 * @author Ondøej Žižka
 */
public class ListPersons extends WebPage {

  public ListPersons() {

    add( new ListView<Person>("personsList", Person.getPersons() ) {
      @Override
      protected void populateItem( ListItem<Person> item ) {
        item.add( new Label( "jmeno", item.getModelObject().getJmeno() ));
        item.add( new Label( "prijmeni", item.getModelObject().getPrijmeni() ));
      }
    });

  }

}// class ListPersons

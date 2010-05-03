
package cz.dw.test.listinlist;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

/**
 *
 * @author Ondrej Zizka
 */
public class ListPersons extends WebPage {

  public ListPersons() {

    add( new ListView<Person>("personsList", Person.getPersons() ) {
      @Override
      protected void populateItem( ListItem<Person> item ) {
        item.add( new Label( "jmeno", item.getModelObject().getJmeno() ));
        item.add( new Label( "prijmeni", item.getModelObject().getPrijmeni() ));
        item.add( new ListView<Person>( "children", item.getModelObject().getChildren() ) {
          @Override
          protected void populateItem( ListItem<Person> item ) {
            item.add( new Label( "ch_jmeno", item.getModelObject().getJmeno() ));
            item.add( new Label( "ch_prijmeni", item.getModelObject().getPrijmeni() ));
          }
        });
      }
    });

  }

}// class ListPersons

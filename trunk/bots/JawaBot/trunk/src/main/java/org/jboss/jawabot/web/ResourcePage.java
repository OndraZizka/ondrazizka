
package org.jboss.jawabot.web;


import cz.dynawest.util.DateUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.*;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.ReservationWrap;
import org.jboss.jawabot.Resource;


/**
 *
 * @author Ondrej Zizka
 */
public class ResourcePage extends BaseLayoutPage
{
  private static final Logger log = Logger.getLogger( HomePage.class.getName() );

  public ResourcePage( PageParameters parameters ) {
     super(parameters);
     
     // Resource
     Resource resource = new Resource("jawa05", "foo");

     add( new Label("res", resource.getName()) );

     List<String> groups = new ArrayList();
     groups.add( resource.getProject() );

     add(new ListView<ResourcePage>("groups", new ListModel( groups ) ) {
        @Override protected void populateItem(ListItem<ResourcePage> item) {
           item.add(new Label("group", item.getModelObject().toString() ));
        }
     });

     // Reservations.
     List<ReservationWrap> resvs = new ArrayList(3);
     resvs.add( new ReservationWrap("jawa01", "ozizka", new Date(2010, 05, 18), new Date(2010, 05, 20) ));
     resvs.add( new ReservationWrap("jawa02", "ozizka", new Date(2010, 05, 20), new Date(2010, 05, 25) ));
     resvs.add( new ReservationWrap("jawa13", "jbossqa", new Date(2010, 05, 20), new Date(2025, 05, 20) ));

     add(new ListView<ReservationWrap>("reservations", new ListModel<ReservationWrap>( resvs ) ) {
        @Override protected void populateItem(ListItem<ReservationWrap> item) {
           ReservationWrap res = item.getModelObject();
           item.add(new Label("res", res.getResourceName() ));
           item.add(new Label("owner", res.getForUser() ));
           item.add(new Label("from", DateUtils.toStringSQL( res.getFrom() ) ));
           item.add(new Label("to",   DateUtils.toStringSQL( res.getTo()   ) ));
           item.add(new Label("note", "" ));
        }
     });
  }


}// class HomePage

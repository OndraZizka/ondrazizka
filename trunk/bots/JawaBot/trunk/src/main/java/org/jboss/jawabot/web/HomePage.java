
package org.jboss.jawabot.web;


import cz.dynawest.util.DateUtils;
import cz.dynawest.wicket.BookmarkablePageLink;
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
public class HomePage extends BaseLayoutPage
{
  private static final Logger log = Logger.getLogger( HomePage.class.getName() );

  public HomePage( PageParameters parameters ) {
     super(parameters);

     // Free resources.
     List<Resource> freeResources = new ArrayList(3);
     freeResources.add( new Resource("jawa05", "eap") );
     freeResources.add( new Resource("jawa06", "eap") );
     freeResources.add( new Resource("jawa07", "soa") );

     add(new ListView<Resource>("freeResources", new ListModel( freeResources ) ) {
        @Override protected void populateItem(ListItem<Resource> item) {
           item.add(
               new BookmarkablePageLink("link", ResourcePage.class)
               .setParameter("name", item.getModelObject().getName())
               .add( new Label("text", item.getModelObject().toString() ))
           );
        }
     });

     // Reservations.
     List<ReservationWrap> resvs = new ArrayList(3);
     resvs.add( new ReservationWrap("jawa01", "ozizka", new Date(), new Date(2010, 05, 20) ));
     resvs.add( new ReservationWrap("jawa02", "ozizka", new Date(2010, 05, 20), new Date(2010, 05, 20) ));
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

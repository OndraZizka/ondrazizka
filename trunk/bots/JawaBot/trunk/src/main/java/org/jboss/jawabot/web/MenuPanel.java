
package org.jboss.jawabot.web;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.Resource;


/**
 *
 * @author Ondrej Zizka
 */
public class MenuPanel extends Panel
{
  private static final Logger log = Logger.getLogger( MenuPanel.class.getName() );

  public MenuPanel( String id ) {
      super( id );

      add( new WebMarkupContainer("userNotLogged"){
            @Override public boolean isVisible() { return !isUserLogged(); }
      });
      add( new WebMarkupContainer("userLogged"){
            @Override public boolean isVisible() { return isUserLogged(); }
         }
         .add( new Label("loggedUser", new PropertyModel(getSession(), "loggedUser")){
            @Override public boolean isVisible() { return isUserLogged(); }
         })
      );
      
      // Free resources.
      List<Resource> freeResources = new ArrayList(3);
      freeResources.add( new Resource("jawa05", "foo") );
      freeResources.add( new Resource("jawa06", "foo") );
      freeResources.add( new Resource("jawa07", "bar") );

      add(new ListView<Resource>("resourceList", new ListModel( freeResources ) ) {
        @Override protected void populateItem(ListItem<Resource> item) {
           item.add( new BookmarkablePageLink("link", ResourcePage.class)
                     .setParameter("name", item.getModelObject().getName())
                     .add( new Label("name", item.getModelObject().getName()) )
           );
        }
      });

  }

   @Override public JawaBotSession getSession(){ return (JawaBotSession) super.getSession(); }
   private boolean isUserLogged(){ return null != getSession().getLoggedUser(); }

}// class MenuPanel

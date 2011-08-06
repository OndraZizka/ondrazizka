
package org.jboss.jawabot.web._co.menu;


import org.jboss.jawabot.web._base.ConveniencePageBase;
import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.util.ListModel;
import org.jboss.jawabot.FromJawaBot;
import org.jboss.jawabot.Resource;
import org.jboss.jawabot.ResourceManager;
import org.jboss.jawabot.plugin.pastebin.JpaPasteBinManager;
import org.jboss.jawabot.plugin.pastebin.ent.PasteBinEntry;
import org.jboss.jawabot.state.ent.Group;
import org.jboss.jawabot.web.JawaBotSession;
import org.jboss.jawabot.web._co.GroupLinkPanel;
import org.jboss.jawabot.web._co.PastebinLinkPanel;
import org.jboss.jawabot.web._co.ResourceLinkPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Ondrej Zizka
 */
public class MenuPanel extends Panel
{
   private static final Logger log = LoggerFactory.getLogger( MenuPanel.class );
   
   
   @Inject private JpaPasteBinManager pbManager;
   
   @Inject @FromJawaBot private ResourceManager resourceManager;

   
   public MenuPanel( String id ) {
      super( id );
   }

   
   @Override
   protected void onInitialize() {
      super.onInitialize();
      
      // User box - MenuBox test.
      add( new MenuBoxPanel( "accountBox", "Account", new AccountBoxPanel("content") ) );


      // Resources.
      
      //List<Resource> resources = ((ConveniencePageBase)getPage()).getJawaBot().getResourceManager().getResourcesWithNoReservations();
      List<Resource> resources = this.resourceManager.getResourcesWithNoReservations();

      add(new ListView<Resource>("resourceList", new ListModel( resources ) ) {
        @Override protected void populateItem(ListItem<Resource> item) {
           item.add( new ResourceLinkPanel("link", item.getModelObject()));
        }
      });

      
      // Groups.
      
      //List<Group> groups = ((ConveniencePageBase)getPage()).getJawaBot().getGroupManager().getAllGroups_OrderByName();
      //List<Group> groups = this.groupManager.getAllGroups_OrderByName();
      
      /*add(new ListView<Group>("groupList", new ListModel( groups ) ) {
        @Override protected void populateItem(ListItem<Group> item) {
           item.add( new GroupLinkPanel("link", item.getModelObject()));
        }
      });/**/
      
      
      // PasteBin
      /*add( new Link("new_paste", new Model("") ) {
            public void onClick() {
                this.setResponsePage( PasteBinPage.class );
            }
        } );*/
      
      // ((ConveniencePageBase)getPage()).getJawaBot().getPasteBinManager()
      List<PasteBinEntry> pastebins = pbManager.getLastPastes_OrderByWhenDesc(10);
      
      add(new ListView<PasteBinEntry>("pastebinList", new ListModel( pastebins ) ) {
        @Override protected void populateItem(ListItem<PasteBinEntry> item) {
           item.add( new PastebinLinkPanel("link", item.getModelObject()));
        }
      });


   }// onInitialize()

  

  @Override public JawaBotSession getSession(){ return (JawaBotSession) super.getSession(); } // TODO: CDI.
  
  private boolean isUserLogged(){ return null != getSession().getLoggedUser(); }

   @Override
   protected void onDetach() {
      this.resourceManager = null;
      super.onDetach();
   }
  
  

}// class MenuPanel

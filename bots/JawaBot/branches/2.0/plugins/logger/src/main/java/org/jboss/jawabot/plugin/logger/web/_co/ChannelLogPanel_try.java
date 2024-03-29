
package org.jboss.jawabot.plugin.logger.web._co;

import com.google.common.collect.AbstractIterator;
import java.awt.Color;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.inject.Inject;
import org.apache.commons.lang.time.DateUtils;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.datetime.DateConverter;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.jawabot.irc.ent.IrcEvAction;
import org.jboss.jawabot.irc.ent.IrcEvJoin;
import org.jboss.jawabot.irc.ent.IrcEvMessage;
import org.jboss.jawabot.irc.ent.IrcEvNickChange;
import org.jboss.jawabot.irc.ent.IrcEvPart;
import org.jboss.jawabot.irc.ent.IrcEvTopic;
import org.jboss.jawabot.irc.ent.IrcEvent;
import org.jboss.jawabot.plugin.irc.Channel;
import org.jboss.jawabot.plugin.logger.bus.ChannelLogManager;
import org.jboss.jawabot.plugin.logger.irc.IrcEventCriteria;
import org.jboss.jawabot.plugin.logger.web.IrcEventCriteriaLDM;

/**
 *  Panel with a table showing logs.
 *  @author Ondrej Zizka
 * 
 *  TODO: The way I instantiate LDM smells. Perhaps I should avoid 
 *        making it a CDI bean and set it's EntityManager manually?
 */
public class ChannelLogPanel_try extends Panel {
   
   @Inject private ChannelLogManager channelLogManager;
   
   @Inject IrcEventCriteriaLDM model;
   
   // Criteria for this log panel - which messages should be listed.
   IrcEventCriteria crit;

   private static NickToColor nickToColor = new NickToColor();

   private static final AttributeAppender MSG_CLASS_APP    = new AttributeAppender("class", new Model( "msg" ), " ");
   private static final AttributeAppender JOIN_CLASS_APP   = new AttributeAppender("class", new Model( "join" ), " ");
   private static final AttributeAppender PART_CLASS_APP   = new AttributeAppender("class", new Model( "part" ), " ");
   private static final AttributeAppender ACTION_CLASS_APP = new AttributeAppender("class", new Model( "action" ), " ");
   private static final AttributeAppender TOPIC_CLASS_APP  = new AttributeAppender("class", new Model( "topic" ), " ");
   private static final AttributeAppender NICK_CHANGE_CLASS_APP = new AttributeAppender("class", new Model( "nick" ), " ");
   private static final AbstractBehavior NOOP_BEHAV = new AbstractBehavior() {}; //AttributeModifier("non-existent", new Model());
   
   
   public ChannelLogPanel_try( String id, Channel ch ) {
      super( id );
      this.crit = new IrcEventCriteria(ch.getName(), DateUtils.addDays(new Date(), -1), new Date());
      this.model.setCrit( crit );
      super.setDefaultModel( this.model );
   }

   
   
   @Override
   protected void onInitialize() {
        super.onInitialize();
        
        IrcEventCriteriaLDM ldm = (IrcEventCriteriaLDM) this.getDefaultModel();
        //ListModel lm = new ListModel( ldm.getObject() );
        
        final DateConverter dcDate = new PatternDateConverter("yyyy-MM-dd", false);
        final DateConverter dcTime = new PatternDateConverter("hh:mm:ss", false);
        
        // Ajax table try.
        /*IColumn[] columns = new IColumn[1];
        columns[0] = new AbstractColumn(this.model) {
            public void populateItem( Item cellItem, String componentId, IModel rowModel ) {
            }
        };
        ISortableDataProvider<IrcEvent> dataProvider = new IrcEventsDataProvider( this.channelLogManager, this.crit );
        int totalCount = this.channelLogManager.getEventsCountByCriteria(crit);
        add( new AjaxFallbackDefaultDataTable("events", columns, dataProvider, totalCount) );
        /**/

                
        // Plain table
        add( new ListView<IrcEvent>("events", ldm ) {
           int lastDay = -1;
           @Override protected void populateItem( ListItem<IrcEvent> item ) {
              IrcEvent ev = item.getModelObject();
              
              // Delimiter
              item.add( new WebMarkupContainer("delimiter")
                  .add(new DateLabel("day", new Model(ev.getWhen()), dcDate ))
                  //.add(new Label("day", new Model("Day " + ev.getWhen()) ))
                  .setVisibilityAllowed( lastDay != ev.getWhen().getDate() )
              );
              
              // Event

              // Nick color.
              // Perhaps, add the color to a CSS class and set class="" here?
              String nickStyle = "color: #" + nickToColor.get( ev.getUser() );
              IBehavior nickColor = new AttributeAppender("style", new Model( nickStyle ), "; ");
              
              boolean isMsg = ev instanceof IrcEvMessage;
              
              // Value of <tr class="...">
              IBehavior classApp = NOOP_BEHAV;
              if( ev instanceof IrcEvMessage )    classApp = MSG_CLASS_APP;
              if( ev instanceof IrcEvJoin )       classApp = JOIN_CLASS_APP;
              if( ev instanceof IrcEvPart )       classApp = PART_CLASS_APP;
              if( ev instanceof IrcEvAction )     classApp = ACTION_CLASS_APP;
              if( ev instanceof IrcEvTopic )      classApp = TOPIC_CLASS_APP;
              if( ev instanceof IrcEvNickChange ) classApp = NICK_CHANGE_CLASS_APP;
              
              item.add( new WebMarkupContainer("event", null)
                  // Time
                  .add(new DateLabel("when", new Model(ev.getWhen()), dcTime ))
                  // Nick
                  .add(new Label("nick", ev.getUser() + (isMsg ? ": " : "") )
                      // For message, only colorize nick; or text too for other events.
                      .add( ev instanceof IrcEvMessage ? nickColor : NOOP_BEHAV )
                  )
                  // Text
                  .add(new Label("text", ChannelLogPanel_try.formatEventText(ev) ))
                  .add( classApp )
              );
              lastDay = ev.getWhen().getDate();
           }
        });

   }
   
   
   /**
    *  Text for the text part of row.
    */
   private static String formatEventText( IrcEvent ev ){
       if( ev instanceof IrcEvJoin )
           return " has joined (" + ev.getText() + ")";
       if( ev instanceof IrcEvPart )
           return " has left (" + ev.getText() + ")";
       if( ev instanceof IrcEvNickChange )
           return " has changed nick to (" + ((IrcEvNickChange)ev).getNewNick() + ")";
       return ev.getText();
   }
   
   
   /**
    *  Keeps a mappng of nick -> color.
    *  These colors are derived from the string, i.e. not random.
    */
   static class NickToColor {
       final ConcurrentMap<String, String> nickToColorCache = new ConcurrentHashMap<String, String>();
       
       public String get( String nick ){
            String color = nickToColorCache.get( nick );
            if( null != color ) return color;
            nickToColorCache.put( nick, color = getDarkishColorAsHex(nick) );
            return color;
       }

       /**
        *   Returns some darker (bri 0.3-0.6) color based on the object's hashCode.
        *   Intended for reproducible (non-random) IRC nick -> color conversion.
        */
       private static String getDarkishColorAsHex( Object o ){
            int hashCode = o.hashCode();
            //long unsignedHashCode = ((long)hashCode) - Integer.MIN_VALUE;
            //float hue = 2 * (-Integer.MIN_VALUE) / unsignedHashCode;
            float hue = ((float)o.hashCode()) % 1024f / 1024f;
            int bri1 = Math.abs(hashCode) % 16;
            float bri2 = 0.0f + bri1;
            float bri3 = bri2 / 16.0f;
            float bri = 0.65f + bri3 * 0.3f;
            Color color = Color.getHSBColor( hue, 1.0f, bri );
            int rgb = color.getRGB();
            int rgb2 =  rgb & 0xFFFFFF;
            return String.format("%x", rgb2 );
       }
   }

   // TODO: Move to a test.
   public static void main( String args[] ){
       NickToColor.getDarkishColorAsHex("JawaBot");
   }
   
}



/**
 *   Trying to implement Facebook-like "More" content-appending behavior.
 *   For ajax table try.
 *   @author ondra
 *   @deprecated  Try failed.
 */
class IrcEventsDataProvider implements ISortableDataProvider<IrcEvent> {
    
    ChannelLogManager manager;
    IrcEventCriteria crit;

    public IrcEventsDataProvider( ChannelLogManager manager, IrcEventCriteria crit ) {
        this.manager = manager;
        this.crit = crit;
    }
    

    /**
     *  @returns an iterator over given range of events conforming criteria of this DataProvider.
     */
    @Override
    public Iterator<? extends IrcEvent> iterator( final int first, final int count ) {
        //crit.setFirst(first);
        //crit.setCount(count);
        return new AbstractIterator<IrcEvent>() {
            List<IrcEvent> events = manager.getEventsByCriteria( crit, first, count, false );
            Iterator<IrcEvent> it = events.listIterator();
            protected IrcEvent computeNext() {
                if( ! it.hasNext() )  return this.endOfData();
                return it.next();
            }
        };
    }

    @Override
    public int size() {
        return manager.getEventsCountByCriteria( crit );
    }

    @Override
    public IModel<IrcEvent> model( IrcEvent ev ) {
        return new Model(ev);
    }

    @Override
    public void detach() {
    }

    /** We don't care about sorting. */
    @Override
    public ISortState getSortState() {
        return new ISortState() {
            public void setPropertySortOrder(String property, int state) {}
            public int getPropertySortOrder(String property) { return 0; }
        };
    }
    public void setSortState(ISortState state) {  }
}

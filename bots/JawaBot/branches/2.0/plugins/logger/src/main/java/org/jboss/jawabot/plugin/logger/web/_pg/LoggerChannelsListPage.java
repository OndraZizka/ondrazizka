package org.jboss.jawabot.plugin.logger.web._pg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.panel.Panel;
import org.jboss.jawabot.plugin.logger.web._co.LoggedChannelsListPanel;
import org.jboss.jawabot.plugin.logger.web._menu.LoggerMenuPanel;
import org.jboss.jawabot.web._base.BaseLayoutPage;

/**
 *  List of logged channels. 
 * 
 *  @author Ondrej Zizka
 */
public class LoggerChannelsListPage extends BaseLayoutPage {
    private static final Logger log = LoggerFactory.getLogger(LoggerChannelsListPage.class);

    
    public LoggerChannelsListPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        add( new LoggedChannelsListPanel("channelList") );
    }// onInitialize()
    
    
    @Override
    public Panel getMenuPanel(String id) {
        return new LoggerMenuPanel(id);
    }

    
    
}// class LoggerChannelsListPage

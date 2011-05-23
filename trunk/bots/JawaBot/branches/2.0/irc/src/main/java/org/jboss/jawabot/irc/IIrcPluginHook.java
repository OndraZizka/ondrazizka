package org.jboss.jawabot.irc;

import cz.dynawest.util.plugin.IPluginLifeCycle;
import org.jboss.jawabot.irc.model.IrcMessage;

/**
 *  
 *  @author Ondrej Zizka
 */
public interface IIrcPluginHook <TInitObject extends Object> extends IPluginLifeCycle <TInitObject> {
   
   public void onMessage( IrcMessage message ) throws IrcPluginException;
   
}// class

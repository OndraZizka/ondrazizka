
package cz.dynawest.wicket.chat;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

/**
 *
 * @author Ondrej Zizka
 */
public class ChatWicketApp extends WebApplication {

	@Override
	public Class<? extends Page> getHomePage() {
		return ChatPage.class;
	}

}// class

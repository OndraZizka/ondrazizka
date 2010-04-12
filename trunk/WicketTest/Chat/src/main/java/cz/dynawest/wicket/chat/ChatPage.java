
package cz.dynawest.wicket.chat;

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

/**
 *
 * @author Ondrej Zizka
 */
public class ChatPage extends WebPage {

	private List<String> messages = new ArrayList();

	public ChatPage() {

		this.add( new ListView("messages", this.messages) {
			@Override protected void populateItem(ListItem item) {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		});

	}

}// class

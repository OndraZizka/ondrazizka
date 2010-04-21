
package cz.dynawest.wicket.chat;

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.RepeatingView;

/**
 *
 * @author Ondrej Zizka
 */
public class ChatPage extends WebPage {

	private List<String> messages = new ArrayList();

	public ChatPage() {

    messages.add("Uh... hi.");
    messages.add("Korben Dallas!");

    // ListView
		this.add( new ListView("messagesList", this.messages) {
			@Override protected void populateItem(ListItem item) {
				item.add( new Label("foo"+Math.random(), "FooBar") );
			}
		});/**/


    // RepeatingView
		RepeatingView repeating = new RepeatingView("repeating");
		add(repeating);

    for( String str : messages ){
			WebMarkupContainer item = new WebMarkupContainer(repeating.newChildId());
			repeating.add(item);

      item.add( new Label("message", str) );
    }
    

    //this.add( new Form("form1") );

	}

}// class


package cz.dw.test.gb;


import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;


/**
 *
 * @author Ondrej Zizka
 */
public final class GuestBook extends WebPage
{
    /** Use a Vector, as it is synchronized. */
    private static final List commentList = new Vector();
    private final ListView commentListView;

    public GuestBook()
    {
        add(new CommentForm("commentForm"));
        add(commentListView = new ListView("comments", commentList)
        {
            public void populateItem(final ListItem listItem)
            {
                final Comment comment = (Comment)listItem.getModelObject();
                listItem.add(new Label("date", comment.date.toString()));
                listItem.add(new MultiLineLabel("text", comment.text));
            }
        });
    }

    public final class CommentForm extends Form
    {
        private final Comment comment = new Comment();

        public CommentForm(final String componentName)
        {
            super(componentName);
            add(new TextArea("text", new PropertyModel(comment, "text")));
        }

        public final void onSubmit()
        {
            final Comment newComment = new Comment();
            newComment.text = comment.text;

            commentList.add(0, newComment);
            commentListView.modelChanged();

            comment.text = "";
        }
    }
}
// class GuestBook

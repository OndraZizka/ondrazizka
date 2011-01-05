
package cz.poh.web.newdesign;

import antlr.StringUtils;
import com.mysql.jdbc.EscapeTokenizer;
import cz.poh.web.newdesign.model.CastoHledaneItem;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 *
 * @author ondra
 */
public class HledaniPanel extends Panel
{
		private static Logger log = Logger.getLogger( HledaniPanel.class.getName() );
		

    public HledaniPanel(String id) {
        super (id);

				//RepeatingView castoHledane = new RepeatingView("castoHledane", getCastoHledaneModel() );
				//castoHledane.setRenderBodyOnly(true);
				//castoHledane.add( new CastoHledaneLabel(  ) );

				ListView castoHledane = new ListView("castoHledane", getCastoHledaneList() ) {
						@Override
						protected void populateItem( ListItem item ) {
								CastoHledaneItem obj = (CastoHledaneItem) item.getModelObject();
								//item.add( new BookmarkablePageLink("link", , PageParameters.NULL) )
								String url;
								try {
										url = "http://www.pohlidame.cz/hledani?q=" + URLEncoder.encode(obj.getLabel(), "UTF-8");
										item.add( new ExternalLink("link", url, obj.getLabel() ) );
										item.add( new Label("resCount", String.valueOf(obj.getSearchCount()) + " výsledků" ) );
								} catch (UnsupportedEncodingException ex) {
										log.log(Level.WARNING, "Can't escape URL: "+ex.getMessage(), ex.toString());
								}
						}
				};

				add( castoHledane );
    }


		private IModel getCastoHledaneModel(){
				List castoHledane = getCastoHledaneList();

				return new AbstractReadOnlyModel() {
						@Override
						public Object getObject() {
								return HledaniPanel.this.getCastoHledaneModel();
						}
				};
		}// getCastoHledaneModel()

		
		private List<CastoHledaneItem> getCastoHledaneList(){
				return Arrays.asList( new CastoHledaneItem[]{
					 new CastoHledaneItem("crystalex", 1)
					,new CastoHledaneItem("Bihári", 17)
					,new CastoHledaneItem("testujme.cz", 1)
				} );
		}



}// class





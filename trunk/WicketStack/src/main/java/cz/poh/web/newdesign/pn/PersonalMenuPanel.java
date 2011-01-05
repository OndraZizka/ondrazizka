
package cz.poh.web.newdesign.pn;

import cz.oz.wicket.stack.MyPanel;
import java.util.List;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;


/**
 *
 * @author ondra
 */
public final class PersonalMenuPanel extends MyPanel
{

    public PersonalMenuPanel( String id ) {
        super(id);

				List<Object> rychleOdkazy = this.getRychleOdkazy();

				ListView view = new ListView("rychleOdkazy", rychleOdkazy) {
						@Override
						protected void populateItem( ListItem item ){
								Object entity = item.getModelObject();
								LinkableInMenu metaData = transform( entity );
						}
				};
				add( view );
    }

		private List<Object> getRychleOdkazy() {
				//this.getApp().getDaoFactory().
				throw new UnsupportedOperationException("Not yet implemented.");
		}


		public interface LinkableInMenu {
				public String getUrl();
				public String getLabel();
				public String getDetail();
				public String getTypeLabel();
		}

		private static LinkableInMenu transform(Object entity) {
				//if( entity instanceof Vzor )

				return new LinkableInMenu() {
						@Override public String getTypeLabel() { return "Vzor";	}
						@Override public String getLabel() { return "Label*";	}
						@Override public String getUrl() { return "http://foobar";		}
						@Override public String getDetail() { return "26 nalezených";		}
				};

		}


}// class PersonalMenu


package cz.poh.web.newdesign.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ondrej Zizka
 */
public class CastoHledane {

		
		List<CastoHledaneItem> items = new ArrayList();



		public void setItems(List<CastoHledaneItem> items) {
				this.items = items;
		}


		
		public void addItem( String label, int searchCount ){
				this.items.add( new CastoHledaneItem(label, searchCount) );
		}


}// class

package cz.dw.test.pages;

import org.apache.wicket.markup.html.WebPage;

/**
 *
 * @author Ondøej Žižka
 */
public class Page1 extends WebPage {

  public Page1() {
    NavomaticBorder nb = new NavomaticBorder( "navomaticBorder" );
    nb.add( new org.apache.wicket.markup.html.border.BoxBorder( "shit2" ) );
    add( nb );

    add( new org.apache.wicket.markup.html.border.Border( "shit1" ) {

    } );
  }


}

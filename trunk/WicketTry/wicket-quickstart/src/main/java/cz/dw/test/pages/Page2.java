
package cz.dw.test.pages;

import org.apache.wicket.markup.html.WebPage;


/**
 *
 * @author Ondrej Zizka
 */
public class Page2 extends WebPage
{
    public Page2()
    {
        add(new NavomaticBorder("navomaticBorder"));
    }
}

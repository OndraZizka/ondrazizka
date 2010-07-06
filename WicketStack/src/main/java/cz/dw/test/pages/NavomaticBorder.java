
package cz.dw.test.pages;

import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.border.BoxBorder;



/**
 *
 * @author Ondrej Zizka
 */
public class NavomaticBorder extends Border
{
    public NavomaticBorder(final String componentName)
    {
        super(componentName);
        add(new BoxBorder("navigationBorder"));
        add(new BoxBorder("bodyBorder"));
    }
}
// class NavomaticBorder

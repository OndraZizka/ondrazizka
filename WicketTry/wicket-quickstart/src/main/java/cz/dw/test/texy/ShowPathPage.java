
package cz.dw.test.texy;


import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;


/**
 *
 * @author Ondrej Zizka
 */
public class ShowPathPage extends WebPage
{

  private String pathD = "someInitialValue.texy";
	public String getPathD() {		return pathD;	}
	public void setPathD(String path) {		this.pathD = path;	}
 
  private String pathX = "someInitialValue.texy";
  private String pathDP = "someInitialValue.texy";
  private String pathXP = "someInitialValue.texy";
	public String getPathDP() {		return pathDP;	}
	public void setPathDP(String pathDP) {		this.pathDP = pathDP;	}
	public String getPathX() {		return pathX;	}
	public void setPathX(String pathX) {		this.pathX = pathX;	}
	public String getPathXP() {		return pathXP;	}
	public void setPathXP(String pathXP) {		this.pathXP = pathXP;	}




  public ShowPathPage(PageParameters pageParams) {

		add( new NavigationPanel("navigation") );

		// TODO: Try QueryStringUrlCodingStrategy, whether it will give me some params.
		/*if( null == getPageParameters() ){
			this.path = "No page parameters.";
		}
		else{
			this.path = getPageParameters().getString(PathUrlCodingStrategyWrong.PATH_PARAM_NAME, "'dw:path' param not set.");
		}*/

		this.pathD = getRequest().getParameter(PathUrlCodingStrategyWrong.PATH_PARAM_NAME);
		this.pathX = getRequest().getParameter("xw:path");
		this.pathDP = pageParams.getString("dw:path");
		this.pathXP = pageParams.getString("xw:path");

    add( new Label( "dw-path", new PropertyModel<String>(this, "pathD")) );
    add( new Label( "xw-path", new PropertyModel<String>(this, "pathX")) );
    add( new Label( "dw-path-page", new PropertyModel<String>(this, "pathDP")) );
    add( new Label( "xw-path-page", new PropertyModel<String>(this, "pathXP")) );

  }



}// class ShowPathPage

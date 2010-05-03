
package cz.dw.test.texy;


import java.util.Map;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.PageParameters;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.coding.QueryStringUrlCodingStrategy;
import org.apache.wicket.request.target.component.BookmarkableListenerInterfaceRequestTarget;
import org.apache.wicket.request.target.component.BookmarkablePageRequestTarget;
import org.apache.wicket.util.string.AppendingStringBuffer;


/**
 *
 * @author Ondrej Zizka
 */
public class PathUrlCodingStrategy extends QueryStringUrlCodingStrategy
{

  private final static String SUFFIX = "texy";
  private final static String PATH_PARAM_NAME = "dw:path";
  

  public PathUrlCodingStrategy( String mountPath, Class bookmarkablePageClass ) {
    super( mountPath, bookmarkablePageClass );
  }


	/**
	 * Append the parameters to the end of the URL.
	 *
	 * @param url
	 *            the relative reference URL
	 * @param parameters
	 *            parameter names mapped to parameter values
   * 
   * @see BookmarkablePageRequestTargetUrlCodingStrategy#encode().
	 */
	@Override
	protected void appendParameters(AppendingStringBuffer url, Map parameters)
	{
    // Is this a hack, or is it an elegant Wicket way?
    // I would rather override BookmarkablePageRequestTargetUrlCodingStrategy#encode().
		if (parameters != null && parameters.size() > 0)
		{
       // Find dw:path. If present, remove, and append the path to the URL.
       Object val = parameters.remove( PathUrlCodingStrategy.PATH_PARAM_NAME );
       if( val != null ){
         String path = val.toString();
         url.append( path );
       }
		}
    url.append('?');
    super.appendParameters( url, parameters );
	}

  

  // Should I override this?
  @Override
  protected String urlEncodePathComponent( String string ) {
    return super.urlEncodePathComponent( string );
  }





  /**
   * Decode - extract the path.
   */
	@Override
	public IRequestTarget decode(RequestParameters requestParameters)
	{



    // Extract the path. Modified from PackageRequestTargetUrlCodingStrategy#decode().
		String remainder = requestParameters.getPath().substring(getMountPath().length());
		final String parametersFragment;

    // [...] mountPath/ <some/dir/document.texy> ['?'...]
		int ix = remainder.indexOf("."+SUFFIX, 1);
		if( ix == -1 ){
			ix = remainder.length();
			parametersFragment = "";
		}
		else {
			parametersFragment = remainder.substring( ix + SUFFIX.length() );
		}

    // Remove potential trailing slash.
		if( remainder.startsWith("/") ){
			remainder = remainder.substring(1);
			ix--;
		}

    // ix and reminder are not used below; basically, we just need path and params parts. TODO: Clean up?


    // The params. Taken from QueryStringUrlCodingStrategy#decode().
		String pageMapName = requestParameters.getPageMapName();
		final PageParameters parameters = new PageParameters(requestParameters.getParameters());


    // Add the path to params.
    parameters.put( PathUrlCodingStrategy.PATH_PARAM_NAME, requestParameters.getPath() );


		// This might be a request to a stateless page, so check for an
		// interface name.
		if (requestParameters.getInterfaceName() != null)
		{
			return new BookmarkableListenerInterfaceRequestTarget(pageMapName,
				bookmarkablePageClassRef.get(), parameters, requestParameters.getComponentPath(),
				requestParameters.getInterfaceName(), requestParameters.getVersionNumber());
		}
		else
		{
			return new BookmarkablePageRequestTarget(pageMapName,
				(Class)bookmarkablePageClassRef.get(), parameters);
		}
	}


}// class PathUrlCodingStrategy

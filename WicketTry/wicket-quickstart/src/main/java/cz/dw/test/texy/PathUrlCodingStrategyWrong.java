
package cz.dw.test.texy;


import java.util.Map;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.PageParameters;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import org.apache.wicket.request.target.coding.QueryStringUrlCodingStrategy;
import org.apache.wicket.request.target.component.BookmarkableListenerInterfaceRequestTarget;
import org.apache.wicket.request.target.component.BookmarkablePageRequestTarget;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.value.ValueMap;


/**
 *
 * @author Ondrej Zizka
 */
public class PathUrlCodingStrategyWrong extends QueryStringUrlCodingStrategy implements IRequestTargetUrlCodingStrategy
{

  private static final String SUFFIX = "texy";
  public  static final String PATH_PARAM_NAME = "dw:path";
  

  public PathUrlCodingStrategyWrong( String mountPath, Class bookmarkablePageClass ) {
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
       // Find dw:path. If present, remove it from params, and append the path to the URL.
       Object val = parameters.remove( PathUrlCodingStrategyWrong.PATH_PARAM_NAME );
       if( val != null ){
         String path = val.toString();
         url.append( path );
       }
		}
    url.append('?');
    super.appendParameters( url, parameters );
		// We end up with  "/<path>?<params-without-dw:path>
	}

	@Override
	public IRequestTarget decode(RequestParameters requestParameters) {
		return super.decode(requestParameters);
	}

	@Override
	public boolean matches(IRequestTarget requestTarget) {
		return super.matches(requestTarget);
	}

  




	/**
	 * Decodes parameters object from the provided query string
	 *
	 * @param fragment
	 *            contains the query string
	 * @param passedParameters
	 *            parameters decoded by wicket before this method - usually off the query string
	 *
	 * @return Parameters
	 */
	@Override
	protected ValueMap decodeParameters(String fragment, Map passedParameters)
	{
		ValueMap parameters = new ValueMap();
		if( passedParameters != null ){
			parameters.putAll(passedParameters);
		}
		
		// Put the path to the "dw:path" param. If present, overwrite.
		parameters.put(PATH_PARAM_NAME, fragment);

		return parameters;
	}


}// class PathUrlCodingStrategyWrong

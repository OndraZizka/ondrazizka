
package cz.dw.test.reloading;

import org.apache.wicket.application.ReloadingClassLoader;
import org.apache.wicket.protocol.http.ReloadingWicketFilter;

/**
 *
 * @author Ondrej Zizka
 */
public class MyReloadingWicketFilter extends ReloadingWicketFilter {

	static {
		ReloadingClassLoader.includePattern("cz.dw.*");
	}


}// class

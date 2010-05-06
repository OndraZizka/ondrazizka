
package cz.dw.test.jbosscache;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.SecondLevelCacheSessionStore.IPageStore;
import org.apache.wicket.protocol.http.pagestore.AbstractPageStore;
import org.infinispan.Cache;
import org.infinispan.config.Configuration;
import org.infinispan.manager.CacheManager;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.query.CacheQuery;
import org.infinispan.query.QueryFactory;
import org.infinispan.query.backend.QueryHelper;

/**
 * JBoss Cache / Infinispan implementation of IPageStore.
 * 
 * @author Ondrej Zizka
 */
public class InfinispanPageStore extends AbstractPageStore implements IPageStore {

	private CacheManager manager;
	private Cache cache;
	private QueryFactory qf;

	// -- Const -- //
	public InfinispanPageStore(CacheManager manager, Cache defaultCache) throws IOException
	{
		Configuration cfg = new Configuration();
		cfg.setIndexingEnabled(true);

		this.manager = new DefaultCacheManager( cfg );
		this.cache = manager.getCache();

     // The QueryHelper must be instantiated before putting objects into the cache.
     QueryHelper qh = new QueryHelper(this.cache, new Properties(), PageKey.class);

     // When I want to query objects in the cache, I will create a QueryFactory.
     this.qf = new QueryFactory(this.cache, qh);

	}

	// Page key - takes only the metainfo from the given page.
	private class PageKey extends SerializedPage
	{
		public PageKey(Page page) {
			super(page);
		}
		public PageKey(int pageId, String pageMapName, int versionNumber, int ajaxVersionNumber, byte[] data) {
			super(pageId, pageMapName, versionNumber, ajaxVersionNumber, null);
		}
		public PageKey(SerializedPage page) {
			super(page.getPageId(), page.getPageMapName(), page.getVersionNumber(), page.getAjaxVersionNumber(), null);
		}
	}



	@Override
	public void destroy() {
		this.cache.stop();
		this.cache.clear();
		this.cache = null;
		this.manager = null;
	}

	@Override
	public <T> Page getPage(String sessionId, String pagemap, int id, int versionNumber, int ajaxVersionNumber) {

		if( versionNumber != -1  &&  ajaxVersionNumber  != -1 ){
			PageKey pk = new PageKey(id, pagemap, versionNumber, ajaxVersionNumber, null);
			SerializedPage page = (SerializedPage) this.cache.get( pk );
			return this.deserializePage(page.getData(), versionNumber);
		}

		try {
			// Let's say I'm searching on a field called "name" and looking for "John".
			CacheQuery cq = qf.getBasicQuery("sessionId", "John");

			// Now I can put all my hits into a list!
			List found = cq.list();
		}
		catch (ParseException ex) {
			Logger.getLogger(InfinispanPageStore.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null; // TODO
	}

	
	@Override
	public void pageAccessed(String sessionId, Page page) {
	}

	@Override
	public void removePage(String sessionId, String pagemap, int id) {
	}

	@Override
	public void storePage( String sessionId, Page page ) {
		
		PageKey key = new PageKey(page);

		List<SerializedPage> pages = this.serializePage(page);
		for (SerializedPage serializedPage : pages) {
			PageKey key2 = new PageKey(serializedPage);
			this.cache.put( key2, serializedPage );
		}
	}

	@Override
	public void unbind(String sessionId) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean containsPage(String sessionId, String pageMapName, int pageId, int pageVersion) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}// class

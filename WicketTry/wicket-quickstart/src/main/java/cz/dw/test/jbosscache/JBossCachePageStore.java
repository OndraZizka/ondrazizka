
package cz.dw.test.jbosscache;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.SecondLevelCacheSessionStore.IPageStore;
import org.apache.wicket.protocol.http.pagestore.AbstractPageStore;
import org.infinispan.Cache;
import org.infinispan.config.Configuration;
import org.infinispan.manager.CacheManager;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.query.CacheQuery;
import org.infinispan.query.QueryFactory;
import org.infinispan.query.QueryIterator;
import org.infinispan.query.backend.QueryHelper;
import org.infinispan.tree.TreeCache;
import org.infinispan.tree.TreeCacheFactory;

/**
 * JBoss Cache / Infinispan implementation of IPageStore.
 * 
 * @author Ondrej Zizka
 */
public class JBossCachePageStore extends AbstractPageStore implements IPageStore {

	private CacheManager manager;
	private Cache cache;
	private QueryFactory qf;
	private TreeCacheFactory tcFactory;
	private TreeCache treeCache;

	// -- Const -- //
	public JBossCachePageStore(CacheManager manager, Cache defaultCache) throws IOException
	{
		Configuration cfg = new Configuration();
		cfg.setIndexingEnabled(true);

		this.manager = new DefaultCacheManager( cfg );
		this.cache = manager.getCache();

		// The QueryHelper must be instantiated before putting objects into the cache.
		QueryHelper qh = new QueryHelper(this.cache, new Properties(), PageKey.class);

		// When I want to query objects in the cache, I will create a QueryFactory.
		this.qf = new QueryFactory(this.cache, qh);

		// Tree
		this.tcFactory = new TreeCacheFactory();
		this.treeCache = this.tcFactory.createTreeCache(cache);
	}


	
	// Page key - takes only the metainfo from the given page.
	private class PageKey extends SerializedPage
	{
		private String sessionId;

		public PageKey(String sessionId, Page page) {
			super(page);
			this.sessionId = sessionId;
		}
		public PageKey(String sessionId, int pageId, String pageMapName, int versionNumber, int ajaxVer, byte[] data) {
			super(pageId, pageMapName, versionNumber, ajaxVer, null);
			this.sessionId = sessionId;
		}
		public PageKey(String sessionId, SerializedPage page) {
			super(page.getPageId(), page.getPageMapName(), page.getVersionNumber(), page.getAjaxVersionNumber(), null);
			this.sessionId = sessionId;
		}
	}



	/**
	 * Destroy the store. 
	 */
	@Override
	public void destroy() {
		this.cache.stop();
		this.cache.clear();
		this.cache = null;
		this.manager = null;
	}



	/**
	 * Stores the page to a persistent layer. The page should be stored under the id and the version number.
	 */
	@Override
	public void storePage( String sessionId, Page page ) {

		PageKey key = new PageKey( sessionId, page );

		List<SerializedPage> pages = this.serializePage(page);
		for (SerializedPage serializedPage : pages) {
			PageKey key2 = new PageKey( sessionId, serializedPage );
			this.cache.put( key2, serializedPage );
		}
	}


	/**
			Restores a page version from the persistent layer.

			Note that the versionNumber and ajaxVersionNumber parameters may be -1.

				* If ajaxVersionNumber is -1 and versionNumber is specified,
				  the page store must return the page with highest ajax version.
				* If both versionNumber and ajaxVersioNumber are -1, the pagestore
				  must return last touched (saved) page version with given id.
	 */
	@Override
	public <T> Page getPage( String sessionId, String pagemap, int id, int versionNumber, int ajaxVer ) {

		if( versionNumber != -1  &&  ajaxVer  != -1 ){
			PageKey pk = new PageKey(sessionId, id, pagemap, versionNumber, ajaxVer, null);
			SerializedPage page = (SerializedPage) this.cache.get( pk );
			return this.deserializePage(page.getData(), versionNumber);
		}

		CacheQuery cq = createQuery(sessionId, pagemap, id, versionNumber, ajaxVer, true);
		QueryIterator lazyIterator = cq.lazyIterator(1);
		SerializedPage serPage = (SerializedPage) lazyIterator.next();
		Page page = this.deserializePage(serPage.getData(), serPage.getVersionNumber());

		return page;
	}



	/**
	 *  Create a query according to the given data.
	 * @returns a query object.
	 */
	private CacheQuery createQuery( String sessionId, String pagemap, int id,
					                        int versionNumber, int ajaxVer, boolean doSort ){

		// private final int pageId;
		// private final String pageMapName;
		// private final int versionNumber;
		// private final int ajaxVersionNumber;

		BooleanQuery query = new BooleanQuery();

		query.add(  new TermQuery( new Term("sessionId", sessionId) )  , Occur.MUST);
		query.add(  new TermQuery( new Term("pageId", ""+id) )  , Occur.MUST);
		query.add(  new TermQuery( new Term("pageMapName", pagemap) )  , Occur.MUST);

		Sort sort = null;
		SortField sortAjax = new SortField("ajaxVersionNumber", SortField.INT, true);

		if( versionNumber != -1 ){
			// Sort by ajax only, filter by version number.
			query.add(  new TermQuery( new Term("versionNumber", pagemap) )  , Occur.MUST);
			if(doSort) sort = new Sort( sortAjax );
		} else {
			// Sort by both.
			if(doSort) sort = new Sort( new SortField[]{ new SortField("versionNumber", SortField.INT, true), sortAjax } );
		}


		CacheQuery cq = qf.getQuery( query );
		if( sort != null ) cq.setSort( sort );

		return cq;
	}




	/**
	 * This method is called when the page is accessed.
	 * A IPageStore implementation can block until a save of that page version is done.
	 * So that a specific page version is always restore able.
	 */
	@Override
	public void pageAccessed( String sessionId, Page page ) {
		// We won't block.
	}

	@Override
	public void removePage( String sessionId, String pagemap, int id ) {
		CacheQuery cq = createQuery( sessionId, pagemap, id, -1, -1, false );
		List serPages = cq.list();
		for( Object serPageObj : serPages) {
			this.cache.remove( (SerializedPage) serPageObj );
		}
	}


	/**
	 * The pagestore should cleanup all the pages for that sessionid.
	 */
	@Override
	public void unbind(String sessionId) {
		try {
			CacheQuery cq = qf.getBasicQuery("sessionId", sessionId);
			List serPages = cq.list();
			for (Object serPageObj : serPages) {
				this.cache.remove((SerializedPage) serPageObj);
			}
		}
		catch (ParseException ex) {
			Logger.getLogger(JBossCachePageStore.class.getName()).log(Level.SEVERE, null, ex);
		}
	}


	/**
	 * Returns whether the PageStore contains given page.
	 */
	@Override
	public boolean containsPage(String sessionId, String pageMapName, int pageId, int pageVer) {
		CacheQuery cq = createQuery( sessionId, pageMapName, pageId, pageVer, -1, false);
		return cq.getResultSize() > 0;
	}

}// class

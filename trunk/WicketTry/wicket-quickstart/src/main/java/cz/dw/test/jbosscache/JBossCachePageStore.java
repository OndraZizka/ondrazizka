
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
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.Query;
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
		public PageKey(String sessionId, int pageId, String pageMapName, int versionNumber, int ajaxVersionNumber, byte[] data) {
			super(pageId, pageMapName, versionNumber, ajaxVersionNumber, null);
			this.sessionId = sessionId;
		}
		public PageKey(String sessionId, SerializedPage page) {
			super(page.getPageId(), page.getPageMapName(), page.getVersionNumber(), page.getAjaxVersionNumber(), null);
			this.sessionId = sessionId;
		}
	}



	@Override
	public void destroy() {
		this.cache.stop();
		this.cache.clear();
		this.cache = null;
		this.manager = null;
	}



	/**
	 *
	 * @param <T>
	 * @param sessionId
	 * @param pagemap
	 * @param id
	 * @param versionNumber
	 * @param ajaxVersionNumber
	 * @return
	 */
	@Override
	public <T> Page getPage(String sessionId, String pagemap, int id, int versionNumber, int ajaxVersionNumber) {

		if( versionNumber != -1  &&  ajaxVersionNumber  != -1 ){
			PageKey pk = new PageKey(sessionId, id, pagemap, versionNumber, ajaxVersionNumber, null);
			SerializedPage page = (SerializedPage) this.cache.get( pk );
			return this.deserializePage(page.getData(), versionNumber);
		}

		try {
			// Let's say I'm searching on a field called "name" and looking for "John".
			CacheQuery cq = qf.getBasicQuery("sessionId", "John");

			// private final int pageId;
			// private final String pageMapName;
			// private final int versionNumber;
			// private final int ajaxVersionNumber;

			BooleanQuery query = new BooleanQuery();

			query.add(  new TermQuery( new Term("sessionId", sessionId) )  , Occur.MUST);
			query.add(  new TermQuery( new Term("pageId", ""+id) )  , Occur.MUST);
			query.add(  new TermQuery( new Term("pageMapName", pagemap) )  , Occur.MUST);

			if( versionNumber != -1 ){
				query.add(  new TermQuery( new Term("versionNumber", pagemap) )  , Occur.MUST);
			} else {
				Sort sortV = new Sort( new SortField("versionNumber", SortField.INT, true) );
			}
			Sort sortA = new Sort( new SortField("ajaxVersionNumber", SortField.INT, true) );

			CacheQuery cq2 = qf.getQuery( query );


			// Now I can put all my hits into a list!
			List found = cq.list();
		}
		catch (ParseException ex) {
			Logger.getLogger(JBossCachePageStore.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null; // TODO
	}

	
	@Override
	public void pageAccessed( String sessionId, Page page ) {
	}

	@Override
	public void removePage( String sessionId, String pagemap, int id ) {
	}

	@Override
	public void storePage( String sessionId, Page page ) {
		
		PageKey key = new PageKey( sessionId, page);

		List<SerializedPage> pages = this.serializePage(page);
		for (SerializedPage serializedPage : pages) {
			PageKey key2 = new PageKey( sessionId, serializedPage );
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

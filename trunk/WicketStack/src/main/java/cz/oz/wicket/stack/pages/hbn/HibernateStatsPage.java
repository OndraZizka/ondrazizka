package cz.oz.wicket.stack.pages.hbn;

import cz.oz.wicket.stack.StackApp;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.SessionFactory;
import org.hibernate.impl.SessionImpl;
import org.hibernate.stat.CollectionStatistics;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;

import cz.oz.wicket.stack.pages.BaseLayoutPage;
import javax.persistence.EntityManagerFactory;
import org.hibernate.stat.SessionStatistics;


public class HibernateStatsPage extends BaseLayoutPage {
	
    //@SpringBean(name = "sessionFactory")
    //private SessionFactory sf = StackApp.getBean("emf");

		private EntityManagerFactory emf = (EntityManagerFactory) StackApp.getBean("emf");

		private SessionFactory sf;


    public HibernateStatsPage(PageParameters parameters) {
        super(parameters);

				Object delegate = emf.createEntityManager().getDelegate(); // SessionImpl
				System.out.println("EMF delegate:" +  delegate.getClass().getName() );
				SessionImpl si = (SessionImpl) delegate;
				this.sf = si.getSessionFactory();
				//SessionStatistics stats = si.getStatistics();


        final WebMarkupContainer st = new WebMarkupContainer("stats");
        final CompoundPropertyModel model = new CompoundPropertyModel(new LoadableDetachableModel() {
            protected Object load() {
                //return sf.getStatistics();
								Statistics stats = HibernateStatsPage.this.sf.getStatistics();
								return stats;
            }
        });
        st.setDefaultModel(model);
        st.add(new Label("isStatisticsEnabled"));
        st.add(new Label("startTime"));
        st.add(new Label("sessionOpenCount"));
        st.add(new Label("sessionCloseCount"));
        st.add(new Label("flushCount"));
        st.add(new Label("connectCount"));
        st.add(new Label("prepareStatementCount"));
        st.add(new Label("closeStatementCount"));
        st.add(new Label("entityLoadCount"));
        st.add(new Label("entityUpdateCount"));
        st.add(new Label("entityInsertCount"));
        st.add(new Label("entityDeleteCount"));
        st.add(new Label("entityFetchCount"));
        st.add(new Label("collectionLoadCount"));
        st.add(new Label("collectionUpdateCount"));
        st.add(new Label("collectionRemoveCount"));
        st.add(new Label("collectionRecreateCount"));
        st.add(new Label("collectionFetchCount"));
        st.add(new Label("secondLevelCacheHitCount"));
        st.add(new Label("secondLevelCacheMissCount"));
        st.add(new Label("secondLevelCachePutCount"));
        st.add(new Label("queryExecutionCount"));
        st.add(new Label("queryExecutionMaxTime"));
        st.add(new Label("queryExecutionMaxTimeQueryString"));
        st.add(new Label("queryCacheHitCount"));
        st.add(new Label("queryCacheMissCount"));
        st.add(new Label("queryCachePutCount"));
        st.add(new Label("commitedTransactionCount"));
        st.add(new Label("transactionCount"));
        st.add(new Label("optimisticFailureCount"));
        add(st);
        String[] entities = ((Statistics) model.getObject()).getEntityNames();
        List entityNames = new ArrayList();
        for (int i = 0; i < entities.length; i++) {
            entityNames.add(entities[i]);
        }
        ListView entityStats = new ListView("entities", entityNames) {
            protected void populateItem(ListItem item) {
                String entityName = (String) item.getModelObject();
                final EntityStatistics entityStat = ((Statistics) model.getObject()).getEntityStatistics(entityName);
                item.add(new Label("deleteCount", new Model(entityStat.getDeleteCount())));
                item.add(new Label("updateCount", new Model(entityStat.getUpdateCount())));
                item.add(new Label("fetchCount", new Model(entityStat.getFetchCount())));
                item.add(new Label("insertCount", new Model(entityStat.getInsertCount())));
                item.add(new Label("loadCount", new Model(entityStat.getLoadCount())));
                item.add(new Label("optimisticFailureCount", new Model(entityStat.getOptimisticFailureCount())));
                item.add(new Label("entityName", new Model(entityName)));
            }
        };
        add(entityStats);

        String[] collections = ((Statistics) model.getObject()).getCollectionRoleNames();
        List collectionNames = new ArrayList();
        for (int i = 0; i < collections.length; i++) {
            collectionNames.add(collections[i]);
        }
        ListView collectionStats = new ListView("collections", collectionNames) {
            protected void populateItem(ListItem item) {
                String collName = (String) item.getModelObject();
                CollectionStatistics collectionStatistics = ((Statistics) model.getObject()).getCollectionStatistics(collName);
                item.add(new Label("recreateCount", new Model(collectionStatistics.getRecreateCount())));
                item.add(new Label("updateCount", new Model(collectionStatistics.getUpdateCount())));
                item.add(new Label("fetchCount", new Model(collectionStatistics.getFetchCount())));
                item.add(new Label("removeCount", new Model(collectionStatistics.getRemoveCount())));
                item.add(new Label("loadCount", new Model(collectionStatistics.getLoadCount())));
                item.add(new Label("collName", new Model(collName)));
            }
        };
        add(collectionStats);

        String[] queries = ((Statistics) model.getObject()).getQueries();
        List queryNames = new ArrayList();
        for (int i = 0; i < queries.length; i++) {
            queryNames.add(queries[i]);
        }
        ListView queryStats = new ListView("queries", queryNames) {
            protected void populateItem(ListItem item) {
                String queryName = (String) item.getModelObject();
                QueryStatistics queryStatistics = ((Statistics) model.getObject()).getQueryStatistics(queryName);
                item.setModel(new CompoundPropertyModel(queryStatistics));
                item.add(new Label("cacheHitCount"));
                item.add(new Label("cacheMissCount"));
                item.add(new Label("cachePutCount"));
                item.add(new Label("executionCount"));
                item.add(new Label("executionRowCount"));
                item.add(new Label("executionAvgTime"));
                item.add(new Label("executionMaxTime"));
                item.add(new Label("executionMinTime"));
                item.add(new Label("categoryName"));
            }
        };
        add(queryStats);

        String[] caches = ((Statistics) model.getObject()).getSecondLevelCacheRegionNames();
        List cacheNames = new ArrayList();
        for (int i = 0; i < caches.length; i++) {
            cacheNames.add(caches[i]);
        }
        ListView cacheStats = new ListView("caches", cacheNames) {
            protected void populateItem(ListItem item) {
                String cacheName = (String) item.getModelObject();
                SecondLevelCacheStatistics cacheStatistics = ((Statistics) model.getObject()).getSecondLevelCacheStatistics(cacheName);
                item.setModel(new CompoundPropertyModel(cacheStatistics));
                item.add(new Label("hitCount"));
                item.add(new Label("missCount"));
                item.add(new Label("putCount"));
                item.add(new Label("elementCountInMemory"));
                item.add(new Label("elementCountOnDisk"));
                item.add(new Label("sizeInMemory"));
                item.add(new Label("categoryName"));
            }
        };
        add(cacheStats);

        add(new org.apache.wicket.markup.html.link.Link("switch_stats") {

            @Override
            public void onClick() {
                sf.getStatistics().setStatisticsEnabled(!sf.getStatistics().isStatisticsEnabled());
                sf.getStatistics().clear();
                final CompoundPropertyModel model = new CompoundPropertyModel(new LoadableDetachableModel() {
                  protected Object load() {
                    return sf.getStatistics();
                  }
               });
              st.setDefaultModel(model);

            }

        });
    }

}

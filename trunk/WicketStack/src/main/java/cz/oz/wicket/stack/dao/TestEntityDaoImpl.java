
package cz.oz.wicket.stack.dao;


import com.ibatis.sqlmap.client.SqlMapClient;
import cz.oz.wicket.stack.ent.TestEntity;
import java.util.*;
import java.util.logging.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.support.JpaDaoSupport;


/**
 *
 * @author Ondrej Zizka
 */
public class TestEntityDaoImpl extends JpaDaoSupport  implements TestEntityDao
{
  private static final Logger log = Logger.getLogger( TestEntityDaoImpl.class.getName() );

  // iBatis
  SqlMapClient iBatis = null;
  public void setIBatis( SqlMapClient iBatis ) { this.iBatis = iBatis; }
  public void setIbatis( SqlMapClient iBatis ) { this.iBatis = iBatis; } // Sic!

  
  @Override
  public TestEntity createSyntheticTestEntity() {
    return (TestEntity) this.getJpaTemplate().execute( new JpaCallback() {
      @Override
      public Object doInJpa( EntityManager em ) throws PersistenceException {
        Query query = em.createNativeQuery( "SELECT 666 AS id, 'This is a value.' AS value", TestEntity.class );
        return query.getSingleResult();
      }
    }, true);
  }

  @Override
  public TestEntity byId( long id ) {
    return this.getJpaTemplate().find( TestEntity.class, id );
  }

}// class TestEntityDaoImpl

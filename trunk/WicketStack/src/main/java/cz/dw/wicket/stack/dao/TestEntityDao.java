
package cz.dw.wicket.stack.dao;

import cz.dw.wicket.stack.ent.TestEntity;
import java.util.List;

/**
 *
 * @author Administrator
 */
public interface TestEntityDao {

  public TestEntity byId( long id );

  public TestEntity createSyntheticTestEntity();

}// class TestEntityDao

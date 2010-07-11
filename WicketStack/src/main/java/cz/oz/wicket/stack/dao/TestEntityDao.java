
package cz.oz.wicket.stack.dao;

import cz.oz.wicket.stack.ent.TestEntity;
import java.util.List;

/**
 *
 * @author Administrator
 */
public interface TestEntityDao {

  public TestEntity byId( long id );

  public TestEntity createSyntheticTestEntity();

}// class TestEntityDao

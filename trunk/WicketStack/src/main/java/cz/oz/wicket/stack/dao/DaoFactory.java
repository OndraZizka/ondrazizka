
package cz.oz.wicket.stack.dao;

/**
 *
 * @author Ondrej Zizka
 */
public class DaoFactory {

  public TestEntityDao testEntityDao;
  public TestEntityDao getTestEntityDao() {    return testEntityDao;  }
  public void setTestEntityDao( TestEntityDao testEntityDao ) {    this.testEntityDao = testEntityDao;  }

}// class DaoFactory

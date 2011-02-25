/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.jirabot;

import org.jboss.kecabot.TimeoutCache;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 *
 * @author ondra
 */
public class IssueCacheTest extends TestCase
{
   private static final Logger log = Logger.getLogger( IssueCacheTest.class.getName() );
    
    public IssueCacheTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


   /**
    * Test of getItem method, of class TimeoutCache.
    */
   public void testPutGetItem() {
      log.info("testPutGetItem()");

      TimeoutCache<String> cache = new TimeoutCache();
      final String KEY = "testKey";
      // Put.
      log.info("Put.");
      cache.putItem(KEY, "Foobar");
      // Wait. Default timeout is 30 minutes.
      log.info("Waiting 4 sec...");
      try { Thread.sleep(4000); } catch (InterruptedException ex) { ; }
      // Get.
      log.info("Get.");
      String result = cache.getItem(KEY);
      // Should be there.
      assertEquals("Foobar", result);
      // Cleanup.
      cache.stopExpiryThread();
   }

   /**
    * Test of getItem method, of class TimeoutCache.
    */
   public void testPutGetExpiredItem() {
      System.out.println("testPutGetExpiredItem()");

      TimeoutCache<String> cache = new TimeoutCache( 100 ); // Expire immediatelly
      final String KEY = "testKey";
      // Put.
      log.info("Put.");
      cache.putItem(KEY, "Foobar");
      // Wait.
      log.info("Waiting 8 sec...");
      try { Thread.sleep(8000); } catch (InterruptedException ex) {  }
      // Get.
      log.info("Get.");
      String result = cache.getItem(KEY);
      // Should be expired by now.
      assertEquals(null, result);
      // Cleanup.
      cache.stopExpiryThread();
   }


}

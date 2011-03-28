
package cz.dynawest.util;

import java.util.Date;

/**
 *
 * @author Ondrej Zizka
 */
public class DateUtils extends org.apache.commons.lang.time.DateUtils {

   public static Date getEarlier( Date a, Date b ) {
      return a.before(b) ? a : b;
   }

   public static Date getLater( Date a, Date b ) {
      return a.before(b) ? b : a;
   }

}// class

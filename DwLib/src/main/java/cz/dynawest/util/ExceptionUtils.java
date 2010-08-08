
package cz.dynawest.util;


/**
 *
 * @author Ondrej Zizka
 */
public class ExceptionUtils
{


  /**
   * @returns the most nested cause.
   */
  public static Throwable getRootCause( Throwable ex ){

    //return null == ex.getCause() ? getRootCause( ex.getCause() ) : ex;

    int i = 0;

    Throwable next;
    while( null != (next = ex.getCause()) ){
      ex = next;
      
      // Loop-prevention.
      if( ++i > 100 )
        break;
    }

    return ex;

  }


  /**
   * @returns true if some of the nested causes are of the class @cls.
   */
  public static boolean hasCause( Throwable ex, Class cls ){
    
    int i = 0;

    while( null != ex ) {
      if( cls.isInstance( ex ) )
        return true;
      ex = ex.getCause();

      // Loop-prevention.
      if( ++i == 64 )
        break;
    }
    
    return false;
  }



  /**
   * @returns  the first cause which is of class cls.
   */
  public static Throwable findCauseByClass( Throwable ex, Class cls ){
    
    int i = 0;

    while( null != ex ) {
      if( cls.isInstance( ex ) )
        return ex;
      ex = ex.getCause();

      // Loop-prevention.
      if( ++i == 64 )
        break;
    }

    return null;

  }

  
}// class ExceptionUtils

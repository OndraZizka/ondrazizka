package cz.dynawest.util;

/**
 *
 * @author Ondřej Žižka
 */
public final class StringTools {

  /**
   * Returns value escaped for JPA QL with given escape character.
   * Escaped characters are '%', '_' and the escape character itself.
   * Eg.:  <code>"%Foo\B_r%"</code> escaped with '\' becomes <code>"\%Foo\\B\_r\%"</code>.
   *
   * @param value  Value to escape.
   * @param escapeChar  Escape character.
   * @return
   */
  public static String escapeJPAQL( String value, String escapeChar ){
    
    value = value.replace( "%", escapeChar+"%" );
    value = value.replace( "_", escapeChar+"_" );
    value = value.replace( escapeChar, escapeChar+escapeChar );
    return value;

  }

  /**
   * Escapes the value with character "\".
   * @see #escapeJPAQL(java.lang.String, java.lang.String)
   */
  public static String escapeJPAQL( String value ){
    return escapeJPAQL( value, "\\" );
  }

}// class Tools

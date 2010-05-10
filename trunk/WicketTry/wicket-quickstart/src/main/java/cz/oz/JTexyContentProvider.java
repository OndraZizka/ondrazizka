
package cz.oz;


import cz.dynawest.jtexy.JTexy;
import cz.dynawest.jtexy.TexyException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.*;
import org.apache.commons.io.FileUtils;


/**
 *
 * @author Ondrej Zizka
 */
public class JTexyContentProvider
{
  private static final Logger log = Logger.getLogger( JTexyContentProvider.class.getName() );

  private String storePath;
  private JTexy jtexy;
  private String cachePath;


  /**
   * Const
   * @param storePath  Filesystem path to a store of Texy markup files.
   */
  public JTexyContentProvider( String storePath ) throws TexyException {
    this.storePath = storePath;
    this.jtexy = JTexy.create();
  }


  /**
   * Remove '..', weird chars, etc.
   */
  private String normalizePath( String path ){
    return path.replace("..", "").replace('\\', '/').replace("//", "/").replace(" ", "").replace(":", "");
  }


  /**
   *
   * @param path   JTexy markup file to convert.
   * @return       Result of conversion - a HTML markup.
   * @throws IOException    Re-thrown from FileUtils.readFileToString
   * @throws TexyException  Re-thrown from this.jtexy.process( file );
   */
  public String getContent( String path ) throws IOException, TexyException {

    path = this.normalizePath(path);
    
    String content = findCachedContent( path );
    if( content != null ){
      return content;
    }

    String file = FileUtils.readFileToString( new File(storePath + path ), "windows-1250" );
    content = this.jtexy.process( file );

    // Convert encoding.
    //content = new String(content.getBytes("utf-8"), "windows-1250");

    storeCachedContent( path, content );

    return content;
  }

  
  /**
   * TODO
   * @param path
   * @return  Cached content for this path, or null if not found.
   */
  private String findCachedContent( String path ) {
    // Touch the entry.
    // Return the entry.
    return null;
  }


  /**
   * TODO
   * @param path    Cache key.
   * @param content Cached content.
   */
  private void storeCachedContent( String path, String content ) {
    
  }

  

  

}// class JTexyContentProvider

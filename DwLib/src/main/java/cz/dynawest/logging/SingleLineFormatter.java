/*
 * @(#)SimpleFormatter.java	1.15 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cz.dynawest.logging;

import java.util.logging.*;
import java.io.*;
import java.text.*;
import java.util.Date;

/**
 * Print a brief summary of the LogRecord in a human readable
 * format.  The summary will typically be 1 or 2 lines.
 *
 * @version 1.15, 11/17/05
 * @since 1.4
 */
public class SingleLineFormatter extends Formatter {

  Date dat = new Date();
  private final static String format = "{0,date} {0,time}";
  private MessageFormat formatter;
  private Object args[] = new Object[1];

  // Line separator string.  This is the value of the line.separator
  // property at the moment that the SimpleFormatter was created.
  //private String lineSeparator = (String) java.security.AccessController.doPrivileged(
  //        new sun.security.action.GetPropertyAction("line.separator"));
  private String lineSeparator = "\n";

  /**
   * Format the given LogRecord.
   * @param record the log record to be formatted.
   * @return a formatted log record
   */
  public synchronized String format(LogRecord record) {
    
    StringBuilder sb = new StringBuilder();
    
    // Minimize memory allocations here.
    dat.setTime(record.getMillis());    
    args[0] = dat;
    
    
    // Date and time 
    StringBuffer text = new StringBuffer();
    if (formatter == null) {
      formatter = new MessageFormat(format);
    }
    formatter.format(args, text, null);
    sb.append(text);
    sb.append(" ");
    
    
    // Class name 
    if (record.getSourceClassName() != null) {
      sb.append(record.getSourceClassName());
    } else {
      sb.append(record.getLoggerName());
    }
    
    // Method name 
    if (record.getSourceMethodName() != null) {
      sb.append(" ");
      sb.append(record.getSourceMethodName());
    }
    sb.append(" - "); // lineSeparator
    
    
    
    String message = formatMessage(record);
    
    // Level
    sb.append(record.getLevel().getLocalizedName());
    sb.append(": ");
    
    // Indent - the more serious, the more indented.
    //sb.append( String.format("% ""s") );
    int iOffset = (1000 - record.getLevel().intValue()) / 100;
    for( int i = 0; i < iOffset;  i++ ){
      sb.append(" ");
    }


    sb.append(message);
    sb.append(lineSeparator);
    if (record.getThrown() != null) {
      try {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        record.getThrown().printStackTrace(pw);
        pw.close();
        sb.append(sw.toString());
      } catch (Exception ex) {
      }
    }
    return sb.toString();
  }
}

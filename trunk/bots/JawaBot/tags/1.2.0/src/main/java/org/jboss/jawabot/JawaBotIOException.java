
package org.jboss.jawabot;

import org.jboss.jawabot.JawaBotException;

/**
 *
 * @author Ondrej Zizka
 */
public class JawaBotIOException extends JawaBotException {

   public JawaBotIOException(String message) {
      super(message);
   }

   public JawaBotIOException(String message, Throwable cause) {
      super(message, cause);
   }

   public JawaBotIOException(Throwable cause) {
      super(cause);
   }

}// class

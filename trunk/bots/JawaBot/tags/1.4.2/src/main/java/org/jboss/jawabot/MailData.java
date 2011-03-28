
package org.jboss.jawabot;

/**
 *
 * @author Ondrej Zizka
 */
public class MailData {

   public final String subject;
   public final String messageBody;

   public MailData(String subject, String messageBody) {
      this.subject = subject;
      this.messageBody = messageBody;
   }

}// class

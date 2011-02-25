
package org.jboss.jirabot.test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import junit.framework.TestCase;

/**
 * Test regexes.
 *

<yiwang> siege, hi. I am trying to build jboss-seam2. There was text built
error when I built from RHEL-4-EP-5. I talked to Marek and he said I
should build from RHEL-4-EP-5-JDK6. I tried that in mock and it
succeeded. when doing "make build COLLECTION=dist-5E-ep-5.1", I got
error message "You can't build text package for RHEL-4-EP-5-JDK6 into
dist-5E-ep-5.1"
<JiraBot> Don't be obnoxious, I'll answer up to 3 JIRA requests at text time.
* sharps has quit (Quit: This computer has gone to sleep)
<jcosta> ozizka, does JiraBot thinks that  RHEL-4-EP-5-JDK6 is text JIRA ?

 * @author Ondrej Zizka
 */
public class RegexTest extends TestCase {

   public void testRegex(){

      //final String REGEX_1 = "([A-Z]{2,}-[0-9]+)";
      //final String REGEX_1 = "\\b(?=[ ,])?([A-Z]{2,}-[0-9]+)(?=[ ,])?";
      //final String REGEX_1 = "(?<!-)([A-Z]{2,}-[0-9]+)(?![.\\-])?";
      //final String REGEX_1 = "^.*?(?!_)([A-Z]{2,}-[0-9]+)";
      //final String REGEX_1 = "^.*?(?![^_A-Z])?+\\b([A-Z]{2,}-[0-9]+)";
      //final String REGEX_1 = "^.*?(?:[^_\\-])??\\b([A-Z]{2,}-[0-9]+)";
      //final String REGEX_1 = "((?<![-_.A-Z])[A-Z]{2,}-[0-9]+)(?!(-|\\.[0-9]))"; // Promising
      final String REGEX_1 = "((?<![-_.A-Z])[A-Z]{2,}-[0-9]++)(?!(-|\\.[0-9A-Za-z]))";

      
      Pattern pat = Pattern.compile( REGEX_1 );
      Matcher mat = pat.matcher("");

      
      List<Sample> samples = new ArrayList(){{
         // Positive
         add( new Sample("JBPAPP-1", "JBPAPP-1") );
         add( new Sample("JBPAPP-123", "JBPAPP-123") );
         add( new Sample("JBPAPP-1.", "JBPAPP-1") );
         add( new Sample("JBPAPP-123.", "JBPAPP-123") );
         add( new Sample("JBPAPP-1,", "JBPAPP-1") );
         add( new Sample("JBPAPP-123,", "JBPAPP-123") );
         add( new Sample("JBPAPP-1?", "JBPAPP-1") );
         add( new Sample("JBPAPP-123?", "JBPAPP-123") );
         add( new Sample(" JBPAPP-1.", "JBPAPP-1") );
         add( new Sample(" JBPAPP-123.", "JBPAPP-123") );
         add( new Sample(",JBPAPP-1.", "JBPAPP-1") );
         add( new Sample(",JBPAPP-123.", "JBPAPP-123") );

         // Negative
         add( new Sample("_JBPAPP-1", null) );
         add( new Sample(".JBPAPP-1", null) );
         add( new Sample("-JBPAPP-1", null) );
         add( new Sample("-JBPAPP-1.", null) );
         add( new Sample("-JBPAPP-123.", null) );
         add( new Sample(" JBPAPP-1-A.", null) );
         add( new Sample(" JBPAPP-123-A", null) );
         add( new Sample("JBPAPP-123.c", null) );

         add( new Sample("I built from RHEL-4-EP-5.", null) );
         add( new Sample("RHEL-4-EP-5.", null) );
         add( new Sample("into dist-5E-ep-5.1.", null) );
         add( new Sample("dist-5E-ep-5.1", null) );
      }};

      // Failed samples
      List<MatchReport> failedSamples = new ArrayList();


      
      // Check all samples.
      for( Sample sample : samples ) {

         String expected = sample.jiraId;

         mat.reset( sample.text );

         // NOT matched.
         if( ! mat.find() ){
            if( null != expected ){
               failedSamples.add( new MatchReport(sample, "(not matched)") );
            }
            continue;
         }

         // Matched string (should be the JIRA ID).
         String matched = mat.group( 1 );

         // False positive.
         if( null == expected ){
            failedSamples.add( new MatchReport(sample, matched) );
            continue;
         }

         if( ! expected.equals( matched ) ){
            failedSamples.add( new MatchReport(sample, matched) );
            continue;
         }

      }// for each sample.

      System.out.println("Failed samples: "+failedSamples.size() + " of "+samples.size() );

      for (MatchReport sample : failedSamples) {
         System.out.println("  * "+sample.toString() );
      }

   }// testRegex()

}// class



class Sample{

   public String text;
   public String jiraId;

   public Sample(String text, String jiraId) {
      this.text = text;
      this.jiraId = jiraId;
   }

   @Override
   public String toString() {
      return this.text + " -> "+ this.jiraId;
   }


}


class MatchReport {
   public final Sample sample;
   public final String actualMatch;

   public MatchReport(Sample sample, String actualMatch) {
      this.sample = sample;
      this.actualMatch = actualMatch;
   }

   @Override
   public String toString() {
      return this.sample.text + " -> "+ this.actualMatch +";  should be: " + this.sample.jiraId;
   }
}
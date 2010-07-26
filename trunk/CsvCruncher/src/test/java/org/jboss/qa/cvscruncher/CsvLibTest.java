package org.jboss.qa.cvscruncher;

import csv.impl.CSVReader;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.lang.StringUtils;

/**
 * Unit test for simple App.
 */
public class CsvLibTest extends TestCase
{
	private static final Logger log = Logger.getLogger(CsvLibTest.class.getName());

	private static final String DATA_DIR = System.getProperty("test.data.dir");

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CsvLibTest( String testName )
    {
        super( testName );

				if( null == DATA_DIR )
					throw new RuntimeException("test.data.dir property is not set - where should I get data from?");
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( CsvLibTest.class );
    }

		
    /**
     * Rigourous Test :-)
     */
    public void testReading()
    {

			try {
				java.io.File file = new java.io.File(DATA_DIR+"/csv-test.csv");

				CSVReader reader = new csv.impl.CSVReader(file);
				reader.setColumnSeparator(',');
				reader.setColumnDelimiter("'");
				reader.setCommentChars("#");
				reader.setIgnoreComments(true);
				reader.setIgnoreEmptyLines(true);

				csv.TableReader tableReader = reader;
				while( tableReader.hasNext() ) {
					Object[] columns = tableReader.next();
					// Do something here
					log.info( StringUtils.join(columns, ", ") );
				}
				tableReader.close();
			} catch (FileNotFoundException ex) {
				log.log(Level.SEVERE, null, ex);
			}

    }
}

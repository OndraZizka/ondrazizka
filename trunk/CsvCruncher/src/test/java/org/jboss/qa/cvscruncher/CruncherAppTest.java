package org.jboss.qa.cvscruncher;

import org.jboss.qa.cvscruncher.App;
import csv.impl.CSVReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.lang.StringUtils;

/**
 * Unit test for simple App.
 */
public class CruncherAppTest extends TestCase
{
	private static final Logger log = Logger.getLogger(CruncherAppTest.class.getName());

	private static final String DATA_DIR = System.getProperty("test.data.dir");
	private static final String TARGET_DIR = System.getProperty("target.dir");

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CruncherAppTest( String testName )
    {
        super( testName );

				log.setLevel(Level.ALL);

				if( null == DATA_DIR )
					throw new RuntimeException("test.data.dir property is not set - where should I get data from?");
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( CruncherAppTest.class );
    }

	@Override
	protected void setUp() throws Exception {
		System.out.println("");
		System.out.println("=============================================================================");
		System.out.println("   Test: " + this.getClass().getName() + " / " + this.getName());
		System.out.println("=============================================================================");
		System.out.println("");
	}

	@Override
	protected void tearDown() throws Exception {
		System.out.println("");
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("");
	}





		
    /**
     * Rigourous Test :-)
     */
    public void testReading() throws Exception
    {

			try {

				String SQL;
				SQL = "SELECT jobName, buildNumber, SUM( (0.0 + deployDur) / scale) AS buildDur FROM indata GROUP BY jobName, buildNumber";
				SQL = "SELECT *, ((0.0 + warmupDur) / deployDur ) AS xwarmupSlower FROM indata ORDER BY deployDur";
				SQL = "SELECT *, ((0.0 + CAST(warmupDur AS INT)) / CAST(deployDur AS INT)) AS warmupSlower FROM indata ORDER BY deployDur";
				SQL = "SELECT * FROM indata ORDER BY deployDur";
				SQL = "SELECT ((0.0 + warmupDur) / deployDur) FROM indata";
				SQL = "SELECT ((0.0 + CAST(warmupDur AS INT)) / CAST(deployDur AS INT)) AS xwarmupSlower FROM indata";
				SQL = "SELECT 1 AS x FROM indata";
				SQL = "SELECT jobName, buildNumber, config, ar, arFile, deployDur, warmupDur, scale,"
								+ " CAST(warmupDur AS DOUBLE) / CAST(deployDur AS DOUBLE) AS warmupSlower FROM indata ORDER BY deployDur";

				//System.setProperty(tmp, tmp)
				App.main( new String[]{DATA_DIR+"/csv-test.csv", TARGET_DIR+"/cruncher-output.csv", SQL, "-db", TARGET_DIR+"/hsql-data"} );

			} catch (Exception ex) {
				log.log(Level.SEVERE, null, ex);
				throw ex;
			}

    }
}

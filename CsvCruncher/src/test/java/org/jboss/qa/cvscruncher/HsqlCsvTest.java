package org.jboss.qa.cvscruncher;

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
public class HsqlCsvTest extends TestCase
{
	private static final Logger log = Logger.getLogger(HsqlCsvTest.class.getName());

	private static final String DATA_DIR = System.getProperty("test.data.dir");
	private static final String TARGET_DIR = System.getProperty("target.dir");

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public HsqlCsvTest( String testName )
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
        return new TestSuite( HsqlCsvTest.class );
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

				String dbPath = TARGET_DIR+"/hsql-data/csv-test";
				File file = new java.io.File(DATA_DIR+"/csv-test.csv");

				Class.forName("org.hsqldb.jdbc.JDBCDriver");
				Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:"+dbPath, "SA", "");

				PreparedStatement ps = conn.prepareStatement("CREATE TEXT TABLE stats ("
								+ "  jobName     VARCHAR(255) NOT NULL,"
								+ "  buildNumber VARCHAR(255) NOT NULL,"
								+ "  config      VARCHAR(255) NOT NULL,"
								+ "  ar          VARCHAR(255) NOT NULL,"
								+ "  arFile      VARCHAR(255) NOT NULL,"
								+ "  deployDur   INT NOT NULL,"
								+ "  warmupDur   INT NOT NULL,"
								+ " scale INT NOT NULL"
								+ ")");
				boolean succ = ps.execute();

				ps = conn.prepareStatement("SET TABLE stats SOURCE '"+file.getPath()+";ignore_first=true;fs=,' ");
				succ = ps.execute();

				ps = conn.prepareStatement("SELECT jobName, buildNumber, SUM( (0.0 + deployDur) / scale) AS buildDur FROM stats GROUP BY jobName, buildNumber");
				ResultSet rs = ps.executeQuery();
				while( rs.next() ){
					//int buildDur = rs.getInt("buildDur");
					//double ms = 0.0 + buildDur /
					System.out.println( String.format(" * Job %s Build %d took %f ms.",
									rs.getString("jobName"),
									rs.getInt("buildNumber"),
									rs.getDouble("buildDur")) );
				}
				

			}
			catch (Exception ex) {
				log.log(Level.SEVERE, "Uh-oh.", ex);
				throw ex;
			}

    }
}

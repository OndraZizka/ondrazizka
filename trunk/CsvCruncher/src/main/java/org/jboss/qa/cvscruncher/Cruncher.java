
package org.jboss.qa.cvscruncher;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;


/**
 *
 * @author Ondrej Zizka
 */
public class Cruncher
{
		private static final Logger log = Logger.getLogger(App.class.getName());


		protected static class Options {
			protected String csvPathIn;
			protected String sql;
			protected String csvPathOut;
			protected String dbPath = null;

			public boolean isFilled(){ return csvPathIn != null && csvPathOut != null && sql != null; }

			public String toString(){
				return new StringBuilder()
					.append("\n    dbPath: ").append(this.dbPath)
					.append("\n    csvPathIn: ").append(this.csvPathIn)
					.append("\n    csvPathOut: ").append(this.csvPathOut)
					.append("\n    sql: ").append(this.sql)
					.toString();
			}
		}


		private Connection conn;
		private Options options;


		public Cruncher(Options options) throws ClassNotFoundException, SQLException {
			this.options = options;
			this.init();
		}



		private void init() throws ClassNotFoundException, SQLException {

				System.setProperty("textdb.allow_full_path", "true");

				// "Connect" to the db.
				Class.forName("org.hsqldb.jdbc.JDBCDriver");
				// TODO: Problem: HSQLDB reads CSV only from it's dir!
				String dbPath = StringUtils.defaultIfEmpty( this.options.dbPath, "hsqldb") + "/cruncher";
				this.conn = DriverManager.getConnection("jdbc:hsqldb:file:"+dbPath+";shutdown=true", "SA", "");

				/*
				  File locations are restricted to below the directory that contains the database,
				  unless the textdb.allow_full_path property is set true as a Java system property.
				  This feature is for security, otherwise an admin database user may be able to open random files.
				 */
		}
		


		/**
		 * Crunches the CSV file:
		 *   - Reads the first line to get the column names,
		 *   - Starts the DB, creates a table with those columns, and imports the file into that table,
		 *   - Performs a SQL query,
		 *   - Puts the result into another CSV file.
		 */
		public void crunch() throws Exception {
			try {

				if( this.options.csvPathIn == null )
					throw new IllegalArgumentException(" -in is not set.");
				if( this.options.sql == null )
					throw new IllegalArgumentException(" -sql is not set.");
				if( this.options.csvPathOut == null )
					throw new IllegalArgumentException(" -out is not set.");

				File file = new java.io.File( this.options.csvPathIn );
				if( ! file.exists() )
					throw new FileNotFoundException( file.getPath() );


				/*
				 * http://hsqldb.org/doc/2.0/guide/deployment-chapt.html
				 *
				 * Readonly Databases
				 *
				 * There is another option which allows MEMORY tables to be writable, but without persisting the changes at SHUTDOWN.
				 * This option is activated with the property, value pair, files_readonly= true, which can be added to the .properties
				 * file of the database, or included in the URL of the first connection to the  .
				 * This option is useful for running application tests which operate on a predefined dataset.
				 *
				 * Tweaking the Mode of Operation
				 *
				 * Some types of tests start with a database that already contains the tables and data,
				 * and perform various operations on it during the tests. You can create and populate the initial database
				 * then set the property "files_readonly=true" in the .properties file of the database.
				 * The tests can then modify the database, but these modifications are not persisted after the tests have completed.
				 */
				//Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:"+dbPath+";shutdown=true;files_readonly=false", "SA", "");
				// SQLException: Database is memory only



				int reachedStage = 0;

				try{

					//File userDir = new File( System.getProperty("user.dir") );
					File inFile   = getFileObject( this.options.csvPathIn );
					File outFile  = getFileObject( this.options.csvPathOut );

					log.info( this.options.toString() );
					log.info("******* inPath: " + this.options.csvPathIn );
					log.info("******* inFile: " + inFile.getPath() );



					// Check input file existence.
					if( ! inFile.exists() )
						throw new FileNotFoundException("CSV file not found: "+inFile.getPath());

					// Create parent dirs for the output.
					outFile.getAbsoluteFile().getParentFile().mkdirs();


					// Create the table for the CSV data.
					String[] colNames = parseColsFromFirstLine( file );
					createTableForCsvFile( "indata", inFile, colNames, true );
					reachedStage = 1;

					if( true )
						testDumpSelect( "indata" );

					// Perform the user-provided SQL statement.
					PreparedStatement ps = conn.prepareStatement( options.sql );
					ResultSet rs = ps.executeQuery();

					// Get col names.
					colNames = new String[rs.getMetaData().getColumnCount()];
					for (int i = 0; i < colNames.length; i++) {
						colNames[i] = rs.getMetaData().getColumnName(i+1);
					}

					// -- Dump data to output CSV file. -- //

					// "Mount" the output CSV file.
					createTableForCsvFile( "output", outFile, colNames, true );
					reachedStage = 2;

					// Perform the user-provided SQL statement.
					/* DOES NOT WORK - java.sql.SQLFeatureNotSupportedException: feature not supported
					 *     https://sourceforge.net/tracker/?func=detail&aid=3033893&group_id=23316&atid=378131
					 */
					String sql = "INSERT INTO output (" + this.options.sql + ")";
					log.info("User's SQL: "+sql);
					ps = this.conn.prepareStatement( sql );

					// Workaround - call a generic INSERT for each row.
  				/*ps = conn.prepareStatement( "INSERT INTO output ("+StringUtils.repeat("?", ", ", colNames.length)+")" );
					for (int i = 0; i < colNames.length; i++) {
						ps.setObject(i+1, rs.getObject(i+1));
					}*/

					int rowsAffected = ps.executeUpdate();
					
					//testDumpSelect( "output" );///


				}
				finally {
					// Detach the table (should ensure flushing).
					if( reachedStage >= 1 ) detachTable( "indata", false );
					if( reachedStage >= 2 ) detachTable( "output", false );
					PreparedStatement ps = this.conn.prepareStatement( "DROP SCHEMA PUBLIC CASCADE" );
					ps.execute();
					this.conn.close();
				}
			}
			catch (Exception ex) {
				//log.log(Level.SEVERE, ex.getMessage(), ex);
				throw ex;
			}

		}// crunch()




		/**
		 * Parses the CSV file's first line for the coma-separated column names.
		 * Must be valid SQL identifier, i.e. must match [a-z][a-z0-9]*.
		 * @param file
		 * @return
		 */
		private static String[] parseColsFromFirstLine(File file) throws IOException
		{

			Pattern pat = Pattern.compile("[a-z][a-z0-9]*", Pattern.CASE_INSENSITIVE);
			Matcher mat = pat.matcher("");

			List<String> cols = new ArrayList();
			LineIterator lineIterator = FileUtils.lineIterator(file);
			if( ! lineIterator.hasNext() )
				throw new IllegalStateException("No first line with columns definition (format: [# ] <colName> [, ...]) in: "+file.getPath());

			String line = lineIterator.nextLine();
			line = StringUtils.stripStart(line, "#");

			String[] colNames = StringUtils.splitPreserveAllTokens(line, ",;");

			for (String colName : colNames) {
				colName = colName.trim();
				if( 0 == colName.length() )
					throw new IllegalStateException("Empty column name (separators: ,; ) in: " + file.getPath() );

				if( ! mat.reset(colName).matches() )
					throw new IllegalStateException("Colname must be valid SQL identifier, i.e. must match /[a-z][a-z0-9]*/i in: " + file.getPath() );

				cols.add( colName );
			}

			return (String[]) cols.toArray( new String[cols.size()]);
		}




		/**
		 * Creates a CSV file backed table with given columns, all VARCHAR(255).
		 *
		 * @param csvPathIn
		 * @param colNames
		 */
		private void createTableForCsvFile( String tableName, File csvFileIn, String[] colNames, boolean ignoreFirst) throws SQLException, FileNotFoundException
		{
				tableName = StringEscapeUtils.escapeSql(tableName);
				boolean readOnly = false; // TODO: Move to params.

				// Create the CSV header.
				// Create the CREATE statement.
				StringBuilder sbCsvHeader = new StringBuilder("# ");
				StringBuilder sb = new StringBuilder("CREATE TEXT TABLE ").append( tableName ).append(" ( ");

				for (String col : colNames) {
					sbCsvHeader.append( col ).append(", ");
					col = StringEscapeUtils.escapeSql( col );
					sb.append( col ).append(" VARCHAR(255), "); // NOT NULL removed
				}
				sb.delete( sb.length() - 2, sb.length() );
				sb.append(" )");
				sbCsvHeader.delete( sbCsvHeader.length() - 2, sbCsvHeader.length() );

				log.info("SQL: " + sb.toString());

				PreparedStatement ps = this.conn.prepareStatement( sb.toString() );
				boolean succ = ps.execute();


				// Load the data from the CSV file.
				String csvPathIn = csvFileIn.getPath();
				csvPathIn = StringEscapeUtils.escapeSql(csvPathIn);
				String ifFlag = ignoreFirst ? "ignore_first=true;" : "";
				String DESC = readOnly ? "DESC" : "";
				String sql = "SET TABLE "+tableName+" SOURCE '"+csvPathIn+";"+ifFlag+"fs=,' "+DESC;
				log.info("SQL: " + sql );
				ps = this.conn.prepareStatement( sql );  // ;encoding=UTF-8
				succ = ps.execute();

				// TODO: When a text table is created with a new source (CSV) file, and ignore_first=true has been specified,
				// the following command can be used to set a user defined string as the first line:
				// SET TABLE <table name> SOURCE HEADER <double quoted string>.
				/* HsqlException: Header not allowed or too long (not too long, 'test' did not pass either.
				ps = conn.prepareStatement("SET TABLE "+tableName+" SOURCE HEADER '"+StringEscapeUtils.escapeSql( sbCsvHeader.toString() )+"' ");
				succ = ps.execute();
				 */
		}




		/**
		 * Detaches - reattaches the table from/to the CSV file.
		 * @param string
		 * @param b
		 */
		private void detachTable( String name, boolean reattach) throws SQLException {
			String sql = "SET TABLE "+StringEscapeUtils.escapeSql(name)+" SOURCE " + (reattach ? "ON" : "OFF");
			PreparedStatement ps = this.conn.prepareStatement( sql );
			boolean succ = ps.execute();
		}

		private void testDumpSelect( String tableName ) throws SQLException {
				PreparedStatement ps = this.conn.prepareStatement("SELECT * FROM "+tableName);
				ResultSet rs = ps.executeQuery();

				ResultSetMetaData metaData = rs.getMetaData();
				while( rs.next() ){
					System.out.println(" ------- ");
					for (int i = 1; i <= metaData.getColumnCount(); i++) {
						System.out.println(" "+ metaData.getColumnLabel(i) + ": "+ rs.getObject(i) );
					}
				}
		}


		/**
		 * HSQLDB uses it's data dir for relative paths, thus we need to make them ourselves.
		 * @param path  Relative or absolute path.
		 * @returns     File object, if path is relative, then it's appended to the JVM's cwd.
		 */
		private File getFileObject(String path) {
			if( ! path.isEmpty() && path.charAt(0) == '/' )
				// Absolute
				return new File( path );
			else
				// Relative, use current working dir.
				return new File( System.getProperty("user.dir"), path );
		}

		
}// class

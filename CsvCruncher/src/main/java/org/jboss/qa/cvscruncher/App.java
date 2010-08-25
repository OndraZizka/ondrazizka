package org.jboss.qa.cvscruncher;

import cz.dynawest.logging.LoggingUtils;
import java.util.logging.Logger;

/**
 * Ugly class which does almost everything. Feel free to refactor.
 *
 * TODO: Move all the DB stuff to another class, with a Connection management.
 * TODO: Use iBatis if ever adding more SQL.
 *
 */
public class App 
{
		private static final Logger log = Logger.getLogger(App.class.getName());

		public static void main( String[] args ) throws Exception
		{

			LoggingUtils.initLogging();

			Cruncher.Options options;
			try {
				options = parseArgs( args );
				//options.dbPath = System.getProperty("user.dir");
				new Cruncher( options ).crunch();
			}
			// Arguments error.
			catch( IllegalArgumentException ex ){
				System.out.println("" + ex.getMessage());
				System.exit( 1 );
			}

		}


		/**
		 * Parses arguments according to this pattern:
		 *
		 *   <pre> crunch [-in] inCSV [-out] outCSV [-sql] SQL </pre>
		 *
		 * @param args
		 * @return
		 */
		private static Cruncher.Options parseArgs(String[] args) {
			Cruncher.Options opt = new Cruncher.Options();

			int relPos = -1; // For args without preceding -label.
			
			OptionsNext next = null;
			for (int i = 0; i < args.length; i++) {
				
				String str = args[i];

				if( "-in".equals(str) ){
					next = OptionsNext.IN; continue;
				}
				if( "-out".equals(str) ){
					next = OptionsNext.OUT; relPos = 2;  continue;
				}
				if( "-sql".equals(str) ){
					next = OptionsNext.SQL;  relPos = 3; continue;
				}
				if( "-db".equals(str) ){
					next = OptionsNext.DBPATH; continue;
				}

				if( next != null ){
					switch( next ){
						case IN: opt.csvPathIn   = str; relPos = Math.max(relPos, 1); continue;
						case OUT: opt.csvPathOut = str; relPos = Math.max(relPos, 2); continue;
						case SQL: opt.sql        = str; relPos = Math.max(relPos, 3); continue;
						case DBPATH: opt.dbPath  = str; continue;
					}
					next = null;
				}

				// An argument without preceding -label.
				relPos++;
				if( relPos == 0 ){ opt.csvPathIn  = str; continue; }
				if( relPos == 1 ){ opt.csvPathOut = str; continue; }
				if( relPos == 2 ){ opt.sql        = str; continue; }

				throw new IllegalArgumentException("Wrong arguments. "+STR_USAGE);

			}// for each arg.

			if( ! opt.isFilled() ){
				throw new IllegalArgumentException("Not enough arguments. "+STR_USAGE);
			}

			return opt;
			
		}// parseArgs();



		/**
		 *
		 */
		private static void printUsage() {
			System.out.println("  Usage:");
			System.out.println("    "+STR_USAGE);
		}

		public static final String STR_USAGE = "Usage: crunch [-in] <inCSV> [-out] <outCSV> [-sql] <SQL>";

		
		
		private enum OptionsNext { IN, OUT, SQL, DBPATH };

}

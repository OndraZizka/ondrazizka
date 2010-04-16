/*
 * Narychlo nasekaný nástroj na analýzu závislostí ant targetů.
 * Usage:  java -jar AntStructure.jar [build.xml]
 */

package antstructure;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.logging.*;





/**
 *
 * @author ondra
 */
public class Main {
	
	//static boolean printProcessingMessages = true;
	static PrintStream processingMessagesStream = System.out;
	

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		
		String sFileNameInput = "build.xml";
		String sFileNameOutput = "output.txt";
		String sTargetSet = null;
		
		if( args.length == 0 ){
			System.out.println( "Usage: java -jar AntStructure.jar [<ant file:'build.xml'> [<target>] ] " );
			System.out.println( "Acting like if \"program-name build.xml\" was called." );
		}else{
			sFileNameInput = args[0];
			if( args.length > 1 ){
				//sFileNameOutput = args[1];
				sTargetSet = args[1];
			}
		}
		
		System.out.println( "Reading file "+sFileNameInput+" ..." );
		
		BuildFileAnalyzer analyzer = new BuildFileAnalyzer();
		
		try{
			analyzer.readBuildFile(sFileNameInput);
			analyzer.showTargetTree( sTargetSet );
		}
		catch(org.xml.sax.SAXParseException e){
			System.err.println("ERROR: XML syntax error in" + sFileNameInput + ": " +e.getMessage());
		}
		catch (FileNotFoundException ex) {
			//Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			System.out.println("ERROR: File not found: "+sFileNameInput);
		}
		catch(Exception e){
			System.err.println(""+e.getClass());
			e.printStackTrace();
		}		
		
		
	}// public static void main(String[] args)
	





}// public class Main
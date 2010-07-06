
package cz.dynawest.util;

import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Ondřej Žižka
 */
public class DebugUtils {

	public static String getEnvironmentVariablesAsString(){

		Map<String, String> envVars = System.getenv();

		StringBuilder sb = new StringBuilder("Environment variables:\n");

		for( Map.Entry<String,String> envVar : envVars.entrySet() ){
			sb.append("  ").append( envVar.getKey() ).append(": ").append(envVar.getValue()).append("\n");
		}

		return sb.toString();
	}


//	public static String getSystemPropertiesAsString(){
//
//		Properties sysProps = System.getProperties();
//
//		StringBuilder sb = new StringBuilder("System properties:\n");
//
//		for( String name : sysProps.stringPropertyNames() ){
//			sb.append("  ").append( name ).append(": ").append(System.getProperty(name)).append("\n");
//		}
//
//		return sb.toString();
//	}

}// class DebugUtils

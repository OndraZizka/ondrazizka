package antstructure;

import java.io.PrintStream;
import java.util.*;
import org.w3c.dom.*;



/**
 *
 * @author ondra
 */
public class TargetTreeWalker {
	
	// Owning analyzer
	BuildFileAnalyzer analyzer;
	
	// Parent targets stack.
	List<Target> parentTargets = new ArrayList();
	
	// Call-depth
	int iDepth = 0;
	

	
	// Const
	public TargetTreeWalker(BuildFileAnalyzer analyzer) {
		this.analyzer = analyzer;
	}
	
	
	
	
	/**
	 * 
	 * @param target
	 */
	protected void WalkTarget(Target target) {
		
		PrintStream out = System.out;
		
		Target[] subTargets = target.getDependTargets();
		
		out.print( getSpaces(iDepth*5) + "* "+target.getName() );
		if(  null != target.getDescription() && !"".equals(target.getDescription()))
			 out.print( " - " + target.getDescription() );
		out.println();
		

		this.iDepth++;
		
		String sSpaces = getSpaces(iDepth*5);
		
		for( Target subTarget : subTargets ){
			this.WalkTarget(subTarget);
		}
		

		// For each task of this target... 
		for( Task task : target.getTasks() ){
			
			String sTaskType = task.getName();
			
			if( "exec".equals(sTaskType) ){
				
				//<exec executable="${tck.svn.dir}/bin/tsant" dir="${tck.svn.dir}/bin">
				//	<arg value="clean.vi" />
				//	<env key="TS_HOME" value="${tck.svn.dir}" /> 
				//  ...
				out.println( sSpaces + String.format(
								"<exec executable=\"%s\" dir=\"%s\">", 
								task.getAttr().getProperty("executable"), 
								task.getAttr().getProperty("dir")
				) );
				
			}
			else if( "antcall".equals(sTaskType) ){

				// <antcall target="configure.backend" >
				/*Target calledTarget = 
				this.WalkTarget(calledTarget);
				 */
				String sTarget = task.getAttr().getProperty("target");
				out.println( sSpaces + String.format(
								"<antcall target=\"%s\">", 
								sTarget
				) );
				
			}
			else if( "ant".equals(sTaskType) ){
				
				// <ant antfile="initdb.xml" dir="${ts.home}/bin/xml" target="init.sybase"/>
				out.println( sSpaces + String.format(
								"<ant antfile=\"%s\" dir=\"%s\" target=\"%s\">", 
								task.getAttr().getProperty("antfile"), 
								task.getAttr().getProperty("dir"), 
								task.getAttr().getProperty("target")
				) );
				
			}
			else if( "java".equals(sTaskType) ){
				
				// <java classname="com.sun.javatest.tool.Main" failonerror="yes" fork="yes" dir="${ts.home}/src">
				out.println( sSpaces + String.format(
								"<java classname=\"%s\" dir=\"%s\" fork=\"%s\">", 
								task.getAttr().getProperty("classname"), 
								task.getAttr().getProperty("dir"), 
								task.getAttr().getProperty("fork")
				) );
				
			}// task type
			
			
		}// For each task of this target... 
		
		this.iDepth--;
		
	}// processTargetElement(Element eTarget)

	
	
	
	
	/**
	 * 
	 * @param iSpaces
	 * @return  A string with given number of spaces.
	 */
	private String getSpaces(int iSpaces){
		switch( iSpaces ){
			case 0: return "";
			case 1: return " ";
			case 2: return "  ";
			case 3: return "   ";
			case 4: return "    ";
			case 5: return "     ";
			case 6: return "      ";
			case 7: return "       ";
			case 8: return "        ";
			default:{
				StringBuilder sb = new StringBuilder(iSpaces);
				sb.append("        ");
				for (int i = 8; i < iSpaces; i++) {
					sb.append(" ");
				}
				return sb.toString();
			}
		}
	}// private String getSpaces(int iSpaces)
	
	
}// public class TargetTreeWalker
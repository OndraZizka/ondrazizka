/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package antstructure;

import java.util.*;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;


/**
 *
 * @author ondra
 */
public class BuildFileAnalyzer {
	
	
	
	// Props
	//List<Target> aoTargets = new ArrayList();	
	Map<String, Target> mapTargets = new HashMap();
	String sStartTarget = null;

	
	
	public Map<String, Target> getTargets() {		return mapTargets;	}
	public void setTargets(Map<String, Target> mapTargets) {		this.mapTargets = mapTargets;	}

	//public List<Target> getAoTargets() {		return aoTargets;	}
	//public void setAoTargets(List<Target> aoTargets) {		this.aoTargets = aoTargets;	}
	
	public String getDefaultTarget() {		return sStartTarget;	}
	public void setDefaultTarget(String defaultTarget) {		this.sStartTarget = defaultTarget;	}	
	
	private static final String sSpaces = "    ";
	
	
	
	

	
	
	
	/**
	 * Reads specified Ant build file.
	 * @param sFileName
	 * TODO: Throw exception, return void.
	 */
	protected  void readBuildFile( String sFileName ) throws Exception{
		
		File antScriptFile = new File(sFileName);
		if( !antScriptFile.exists() ){			
			throw new FileNotFoundException("File not found: "+sFileName);
		}
		
    //try{
			
			
			// XML stuff 
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
      factory.setNamespaceAware(false);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse( new File(sFileName) );

			// Root element
			Element root = doc.getDocumentElement();
	    NodeList nl = root.getChildNodes();
			
			// Default target
			if( root.hasAttribute("default")){
				this.setDefaultTarget( root.getAttribute("default") );
			}/*else if( null != sTargetSet ){
				this.setStartTarget( sTargetSet );
			}else{
				System.out.println("Error: No default target and no target set.");
			}*/
			
			
			// For each child node...
			for (int i=0;i<nl.getLength(); i++) {
				
				Node n = nl.item(i);
				if( n.getNodeType() != Node.ELEMENT_NODE) continue;
				
				Element e = (Element)n;
				
				String sTagName = e.getTagName();
				
				//System.out.println("\nTag: "+ sTagName);				
				if( "target".equals(sTagName) ){
					this.processTargetElement(e);
				}
				else if( "import".equals(sTagName) ){
					
				}
				
			}// for each child node...
			
			
			// Convert string references to Target references...
			System.out.println("Convert string references to Target references...");///
			
			Map<String, Target> map = this.getTargets();
			System.out.println("Total targets: "+map.size());///
			// For each target in the file...
			for( Target curTarget: map.values() ) {
				Target[] curSubTargets = new Target[curTarget.getDependTargetsNames().length];
				for( int i = 0; i < curSubTargets.length; i++ ){
					curSubTargets[i] = map.get( curTarget.getDependTargetsNames()[i] );
				}
				curTarget.setDependTargets(curSubTargets);
			}
			
//		}
//		catch(Exception e){
//			System.err.println(""+e.getClass());
//      e.printStackTrace();
//    }
		
	}// ReadBuildFile()
	
	

	
	/**
	 * 
	 * @param sStartTargetName
	 */
	protected void showTargetTree( String sStartTargetName ){
		
		// Process dependencies from specified target...
		System.out.println("Process dependencies from specified target "+sStartTargetName+"...");
		//String sStartTargetName = this.getStartTarget();

		Map<String, Target> map = this.getTargets();

		if( ! map.containsKey(sStartTargetName) ){
			System.out.println("Specified target not found: "+sStartTargetName);
		}else{
			Target startTarget = map.get(sStartTargetName);
			String[] asDependent = startTarget.getDependTargetsNames();

			TargetTreeWalker walker = new TargetTreeWalker(this);
			walker.WalkTarget( startTarget	);

			// TODO: Don't detail targets that were already listed (using an extra map)
		}
		
	}// showTargetTree( String sStartTargetName )

	
	
	
	
	
	/**
	 * 
	 * @param e
	 */
	protected void processTargetElement(Element eTarget) {
		
		PrintStream out = Main.processingMessagesStream;
		
		out.println("Target "+eTarget.getAttribute("name") + "; Depends on: " );///

		
		// --- Depends --- //
		String[] asDepends;
		if( !eTarget.hasAttribute("depends") ){
			out.print(sSpaces + "(nothing)");///
			asDepends = new String[0];
		}
		else{
			out.print( eTarget.getAttribute("depends") );
			String sDepends = eTarget.getAttribute("depends");
			asDepends = sDepends.split(",");

			for (int i = 0; i < asDepends.length; i++) {
				asDepends[i] = asDepends[i].trim();
				//out.print(sSpaces + asDepends[i]);///
			}
		}
		out.println();///
						
		
		// Target object
		Target target = new Target(eTarget.getAttribute("name"), asDepends);
		target.setDescription(eTarget.getAttribute("description"));
		//aoTargets.add( target );
		mapTargets.put(target.name, target);
		
		
		// Target's tasks
		List<Task> tasksOfTarget = new ArrayList();
		
		out.print("    Tasks: ");///
		NodeList nl = eTarget.getChildNodes();
		// For each task (target's child element)
		for (int i=0;i<nl.getLength(); i++) {
			Node n = nl.item(i);
			if( n.getNodeType() != Node.ELEMENT_NODE) continue;

			Element eChild = (Element)n;
			String sTagName = eChild.getTagName();
			out.print(" "+ sTagName);///
			
			if( "exec".equals(sTagName) ){
				
				//<exec executable="${tck.svn.dir}/bin/tsant" dir="${tck.svn.dir}/bin">
				//	<arg value="clean.vi" />
				//	<env key="TS_HOME" value="${tck.svn.dir}" /> 
				//  ...
				Task task = new Task(sTagName);
				task.getAttr().setProperty("dir", eChild.getAttribute("dir"));
				task.getAttr().setProperty("executable", eChild.getAttribute("executable"));
				tasksOfTarget.add(task);
			}
			else if( "antcall".equals(sTagName) ){

				// <antcall target="configure.backend" >
				Task task = new Task(sTagName);
				task.getAttr().setProperty("target", eChild.getAttribute("target"));
				tasksOfTarget.add(task);
			}
			else if( "ant".equals(sTagName) ){
				
				// <ant antfile="initdb.xml" dir="${ts.home}/bin/xml" target="init.sybase"/>
				Task task = new Task(sTagName);
				task.getAttr().setProperty("dir", eChild.getAttribute("dir"));
				task.getAttr().setProperty("antfile", eChild.getAttribute("antfile"));
				task.getAttr().setProperty("target", eChild.getAttribute("target"));
				tasksOfTarget.add(task);
			}
			else if( "java".equals(sTagName) ){
				
				// <java classname="com.sun.javatest.tool.Main" failonerror="yes" fork="yes" dir="${ts.home}/src">
				Task task = new Task(sTagName);
				String[] extract = new String[]{"classname","dir","fork"};
				for (String attrName : extract) {
					task.getAttr().setProperty(attrName, eChild.getAttribute(attrName));
				}				
				tasksOfTarget.add(task);
			}
			
		}// For each task (target's child element)
		out.println();
		
		target.setTasks(tasksOfTarget.toArray(new Task[0]));
		
	}// processTargetElement(Element eTarget)

}// class BuildFileAnalyzer
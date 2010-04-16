/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package antstructure;

import java.util.Properties;

/**
 *
 * @author ondra
 */
public class Task {
	
	// -- Props -- //
	final String name;
	final Properties attr = new Properties();

	
	// -- Const -- //
	public Task(String name) {
		this.name = name;
	}

	
	// Get / Set //
	public String getName() {		return name;	}
	public Properties getAttr() {		return attr;	}
	

}// public class Task

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package antstructure;

/**
 *
 * @author ondra
 */
public class Target {
	
	// Props
	public String name;
	public String description = "";
	public String[] depends = new String[0];
	public Target[] subTargets = new Target[0];
	private Task[] tasks;
	

	// Get / set 
	public String[] getDependTargetsNames() {		return depends;	}
	public void setDependTargetsNames(String[] depends) {		this.depends = depends;	}
	public String getName() {		return name;	}
	public void setName(String name) {		this.name = name;	}
	public String getDescription() {		return description;	}
	public void setDescription(String description) {		this.description = description;	}	
	public Target[] getDependTargets() {		return subTargets;	}
	public void setDependTargets(Target[] subTargets) {		this.subTargets = subTargets;	}
	public Task[] getTasks() {		return tasks;	}
	public void setTasks(Task[] tasks) {		this.tasks = tasks;	}	
	
	
	// Const 
	public Target(String name, String[] depends) {
		this.name = name;
		this.depends = depends;
	}
	
}

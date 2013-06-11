package org.edu.agh.utils;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FunctionObject {

	String id; 
	int argumentCount;
	String name;
	String description;
	
	public FunctionObject(){}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getArgumentCount() {
		return argumentCount;
	}

	public void setArgumentCount(int argumentCount) {
		this.argumentCount = argumentCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	
}

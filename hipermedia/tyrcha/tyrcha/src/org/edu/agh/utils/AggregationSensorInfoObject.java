package org.edu.agh.utils;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AggregationSensorInfoObject extends SensorInfoObject{
	
	
	private String id;
	private List<URI> arguments;
	private String type  = "aggregation";
	private FunctionObject function;
	
	public AggregationSensorInfoObject(){}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public List<URI> getArguments() {
		return arguments;
	}

	public void setArguments(List<URI> arguments) {
		this.arguments = arguments;
	}


	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}


	public FunctionObject getFunction() {
		return function;
	}


	public void setFunction(FunctionObject function) {
		this.function = function;
	}
	
	
	

}

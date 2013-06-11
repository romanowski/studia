package org.edu.agh.utils;

import java.net.URI;
import java.util.List;

public class MesuementObject {

	private String id;
	private URI sensor;
	private String measure;
	private String dataType;
	
	private List<Object> result;
	
	public MesuementObject(){}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public URI getSensor() {
		return sensor;
	}

	public void setSensor(URI sensor) {
		this.sensor = sensor;
	}

	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public List<Object> getResult() {
		return result;
	}

	public void setResult(List<Object> result) {
		this.result = result;
	}

	


	
	
}

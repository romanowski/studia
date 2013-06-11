package org.edu.agh.utils;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SubscriptionObject {

	private String id;
	private String user;
	private String name;
	private List<URI> sensors; 
	
	public SubscriptionObject(){}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<URI> getSensors() {
		return sensors;
	}

	public void setSensors(List<URI> sensors) {
		this.sensors = sensors;
	}
	
	
	
}

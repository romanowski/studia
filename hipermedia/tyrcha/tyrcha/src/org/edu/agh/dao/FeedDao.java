package org.edu.agh.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.edu.agh.utils.SensorInfoObject;

public class FeedDao {
	List<SensorInfoObject> feeds;

	Map<String,List<Object>> measurements;
	
	private static FeedDao _soleInstance;
	int idnumber;
	
	private FeedDao(){
		feeds = new ArrayList<SensorInfoObject>();
		measurements = new HashMap<String,List<Object>>();
		idnumber = 0;
	}
	
	public static FeedDao getInstance(){
		if (_soleInstance == null){
			_soleInstance = new FeedDao();
		}
		return _soleInstance;
	}

	public List<SensorInfoObject> getFeeds() {
		return feeds;
	}

	public void setFeeds(List<SensorInfoObject> feeds) {
		this.feeds = feeds;
	}

	public String getNewId() {
		idnumber++;
		return "" +idnumber;
	}

	public Map<String, List<Object>> getMeasurements() {
		return measurements;
	}

	public void setMeasurements(Map<String, List<Object>> measurements) {
		this.measurements = measurements;
	}

}

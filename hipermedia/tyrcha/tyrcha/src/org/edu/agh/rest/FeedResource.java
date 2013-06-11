package org.edu.agh.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.edu.agh.dao.FeedDao;
import org.edu.agh.utils.AggregationSensorInfoObject;
import org.edu.agh.utils.MesuementObject;
import org.edu.agh.utils.SensorInfoObject;


@Path("/sensor")
public class FeedResource {

	FeedDao feedDao = FeedDao.getInstance();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<SensorInfoObject> getFeeds(){
		return feedDao.getFeeds();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createFeed(SensorInfoObject feed){	
		for (SensorInfoObject f : feedDao.getFeeds()){
			if (f.getId().equals(feed.getId())){
				feedDao.getFeeds().remove(f);
				feedDao.getFeeds().add(feed);
				return Response.status(201).entity("Feed updated").build();
			}
		}

		feed.setId(feedDao.getNewId());
		
		if (feed.getType().equals("aggregation")){
			List<URI> toSet = new ArrayList<URI>();
			
			for (URI uri : ((AggregationSensorInfoObject)feed).getArguments()){
				try {
					toSet.add(new URI("/sensor/" + feed.getId()));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}
		
		feedDao.getFeeds().add(feed);
		return Response.status(201).entity("Feed created").build();
	}


	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteFeed(SensorInfoObject feed){
		if (feedDao.getFeeds().contains(feed)){
			feedDao.getFeeds().remove(feed);
			return Response.status(201).entity("Feed deleted").build();
		}
		return Response.status(400).entity("Specified feed does not exist").build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public MesuementObject getFeed(@PathParam("id") String id,int skip){
		
		SensorInfoObject sensor = null;
		
		for (SensorInfoObject feed : feedDao.getFeeds()){
			if (feed.getId().equals(id)){
				sensor = feed;
				break;
			}
		}
		if (sensor == null){ return null; }
		
		MesuementObject toRet = new MesuementObject();
		toRet.setId(sensor.getId());
		toRet.setDataType(sensor.getDataType());
		toRet.setSensor(null); //TODO - add it later !!
		toRet.setMeasure(sensor.getMeasure());
		
		
		List<Object> mes = feedDao.getMeasurements().get(sensor.getId());
		
		toRet.setResult(mes.subList(skip * 2, mes.size()));
		
		return toRet;
		
		
	}

	@POST	//Dodawanie pomiaru do listy z pomiarami
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public void addMeasure (@PathParam("id") String id,Object measure) {
		List<Object> measurements = feedDao.getMeasurements().get(id);
		if (measurements == null){
			measurements = new ArrayList<Object>();
			feedDao.getMeasurements().put(id, measurements);
		}
		//
		measurements.add(new Date());
		measurements.add(measure);
	}
	
	
	
}

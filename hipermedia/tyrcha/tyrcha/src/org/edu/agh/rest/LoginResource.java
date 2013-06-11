package org.edu.agh.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/login")
public class LoginResource {

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(String login,String password){
		
		//TODO - add login method
		
		return Response.status(201).entity("User logged in successfuly").build();
	}
	
}

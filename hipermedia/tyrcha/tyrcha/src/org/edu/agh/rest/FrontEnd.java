package org.edu.agh.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/*
 Klasa przechowujaca liste Klas obslugujacych Resta( zawierajacych metody @GET @POST)
 */

public class FrontEnd extends Application {

    public Set<Class<?>> getClasses() {
        Set<Class<?>> rrcs = new HashSet<Class<?>>();
        rrcs.add(FeedResource.class);
        rrcs.add(LoginResource.class);
        return rrcs;
    }
}

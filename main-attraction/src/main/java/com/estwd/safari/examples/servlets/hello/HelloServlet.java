package com.estwd.safari.examples.servlets.hello;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by development on 13/03/16.
 */
@Path("/hello")
public class HelloServlet {

    private final String MESSAGE = "Hey, This is the Jersey JAX-RS Safari park - welcome!" +
            "(... and please don't feed the animals)";

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
        return MESSAGE;
    }
}

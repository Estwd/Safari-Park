package com.estwd.safari.examples.servlets.hello;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A demo servlet in the Safari-park test application.
 *
 * @author Guni Y.
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

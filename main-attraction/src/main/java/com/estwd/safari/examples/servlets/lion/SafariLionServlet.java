package com.estwd.safari.examples.servlets.lion;


import org.apache.shiro.util.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A servlet for testing the uses of the Safari-park Lion utilities.
 *
 * @author Guni Y.
 */
@Path("/lion")
public class SafariLionServlet {

    private static final Logger log = LoggerFactory.getLogger(SafariLionServlet.class);

    public SafariLionServlet() {
        log.info("Started the Safari-Lion servlet");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "This service is used to demonstrate the use of Zookeeper as a sessions-store for the Apache Shiro library";
    }

    @GET
    @Path("/login/{username}")
    @Produces(MediaType.TEXT_PLAIN)
    public String login(@PathParam("username") final String username) {
        return "The session ID created for this session is:" + ThreadContext.getSubject().getSession().getId();
    }

    @GET
    @Path("/user/{sessionId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUserName(@PathParam("sessionId") final String sessionId) {
        return "The user that created this session is:" + ThreadContext.getSubject().getPrincipal();
    }
}

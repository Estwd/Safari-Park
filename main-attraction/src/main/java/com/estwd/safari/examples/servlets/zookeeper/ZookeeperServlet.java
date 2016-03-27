package com.estwd.safari.examples.servlets.zookeeper;

import com.netflix.config.DynamicPropertyFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by development on 19/03/16.
 */
@Path("/zk")
public class ZookeeperServlet {

    private final static String VALUE_FOUND = "The value for the ZNode '%s' is: %s";
    private final static String NO_VALUE_FOUND = "No value was found for the ZNode '%s'";

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "This service is used to return values of ZNodes watched by the service";
    }

    @GET
    @Path("/{zNode}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getZNodeValue(@PathParam("zNode") final String zNode) {
        String value = DynamicPropertyFactory.getInstance().getStringProperty(zNode, null).getValue();
        return String.format(value == null ? NO_VALUE_FOUND : VALUE_FOUND, zNode, value);
    }
}

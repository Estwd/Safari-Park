package com.estwd.safari.examples.servlets.leopard;

import com.netflix.config.DynamicPropertyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A servlet for testing the uses of the Safari-park Leopard utilities
 *
 * @author Guni Y.
 */
@Path("/leopard")
public class SafariLeopardServlet {

    private static final String VALUE_FOUND = "The value for the ZNode '%s' is: %s";
    private static final String NO_VALUE_FOUND = "No value was found for the ZNode '%s'";

    private static final Logger log = LoggerFactory.getLogger(SafariLeopardServlet.class);

    public SafariLeopardServlet() {
        log.info("Started the Safari-Leopard servlet");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "This service is used to return values of ZNodes watched as dynamic configuration keys" +
                "(using the Netflix Archaius library)";
    }

    @GET
    @Path("/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getZNodeConfigurationValue(@PathParam("key") final String key) {
        String value = DynamicPropertyFactory.getInstance().getStringProperty(key, null).getValue();
        return String.format(value == null ? NO_VALUE_FOUND : VALUE_FOUND, key, value);
    }
}

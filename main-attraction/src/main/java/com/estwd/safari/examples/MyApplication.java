package com.estwd.safari.examples;

import com.estwd.safari.leopard.recipes.distributed.DistributedSystemSubjectBuilder;
import com.estwd.safari.utils.SubjectsConfigurationManager;
import com.estwd.safari.utils.subject.ConfigurationSubject;
import com.netflix.config.DynamicPropertyFactory;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * A {@link ResourceConfig} that install the dynamic configuration for a demo
 * and distributed system values.
 *
 * @author Guni Y.
 */
@ApplicationPath("/")
public class MyApplication extends ResourceConfig {

    private static final String ENV_NAME = "demo.e";
    private static final String SERVICE_NAME = "demo.s";
    private static final String INSTANCE_NAME = "demo.i";
    private static final String CONNECTION_STRING_KEY = "zk.connection.string";

    public MyApplication() {
        init();
        packages("com.estwd.safari.examples.servlets");
    }

    private void init() {
        ConfigurationSubject configurationSubject = getDynamicConfigurationSubject();
        configurationSubject = configurationSubject.add(ConfigurationSubject.createSingleSubject("/demo", "Demo values"));

        SubjectsConfigurationManager.install(configurationSubject, getSimpleCuratorFramework());
    }

    private static ConfigurationSubject getDynamicConfigurationSubject() {
        return new DistributedSystemSubjectBuilder()
                .global().environment(ENV_NAME).service(SERVICE_NAME).instance(INSTANCE_NAME)
                .build();
    }

    private static CuratorFramework getSimpleCuratorFramework() {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .retryPolicy(new RetryOneTime(1000))
                .ensembleProvider(new FixedEnsembleProvider(getConnectionString()))
                .build();

        client.start();

        return client;
    }

    private static String getConnectionString() {
        return DynamicPropertyFactory.getInstance().getStringProperty(CONNECTION_STRING_KEY, null).get();
    }
}

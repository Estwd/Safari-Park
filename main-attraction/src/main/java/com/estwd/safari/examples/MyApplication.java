package com.estwd.safari.examples;

import com.estwd.safari.leopard.recipes.distributed.DistributedSystemSubject;
import com.estwd.safari.utils.SafariParkConfigurationManager;
import com.estwd.safari.utils.subject.ConfigurationSubject;
import com.estwd.safari.utils.subject.SingleSubject;
import com.netflix.config.DynamicPropertyFactory;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * Created by development on 14/03/16.
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
        configurationSubject = ConfigurationSubject.append(configurationSubject, new SingleSubject("/demo", "Demo values"));

        SafariParkConfigurationManager.init(configurationSubject, getSimpleCuratorFramework());
    }

    private static ConfigurationSubject getDynamicConfigurationSubject() {
        return DistributedSystemSubject.builder().global().environment(ENV_NAME).service(SERVICE_NAME).instance(INSTANCE_NAME).build();
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

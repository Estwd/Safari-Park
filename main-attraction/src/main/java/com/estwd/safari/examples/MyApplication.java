package com.estwd.safari.examples;

import com.estwd.safari.leopard.SubjectsConfigurationManager;
import com.estwd.safari.leopard.recipes.distributed.DistributedSystemSubjectBuilder;
import com.estwd.safari.leopard.subject.ConfigurationSubject;
import com.estwd.safari.lion.shiro.DefaultZkSessionCache;
import com.netflix.config.DynamicPropertyFactory;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.shiro.session.mgt.SimpleSession;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * A {@link ResourceConfig} that install the dynamic configuration for a demo
 * and distributed system values. It also defines an implementation of a {@link
 * DefaultZkSessionCache} to be used as a Shiro session data store cache (see the
 * shiro.ini file).
 *
 * @author Guni Y.
 * @see ResourceConfig
 * @see DefaultZkSessionCache
 */
@ApplicationPath("/")
public class MyApplication extends ResourceConfig {

    private static final String DEMO_ROOT_NODE = "/demo";
    private static final String DEMO_NODE_DESCRIPTION = "Demo values";

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
        configurationSubject = configurationSubject.add(ConfigurationSubject.createSingleSubject(DEMO_ROOT_NODE, DEMO_NODE_DESCRIPTION));

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

    public static class SimpleZkSessionCache<K, V extends SimpleSession> extends DefaultZkSessionCache<K, V> {

        private static final String STORE_ROOT_PATH = "/shiro";
        private static final CuratorFramework CURATOR_FRAMEWORK = getSimpleCuratorFramework();
        private static final int TEST_MAX_SHARD_SIZE = 3;

        public SimpleZkSessionCache() {
            //Create the root ZNode if it doesn't exist
            getZkDal().save(STORE_ROOT_PATH, "");
        }

        @Override
        protected CuratorFramework getCuratorFramework() {
            return CURATOR_FRAMEWORK;
        }

        @Override
        protected String getRootPath() {
            return STORE_ROOT_PATH;
        }

        @Override
        protected int getMaxShardSize() {
            return TEST_MAX_SHARD_SIZE;
        }
    }
}

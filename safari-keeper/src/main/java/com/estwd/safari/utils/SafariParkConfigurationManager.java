package com.estwd.safari.utils;

import com.estwd.safari.utils.subject.ConfigurationSubject;
import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicWatchedConfiguration;
import com.netflix.config.source.ZooKeeperConfigurationSource;
import org.apache.curator.framework.CuratorFramework;

/**
 * Created by development on 21/03/16.
 */
public class SafariParkConfigurationManager {

    final static ConcurrentCompositeConfiguration compositeConfig = new ConcurrentCompositeConfiguration();

    public static void init(ConfigurationSubject subject, CuratorFramework curatorFramework) {
        subject.getConfigurationNodes().stream()
                .forEachOrdered(zNode -> add(zNode.getPath(), zNode.getDescription(), curatorFramework));
        ConfigurationManager.install(compositeConfig);
    }

    private static void add(String configSubjectPath, String description, CuratorFramework curatorFramework) {
        ZooKeeperConfigurationSource zkConfigSource = new ZooKeeperConfigurationSource(curatorFramework, configSubjectPath);

        try {
            zkConfigSource.start();
        } catch (Exception exc) {
            throw new IllegalStateException(exc);
        }

        DynamicWatchedConfiguration zkDynamicOverrideConfig = new DynamicWatchedConfiguration(zkConfigSource);
        compositeConfig.addConfiguration(zkDynamicOverrideConfig, description);
    }
}

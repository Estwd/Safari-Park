package com.estwd.safari.utils;

import com.estwd.safari.utils.subject.ConfigurationSubject;
import com.estwd.safari.utils.subject.ZNode;
import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicWatchedConfiguration;
import com.netflix.config.source.ZooKeeperConfigurationSource;
import org.apache.curator.framework.CuratorFramework;

/**
 * A configuration manager that uses a {@link ConfigurationSubject} to set
 * configurations.
 * <p>
 * Note: This class is not thread safe. It was not designed as such because the
 * registration of watches should be made at a clear and readable location in
 * the thread invoking it. The purpose of this class is enabling that clarity
 * and allowing other options would have hampered it.
 *
 * @author Guni Y.
 * @see ConfigurationManager
 */
public final class SubjectsConfigurationManager {

    private final static ConcurrentCompositeConfiguration compositeConfig = new ConcurrentCompositeConfiguration();

    /**
     * Install the system wide configuration with the ConfigurationManager, and
     * watch the ZNodes in the configuration subject.
     * <p>
     * The ConfigurationManager will watch all the {@link ZNode}s in the
     * configuration subject according to their precedence order, using a Curator
     * client configured by the curatorFramework parameter.
     * <p>
     * This call can be made only once, otherwise IllegalStateException will be
     * thrown.
     *
     * @param subject          a {@link ConfigurationSubject} containing the
     *                         ZNodes to be watched ordered by precedence
     * @param curatorFramework a {@link CuratorFramework} for configuring the
     *                         Curator Zookeeper client
     * @throws IllegalArgumentException if the ZNodes in the subject are
     *                                  missing from Zookeeper or are invalid
     * @throws IllegalStateException    if this call is made more than once
     * @see ConfigurationManager
     */
    public static void install(ConfigurationSubject subject, CuratorFramework curatorFramework) {
        subject.getConfigurationZNodes().stream()
                .forEachOrdered(zNode -> add(zNode.getPath(), zNode.getDescription(), curatorFramework));
        ConfigurationManager.install(compositeConfig);
    }

    private static void add(String configSubjectPath, String description, CuratorFramework curatorFramework) {
        ZooKeeperConfigurationSource zkConfigSource = new ZooKeeperConfigurationSource(curatorFramework, configSubjectPath);

        try {
            zkConfigSource.start();
        } catch (Exception exc) {
            throw new IllegalArgumentException(exc);
        }

        DynamicWatchedConfiguration zkDynamicOverrideConfig = new DynamicWatchedConfiguration(zkConfigSource);
        compositeConfig.addConfiguration(zkDynamicOverrideConfig, description);
    }
}

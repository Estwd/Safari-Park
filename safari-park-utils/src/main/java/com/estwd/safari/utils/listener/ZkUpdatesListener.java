package com.estwd.safari.utils.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;

/**
 * An abstract {@link TreeCache} wrapper class.
 *
 * @author Guni Y.
 * @see TreeCache
 */
public class ZkUpdatesListener {

    final TreeCache treeCache;
    final String rootPath;

    /**
     * Creates the treeCache using the CuratorFramework client and ZK root path
     * node for the config.
     *
     * @param client    Curator client
     * @param rootPath  Path of the root Znode for the session data store
     */
    ZkUpdatesListener(CuratorFramework client, String rootPath) {
        treeCache = new TreeCache(client, rootPath);
        this.rootPath = rootPath;
    }

    /**
     * Starts the cache-management background thread
     *
     * @throws Exception
     */
    public void start() throws Exception {
        treeCache.start();
    }
}

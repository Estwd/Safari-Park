package com.estwd.safari.utils.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ZkUpdatesListener} that updates a single {@link UpdatesListener} on
 * any update.
 *
 * @author Guni Y.
 * @see ZkUpdatesListener
 * @see UpdatesListener
 */
public class SingleZkUpdatesListener extends ZkUpdatesListener {
    private static final Logger logger = LoggerFactory.getLogger(SingleZkUpdatesListener.class);

    private final UpdatesListener updatesListener;

    /**
     * Creates the treeCache using the CuratorFramework client and ZK root path node for the config
     *
     * @param client Curator client
     */
    public SingleZkUpdatesListener(CuratorFramework client, String rootPath, UpdatesListener updatesListener) {
        super(client, rootPath);
        this.updatesListener = updatesListener;
        addListener();
    }

    /**
     * Adds a listener to the treeCache
     */
    private void addListener() {
        treeCache.getListenable().addListener((aClient, event) -> {
            ChildData data = event.getData();

            if (data != null) {
                String path = data.getPath();
                if (!updatesListener.isUpdateData(path))
                    return;

                TreeCacheEvent.Type eventType = event.getType();
                logger.info("Received a Zookeeper update (path={}) of type:{}", path, eventType);

                switch (eventType) {
                    case NODE_ADDED:
                        updatesListener.dataAdded(path, data.getData());
                        break;
                    case NODE_UPDATED:
                        updatesListener.dataUpdated(path, data.getData());
                        break;
                    case NODE_REMOVED:
                        updatesListener.dataRemoved(path);
                        break;
                }
            }
        });
    }
}
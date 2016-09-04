package com.estwd.safari.utils.listener;

/**
 * An interface that is used by an implementation of {@link ZkUpdatesListener}
 * to receive Zookeeper data updates.
 *
 * @author Guni Y.
 * @see ZkUpdatesListener
 */
public interface UpdatesListener {
    boolean isUpdateData(String path);
    void dataUpdated(String path, byte[] data);
    void dataAdded(String path, byte[] data);
    void dataRemoved(String path);
}
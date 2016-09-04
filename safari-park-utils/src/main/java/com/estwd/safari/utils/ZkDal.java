package com.estwd.safari.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundPathAndBytesable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * An abstract Data Access Layer for the Zookeeper data base. Throws a runtime
 * exception when a request to the DB fails.
 *
 * @author Guni Y.
 */
public abstract class ZkDal {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final CuratorFramework framework;

    public ZkDal(CuratorFramework framework) {
        this.framework = framework;
        if (framework == null)
            throw new NullPointerException("The Curator framework cannot be null");
    }

    public void save(String path, String data) {
        save(path, data.getBytes(CHARSET), isExists(path));
    }

    public void save(String path, byte[] dataInBytes, boolean isExists) {
        BackgroundPathAndBytesable saveBuilder = isExists ? framework.setData() : framework.create();
        try {
            saveBuilder.forPath(path, dataInBytes);
        } catch (Exception e) {
            throw getDbException(isExists ? "Update" : "create", e);
        }
    }

    public void delete(String path) {
        try {
            framework.delete().inBackground().forPath(path);
        } catch (Exception e) {
            throw getDbException("Delete", e);
        }
    }

    public byte[] get(String path) {
        try {
            return isExists(path) ? framework.getData().forPath(path) : null;
        } catch (Exception e) {
            throw getDbException("get", e);
        }
    }

    private boolean isExists(String path) {
        try {
            return null != framework.checkExists().forPath(path);
        } catch (Exception e) {
            throw getDbException("Check-exists", e);
        }
    }

    protected abstract RuntimeException getDbException(String action, Exception e);
}


package com.estwd.safari.lion.store;

import com.estwd.safari.utils.ZkDal;
import com.estwd.safari.utils.listener.UpdatesListener;
import org.apache.shiro.session.mgt.SimpleSession;

import java.util.Collection;

/**
 * An abstract {@link UpdatesListener} that also updates the session store by
 * using a {@link ZkDal} and serializes/deserializes the data using a {@link
 * SerializationUtility}.
 *
 * @author Guni Y.
 * @see UpdatesListener
 * @see ZkDal
 * @see SerializationUtility
 */
public abstract class ZkSessionStore<K, V extends SimpleSession> implements UpdatesListener {

    protected final String rootPath;
    protected final ZkDal zkDal;
    private final SerializationUtility<V> serializationUtility;

    protected ZkSessionStore(String rootPath, ZkDal zkDal, SerializationUtility<V> serializationUtility) {
        this.rootPath = rootPath;
        this.zkDal = zkDal;
        this.serializationUtility = serializationUtility;
    }

    protected String getSessionId(String path) {
        return path.substring(path.lastIndexOf('/') + 1);
    }

    public boolean isUpdateData(String path) {
        return !path.equals(rootPath);
    }

    protected V deserializeSession(byte[] data) {
        return serializationUtility.deserialize(data);
    }

    protected byte[] serializeSession(V session) {
        return serializationUtility.serialize(session);
    }

    public abstract V getSession(K id);
    public abstract Collection<V> getSessions();
    public abstract void updateSession(K id, V session, boolean isExists);
    public abstract void deleteSession(K id);
}

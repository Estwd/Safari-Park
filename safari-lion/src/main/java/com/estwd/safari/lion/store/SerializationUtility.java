package com.estwd.safari.lion.store;

import org.apache.shiro.session.mgt.SimpleSession;

/**
 * An abstract facade for a serialization utility that is used by a {@link
 * ZkSessionStore} to serialize its data to/from a bytes array.
 *
 * @author Guni Y.
 * @see ZkSessionStore
 */
public abstract class SerializationUtility<V extends SimpleSession> {

    protected abstract V deserialize(byte[] data);

    protected abstract byte[] serialize(V session);
}

package com.estwd.safari.lion.shiro;

import com.estwd.safari.lion.store.SerializationUtility;
import com.estwd.safari.lion.store.ShardedZkSessionStore;
import com.estwd.safari.lion.store.ZkSessionStore;
import com.estwd.safari.utils.ZkDal;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.session.mgt.SimpleSession;

/**
 * An abstract implementation of the {@link ZkSessionCache} that uses a {@link
 * ShardedZkSessionStore} to manage the Shiro's Zookeeper session data store in
 * shards (because of ZNodes size limits, as described in
 * <a href="https://cwiki.apache.org/confluence/display/CURATOR/TN4">this link</a>).
 * <p>
 * The size used by default is 8000 sessions per shard (this includes active
 * sessions and expired sessions that were not yet purged). If you experience
 * issues with this size in your environment please reimplement {@link
 * #getMaxShardSize} to use a lower size limit.
 * <p>
 * This implementation uses a {@link SerializationUtils} to serialize/deserialize
 * data to/from the Zookeeper cluster.
 * <p>
 * Note: Each sessions takes about 200 bytes, so storing a million sessions would
 * take about 200MB - not just in the Zookeeper cluster, but also in the RAM
 * used by each process that uses this cache. You can infer the limitation that
 * puts on using this cache (without changes) in your environment (e.g. if you
 * will have at most 100,000 sessions in your environment, than the memory
 * required is at most about 20MB).
 *
 * @author Guni Y.
 * @see ZkSessionCache
 * @see ShardedZkSessionStore
 * @see SerializationUtils
 */
public abstract class DefaultZkSessionCache<K, V extends SimpleSession> extends ZkSessionCache<K, V> {

    private final static int DEFAULT_MAX_SHARD_SIZE = 8000;
    private static final String ZK_EXCEPTION = "Zookeeper Exception in %s.";

    protected ZkDal getZkDal() {
        return new ZkDal(getCuratorFramework()) {
            @Override
            protected RuntimeException getDbException(String action, Exception e) {
                return new CacheException(String.format(ZK_EXCEPTION, action), e);
            }
        };
    }

    protected SerializationUtility<V> getSerializationUtility() {
        return new SerializationUtility<V>() {
            @Override
            protected V deserialize(byte[] data) {
                return SerializationUtils.deserialize(data);
            }

            @Override
            protected byte[] serialize(V session) {
                return SerializationUtils.serialize(session);
            }
        };
    }

    protected int getMaxShardSize() {
        return DEFAULT_MAX_SHARD_SIZE;
    }

    @Override
    protected ZkSessionStore<K, V> getZkSessionStore() {
        return new ShardedZkSessionStore<>(getRootPath(),
                getZkDal(), getSerializationUtility(), getMaxShardSize());
    }
}

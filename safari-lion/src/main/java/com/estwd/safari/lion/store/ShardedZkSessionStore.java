package com.estwd.safari.lion.store;

import com.estwd.safari.utils.ZkDal;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * An implementation of a {@link ZkSessionStore} that manages it's data in
 * shards of a maximum size (this size might be marginally passed, if more than
 * one thread create a session in the same time in an almost full shard).
 * <p>
 * The shards themselves are Znodes under the root with an ascending numerical
 * value (starting at 1).
 *
 * @author Guni Y.
 * @see ZkSessionStore
 */
public class ShardedZkSessionStore<K, V extends SimpleSession> extends ZkSessionStore<K, V> {

    private final String shardNodeFormat = rootPath + "/%d";
    private final String sessionNodeFormat = shardNodeFormat + "/%s";
    private final ConcurrentHashMap<String, ShardedSession<V>> sessions;
    private final int maxShardSize;
    private final HashMap<Integer, AtomicInteger> shards = new HashMap<>();

    public ShardedZkSessionStore(String sessionStoreRootPath,
                                 ZkDal zkDal,
                                 SerializationUtility serializationUtility,
                                 int maxShardSize) {
        super(sessionStoreRootPath, zkDal, serializationUtility);
        this.maxShardSize = maxShardSize;
        sessions = new ConcurrentHashMap<>();
    }


    @Override
    public boolean isUpdateData(String path) {
        return path.lastIndexOf('/') != rootPath.length() && super.isUpdateData(path);
    }

    @Override
    public void dataAdded(String path, byte[] data) {
        int shard = getShared(path);

        shards.putIfAbsent(shard, new AtomicInteger());
        shards.get(shard).getAndIncrement();

        dataUpdated(path, data);
    }

    @Override
    public void dataUpdated(String path, byte[] data) {
        sessions.put(getSessionId(path), new ShardedSession<>(getShared(path), deserializeSession(data)));
    }

    @Override
    public void dataRemoved(String path) {
        shards.get(getShared(path)).decrementAndGet();
        sessions.remove(getSessionId(path));
    }

    @Override
    public V getSession(K id) {
        ShardedSession<V> session = getShardedSession(id);
        return session == null ? null : session.getSession();
    }

    @Override
    public Collection<V> getSessions() {
        Collection<V> values = sessions.values().stream()
                .map(ShardedSession::getSession)
                .collect(Collectors.toList());
        return CollectionUtils.isEmpty(values) ?
                Collections.emptySet() :
                Collections.unmodifiableCollection(values);
    }

    @Override
    public void updateSession(K id, V session, boolean isExists) {
        int shard;
        if (isExists) {
            ShardedSession<V> shardedSession = getShardedSession(id);
            if (shardedSession == null)
                throw new UnknownSessionException("Session was logged out - it cannot be updated");
            shard = shardedSession.getShard();
        } else {
            //find an appropriate shard or create a new one
            for (shard = 1; isShardFull(shard); shard++);

            if (shard > shards.size()) {
                String shardZnodePath = String.format(shardNodeFormat, shard);
                zkDal.save(shardZnodePath, "");
            }
        }
        zkDal.save(getSessionPath(shard, id), serializeSession(session), isExists);
        sessions.put(id.toString(), new ShardedSession<>(shard, session));
    }

    @Override
    public void deleteSession(K id) {
        zkDal.delete(getSessionPath(sessions.get(id).getShard(), id));
        sessions.remove(id);
    }

    private ShardedSession<V> getShardedSession(K id) {
        if (sessions == null)
            return null;
        ShardedSession<V> session = sessions.get(id.toString());
        if (session == null) {
            for (int shard = 1; shard <= shards.size(); shard++) {
                byte[] sessionData = zkDal.get(String.format(sessionNodeFormat, shard, id));
                if (sessionData != null) {
                    session = new ShardedSession<>(shard, deserializeSession(sessionData));
                    sessions.put(id.toString(), session);
                }
            }
        }
        return session;
    }

    private boolean isShardFull(int i) {
        return i <= shards.size()
                && shards.get(i) != null
                && shards.get(i).get() >= maxShardSize;
    }

    private static int getShared(String pathWithSessionId) {
        String path = pathWithSessionId.substring(0, pathWithSessionId.lastIndexOf('/'));
        String shard = path.substring(path.lastIndexOf('/') + 1);
        return Integer.parseInt(shard);
    }

    private String getSessionPath(int shard, Object id) {
        return String.format(sessionNodeFormat, shard, id);
    }

    private class ShardedSession<S extends SimpleSession> {
        private final int shard;
        private final S session;

        public ShardedSession(int shard, S session) {
            this.shard = shard;
            this.session = session;
        }

        public int getShard() {
            return shard;
        }

        public S getSession() {
            return session;
        }
    }
}

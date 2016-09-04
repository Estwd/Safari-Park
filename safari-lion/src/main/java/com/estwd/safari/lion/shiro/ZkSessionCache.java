package com.estwd.safari.lion.shiro;

import com.estwd.safari.lion.store.ZkSessionStore;
import com.estwd.safari.utils.listener.SingleZkUpdatesListener;
import com.estwd.safari.utils.listener.ZkUpdatesListener;
import org.apache.curator.framework.CuratorFramework;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;

/**
 * An abstract Shiro {@link Cache} that manages and listens to a Zookeeper
 * session data store.
 *
 * @author Guni Y.
 * @see Cache
 */
public abstract class ZkSessionCache<K, V extends SimpleSession> implements Cache<K, V> {

    private static final Logger log = LoggerFactory.getLogger(ZkSessionCache.class);

    private final ZkUpdatesListener sessionStoreListener;
    private final ZkSessionStore<K, V> sessionStore;

    public ZkSessionCache() {
        sessionStore = getZkSessionStore();
        sessionStoreListener = new SingleZkUpdatesListener(getCuratorFramework(),
                getRootPath(), sessionStore);
        try {
            sessionStoreListener.start();
        } catch (Exception e) {
            throw new CacheException("Could not start a new Zookeeper listener", e);
        }
    }

    @Override
    public V get(K sessionId) throws CacheException {
        log.info("get() - sessionId = {}", sessionId);
        return sessionStore.getSession(sessionId);
    }

    @Override
    public V put(K sessionId, V session) {
        log.info("put() - sessionId = {}", sessionId);
        log(sessionId, session);
        V previousSession = sessionStore.getSession(sessionId);

        sessionStore.updateSession(sessionId, session, (previousSession != null));

        return previousSession;
    }

    @Override
    public V remove(K sessionId) {
        log.info("remove() - sessionId = {}", sessionId);
        V previousSession = sessionStore.getSession(sessionId);
        if (previousSession != null) {
            log(sessionId, previousSession);
            sessionStore.deleteSession(sessionId);
        }

        return previousSession;
    }

    @Override
    public void clear() throws CacheException {
        throw new UnsupportedOperationException("ZkSessionCache.clear() - not implemented");
    }

    @Override
    public int size() {
        log.info("size()");
        return values().size();
    }

    @Override
    public Set<K> keys() {
        throw new UnsupportedOperationException("ZkSessionCache.keys() - not implemented");
    }

    @Override
    public Collection<V> values() {
        log.info("values()");
        return sessionStore.getSessions();
    }

    private static final String SESSION_LOG_FORMAT = "\n\t\tSession Data: id = %s" +
            "\n\t\tHost: %s, Start Time: %s, Timeout: %s (%s & %s), Last Access Time: %s" +
            "\n\t\tSession Attributes: %s";

    private void log(K key, SimpleSession session) {
        String message =
                String.format(SESSION_LOG_FORMAT, key, session.getHost(),
                        session.getStartTimestamp(), session.getTimeout(),
                        session.isExpired() ? "expired" : "not expired",
                        session.isValid() ? "valid" : "not valid",
                        session.getLastAccessTime(),
                        session.getAttributes() == null ? "None" : session.getAttributes());
        log.info(message);
    }

    abstract protected CuratorFramework getCuratorFramework();
    abstract protected String getRootPath();
    abstract protected ZkSessionStore<K, V> getZkSessionStore();
}

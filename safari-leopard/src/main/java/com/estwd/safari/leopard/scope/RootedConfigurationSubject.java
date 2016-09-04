package com.estwd.safari.leopard.scope;

import com.estwd.safari.leopard.subject.ConfigurationSubject;
import com.estwd.safari.leopard.subject.ZNode;
import org.apache.curator.utils.PathUtils;

import java.util.LinkedHashSet;

/**
 * {@link ConfigurationSubject} in which all the {@link ZNode}s share a common
 * root.
 * <p>
 * Any implementation must make sure that the root returned by
 * {@link #getRootPath} is a valid ZNode that is actually shared by all the
 * paths of the ZNodes returned by {@link #getConfigurationZNodes}, unless no
 * paths are returned.
 * <p>
 * Note: This class is not thread safe. It was not designed as such because the
 * registration of watches should be made at a clear and readable location in
 * the thread invoking it. The purpose of this class is enabling that clarity
 * and allowing other options would have hampered it.
 *
 * @author Guni Y.
 */
public interface RootedConfigurationSubject extends ConfigurationSubject {

    /**
     * Returns a new {@link RootedConfigurationSubject} which returns an empty
     * set when its {@link #getConfigurationZNodes} method is invoked and the
     * rootPath argument when its {@link #getRootPath} method is invoked.
     *
     * @param rootPath a path to a ZNode
     * @return a ConfigurationSubject containing an
     * empty set of {@link ZNode}s
     * @throws IllegalArgumentException if the rootPath isn't a valid ZNode
     */
    static RootedConfigurationSubject getEmptyRootSubject(String rootPath) {
        PathUtils.validatePath(rootPath);

        LinkedHashSet<ZNode> emptySubject = new LinkedHashSet<>();

        return new RootedConfigurationSubject() {
            @Override
            public String getRootPath() {
                return rootPath;
            }

            @Override
            public LinkedHashSet<ZNode> getConfigurationZNodes() {
                return new LinkedHashSet<>(emptySubject);
            }
        };
    }

    /**
     * Returns the path shared by all the ZNodes returned when calling this
     * subject {@link #getConfigurationZNodes} method, if any exist - or a
     * valid ZNode if the method returns an empty set.
     * <p>
     * This rule most be kept by every implementation, as other functionality
     * might depend on it.
     *
     * @return the {@link ZNode} path of the root shared by all the ZNodes in
     * this subject
     */
    String getRootPath();
}

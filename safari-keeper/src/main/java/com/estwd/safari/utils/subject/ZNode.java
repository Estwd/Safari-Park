package com.estwd.safari.utils.subject;

import org.apache.curator.utils.PathUtils;

/**
 * A ZNode in the Zookeeper tree structure.
 *
 * @author Guni Y.
 */
public final class ZNode {

    private final String path;
    private final String description;

    /**
     * Constructs a {@link ZNode} containing the path and description.
     *
     * @param path        the path of the ZNode
     * @param description a description of the ZNode's context or scope
     * @throws IllegalArgumentException if the path is invalid
     */
    public ZNode(String path, String description) {
        PathUtils.validatePath(path);

        this.path = path;
        this.description = description;
    }

    /**
     * Returns the path of this {@link ZNode}.
     *
     * @return the path of this ZNode
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the description of this {@link ZNode}.
     *
     * @return the description of this ZNode
     */
    public String getDescription() {
        return description;
    }
}

package com.estwd.safari.utils.subject;

/**
 * Created by development on 26/03/16.
 */
public class ZNode {

    private final String path;
    private final String description;

    public ZNode(String path, String description) {
        this.path = path;
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }
}

package com.estwd.safari.utils.subject;

import java.util.LinkedHashSet;

/**
 * Created by development on 24/03/16.
 */
public class SingleSubject implements ConfigurationSubject {

    private final ZNode zNode;

    public SingleSubject(String path, String description) {
        zNode = new ZNode(path, description);
    }

    public SingleSubject(ZNode zNode) {
        this.zNode = zNode;
    }

    @Override
    public LinkedHashSet<ZNode> getConfigurationNodes() {
        return new LinkedHashSet<ZNode>() {{
            add(zNode);
        }};
    }
}

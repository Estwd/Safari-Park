package com.estwd.safari.utils.subject;

import java.util.LinkedHashSet;

/**
 * Created by development on 24/03/16.
 */
public class SingleSubject implements ConfigurationSubject {

    protected final LinkedHashSet<ZNode> configurationNode;

    public SingleSubject(String path, String description) {
       this(new ZNode(path, description));
    }

    public SingleSubject(ZNode zNode) {
        configurationNode = new LinkedHashSet<ZNode>() {{
            add(zNode);
        }};
    }

    @Override
    public LinkedHashSet<ZNode> getConfigurationNodes() {
        return configurationNode;
    }
}

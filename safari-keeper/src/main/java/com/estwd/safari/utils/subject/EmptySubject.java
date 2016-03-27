package com.estwd.safari.utils.subject;

import java.util.LinkedHashSet;

/**
 * Created by development on 24/03/16.
 */
public class EmptySubject implements ConfigurationSubject {

    @Override
    public LinkedHashSet<ZNode> getConfigurationNodes() {
        return new LinkedHashSet<>();
    }
}

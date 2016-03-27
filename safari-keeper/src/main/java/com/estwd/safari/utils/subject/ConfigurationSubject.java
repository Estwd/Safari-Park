package com.estwd.safari.utils.subject;

import java.util.LinkedHashSet;

/**
 * Created by development on 21/03/16.
 */
public interface ConfigurationSubject {

    LinkedHashSet<ZNode> getConfigurationNodes();

    static ConfigurationSubject append(ConfigurationSubject subject1, ConfigurationSubject subject2) {
        LinkedHashSet<ZNode> newSubject = new LinkedHashSet<>();
        newSubject.addAll(subject1.getConfigurationNodes());
        newSubject.addAll(subject2.getConfigurationNodes());
        return () -> (newSubject);
    }
}

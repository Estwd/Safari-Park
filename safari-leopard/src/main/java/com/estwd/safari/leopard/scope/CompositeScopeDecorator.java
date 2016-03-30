package com.estwd.safari.leopard.scope;

import com.estwd.safari.utils.subject.ConfigurationSubject;
import com.estwd.safari.utils.subject.ZNode;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

/**
 * Created by development on 21/03/16.
 */
public class CompositeScopeDecorator implements ConfigurationScopeDecorator {

    private static final String INITIAL_DESCRIPTION = "Values for the following scope: %s";

    private final LinkedHashSet<ZNode> configurationNodes;

    public CompositeScopeDecorator(ConfigurationSubject subject, String zNode, String scopeName, String root) {
        this(subject, new Scope(zNode, scopeName), root);
    }

    public CompositeScopeDecorator(ConfigurationSubject subject, Scope scope, String root) {
        configurationNodes =
                subject.getConfigurationNodes().stream()
                        .map(zNode -> addCompositeScope(zNode, scope))
                        .collect(Collectors.toCollection(LinkedHashSet::new));
        configurationNodes.add(new ZNode(root + "/" + scope.getzNode(), getInitialDescription(scope.getScopeName())));
        configurationNodes.addAll(subject.getConfigurationNodes());
    }

    @Override
    public LinkedHashSet<ZNode> getConfigurationNodes() {
        return configurationNodes;
    }

    protected static ZNode addCompositeScope(ZNode zNode, Scope compositeScope) {
        return new ZNode(
                addToPath(zNode.getPath(), compositeScope.getzNode()),
                addToDescription(zNode.getDescription(), compositeScope.getScopeName()));
    }

    protected static String addToPath(String path, String zNode) {
        return path + "/" + zNode;
    }

    protected static String addToDescription(String description, String scope) {
        return description + "." + scope;
    }

    protected static String getInitialDescription(String scopeName) {
        return String.format(INITIAL_DESCRIPTION, scopeName);
    }
}

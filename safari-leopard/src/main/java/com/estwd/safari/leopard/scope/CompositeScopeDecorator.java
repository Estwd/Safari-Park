package com.estwd.safari.leopard.scope;

import com.estwd.safari.leopard.subject.ZNode;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

/**
 * {@link RootedConfigurationSubject} decorator that allows the creation of a
 * composite tree structure of {@link Scope}s. Every new decorating subject's
 * scope is concatenated as a sub-scope to all the scopes in the decorated subject.
 * <p>
 * The rules for the order of precedence of the newly created sub-scopes are as
 * follows:
 * <ul>
 * <li>The new sub-scopes are ordered by the order of the scopes who are their
 * direct parents.
 * <li>A new sub-scope below this subject root is also created, with a lower
 * precedence than those of the other new sub-scopes.
 * <li>All the old sub-scopes retain their precedence order, but have lower
 * precedence then all the new sub-scopes.
 * </ul>
 * <p>
 * Note: This class is not thread safe. It was not designed as such because the
 * registration of watches should be made at a clear and readable location in
 * the thread invoking it. The purpose of this class is enabling that clarity
 * and allowing other options would have hampered it.
 *
 * @author Guni Y.
 */
public final class CompositeScopeDecorator implements RootedSubjectDecorator {

    private static final String INITIAL_DESCRIPTION = "Values for the following scope: %s";

    private final LinkedHashSet<ZNode> configurationNodes;
    private final String root;

    /**
     * Decorates an existing {@link RootedConfigurationSubject} with the given
     * {@link Scope}.
     *
     * @param subject a RootedConfigurationSubject to be decorated
     * @param scope   a Scope
     * @see CompositeScopeDecorator
     */
    public CompositeScopeDecorator(RootedConfigurationSubject subject, Scope scope) {
        configurationNodes =
                subject.getConfigurationZNodes().stream()
                        .map(zNode -> addCompositeScope(zNode, scope))
                        .collect(Collectors.toCollection(LinkedHashSet::new));
        configurationNodes.add(new ZNode(addToPath(subject.getRootPath(), scope), getInitialDescription(scope)));
        configurationNodes.addAll(subject.getConfigurationZNodes());

        root = subject.getRootPath();
    }

    @Override
    public LinkedHashSet<ZNode> getConfigurationZNodes() {
        return new LinkedHashSet<>(configurationNodes);
    }

    private static ZNode addCompositeScope(ZNode zNode, Scope scope) {
        return new ZNode(addToPath(zNode, scope), addToDescription(zNode, scope));
    }

    private static String addToPath(ZNode zNode, Scope scope) {
        return addToPath(zNode.getPath(), scope);
    }

    private static String addToPath(String path, Scope scope) {
        return String.format("%s/%s/%s", path, scope.getClassification(), scope.getName());
    }

    private static String addToDescription(ZNode zNode, Scope scope) {
        return String.format("%s.%s", zNode.getDescription(), scope.getClassification());
    }

    private static String getInitialDescription(Scope scope) {
        return String.format(INITIAL_DESCRIPTION, scope.getClassification());
    }

    @Override
    public String getRootPath() {
        return root;
    }
}

package com.estwd.safari.utils.subject;

import java.util.LinkedHashSet;

/**
 * An interface used by all the classes saving ordered sets of {@link ZNode}s.
 * Should be used to distinguish sets of ZNodes by some related context
 * or scope (for example - configuration scopes).
 * <p>
 * Note: This class is not thread safe. It was not designed as such because the
 * registration of watches should be made at a clear and readable location in
 * the thread invoking it. The purpose of this class is enabling that clarity
 * and allowing other options would have hampered it.
 *
 * @author Guni Y.
 */
public interface ConfigurationSubject {

    /**
     * Returns a new {@link ConfigurationSubject} which returns an empty
     * set when its {@link #getConfigurationZNodes} method is invoked.
     *
     * @return a ConfigurationSubject containing an
     * empty set of {@link ZNode}s
     */
    static ConfigurationSubject createEmptySubject() {
        LinkedHashSet<ZNode> emptySubject = new LinkedHashSet<>();
        return () -> (new LinkedHashSet<>(emptySubject));
    }

    /**
     * Returns a new {@link ConfigurationSubject} which returns a set containing
     * a single {@link ZNode} (with the given path and description) when its
     * {@link #getConfigurationZNodes} method is invoked.
     *
     * @param path        a path for the single ZNode to be returned by the subject
     * @param description a description for the single ZNode to be returned by the subject
     * @return a ConfigurationSubject containing a set with a
     * single ZNode
     * @throws IllegalArgumentException if the path is invalid
     */
    static ConfigurationSubject createSingleSubject(String path, String description) {
        return createSingleSubject(new ZNode(path, description));
    }

    /**
     * Returns a new {@link ConfigurationSubject} which returns a set containing
     * only the ZNode passed when its {@link #getConfigurationZNodes} method is
     * invoked.
     *
     * @param zNode a {@link ZNode} to be returned by the subject
     * @return a ConfigurationSubject containing a set with a
     * single ZNode
     */
    static ConfigurationSubject createSingleSubject(ZNode zNode) {
        LinkedHashSet<ZNode> singleSubject = new LinkedHashSet<ZNode>() {{
            add(zNode);
        }};

        return () -> (new LinkedHashSet<>(singleSubject));
    }

    /**
     * Returns a new {@link ConfigurationSubject} created by appending this subject to
     * all the subjects passed as parameters.
     * <p>
     * The order of precedence for the configuration {@link ZNode}s will the be determined
     * by the order of parameters (with this subject having the highest precedence),
     * i.e. - if any configuration key appears in more than one subject's ZNode
     * than the value to be used will be the one from the ZNode (with the highest
     * precedence) in this subject, or (if it doesn't exist in this subject) from the first
     * subject used as parameters to this function.
     *
     * @param firstSubject a configuration subject
     * @param subjects     a list of configuration subjects
     * @return a ConfigurationSubject containing a set
     * of nodes appended from all the subjects to this subject
     */
    default ConfigurationSubject add(ConfigurationSubject firstSubject, ConfigurationSubject... subjects) {
        LinkedHashSet<ZNode> newSubject = getConfigurationZNodes();
        newSubject.addAll(firstSubject.getConfigurationZNodes());
        for (ConfigurationSubject subject : subjects)
            newSubject.addAll(subject.getConfigurationZNodes());
        return () -> (new LinkedHashSet<>(newSubject));
    }

    /**
     * Returns the ordered set of {@link ZNode}s contained in this subject. The
     * order of the set specifies the precedence value of the configuration
     * keys, i.e - if any configuration key appears in more than one ZNode than
     * the value to be used will be the one from the ZNode appearing first in
     * the set.
     * <p>
     * Note: Changing the returned set will not effect the set contained in this
     * subject. This rule should be kept by subclasses.
     *
     * @return a {@link LinkedHashSet} of ZNodes
     */
    LinkedHashSet<ZNode> getConfigurationZNodes();


}

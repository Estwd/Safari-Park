package com.estwd.safari.leopard.recipes.distributed;

import com.estwd.safari.leopard.scope.CompositeScopeDecorator;
import com.estwd.safari.leopard.scope.RootedConfigurationSubject;
import com.estwd.safari.leopard.scope.Scope;
import com.estwd.safari.leopard.subject.ConfigurationSubject;
import com.estwd.safari.leopard.subject.ZNode;

/**
 * Builder of a {@link ConfigurationSubject} to be used in distributed system.
 * This subject is used to watch only the {@link ZNode}s containing values
 * specific to the servlet registering the watches. The ZNodes watches are also
 * prioritized so that only the most specific value is returned for any
 * configuration key.
 * <p>
 * The distributed system scopes supported by this builder are (in order of low
 * to high precedence):
 * <ul>
 * <li>Global - for default values available and used throughout a
 * distributed services platform.
 * <li>Environment - for values specific to a single distributed environment in
 * the platform (e.g. production).
 * <li>Service - for values specific to a single distributed service class.
 * <li>Instance - for values relating to a specific instance of the distributed
 * service.
 * </ul>
 * <p>
 * Below is an abstract diagram of the order of precedence used by the ZNodes
 * watch mechanism (where 1 is the highest priority). If any of the scopes is not
 * used, than all its nodes (and their sub-nods) would not appear.
 * <p>
 * <img src="https://github.com/Estwd/Safari-Park/wiki/images/safari-leopard/distributedRecipe.zNodesPrecedenceOrder.png" alt="">
 * <p>Note: Before applying the watch, the following ZNodes must exist:
 * <ul>
 * <li>/configuration/{scope-class}/{scope-name} for every scope used (for
 * example /configuration/environment/production)
 * <li>{parent-scope}/{sub-scope-class}/{sub-scope-name} for every scope used
 * along with a lower priority scope - the lower priority is referred to as the
 * parent-scope (see the above diagram for all the available sub-scopes options).
 * </ul>
 * <p>
 * Note: This class is not thread safe. It was not designed as such because the
 * registration of watches should be made at a clear and readable location in
 * the thread invoking it. The purpose of this class is enabling that clarity
 * and allowing other options would have hampered it.
 *
 * @author Guni Y.
 */
public class DistributedSystemSubjectBuilder {

    private final static String CONFIG_ROOT_PATH = "/configuration";
    private final static ZNode GLOBAL_ZNODE = new ZNode(CONFIG_ROOT_PATH + "/global", "Values for the global scope");

    private final static String ENVIRONMENT = "environment";
    private final static String SERVICE = "service";
    private final static String INSTANCE = "instance";

    private boolean isGlobalScopeUsed = false;
    private Scope envScope;
    private Scope serviceScope;
    private Scope instanceScope;

    /**
     * Adds a global scope to this subject scopes.
     *
     * @return this subject with an added global scope
     * @see DistributedSystemSubjectBuilder
     */
    public final DistributedSystemSubjectBuilder global() {
        isGlobalScopeUsed = true;
        return this;
    }

    /**
     * Adds an environment scope to this subject scopes.
     *
     * @param environment the name of the environment
     * @return this subject with an added environment scope
     * @see DistributedSystemSubjectBuilder
     */
    public final DistributedSystemSubjectBuilder environment(String environment) {
        envScope = new Scope(environment, ENVIRONMENT);
        return this;
    }

    /**
     * Adds a service scope to this subject scopes.
     *
     * @param service the name of the service
     * @return this subject with an added service scope
     * @see DistributedSystemSubjectBuilder
     */
    public final DistributedSystemSubjectBuilder service(String service) {
        serviceScope = new Scope(service, SERVICE);
        return this;
    }

    /**
     * Adds an instance scope to this subject scopes.
     *
     * @param instance the name of the instance
     * @return this subject with an added instance scope
     * @see DistributedSystemSubjectBuilder
     */
    public final DistributedSystemSubjectBuilder instance(String instance) {
        instanceScope = new Scope(instance, INSTANCE);
        return this;
    }

    /**
     * Builds a {@link ConfigurationSubject} according to the scopes set in
     * this builder.
     *
     * @return the ConfigurationSubject built
     * @see DistributedSystemSubjectBuilder
     */
    public ConfigurationSubject build() {
        ConfigurationSubject configurationSubject = getCompositeScopesSubject();

        if (isGlobalScopeUsed) {
            ConfigurationSubject globalSubject = ConfigurationSubject.createSingleSubject(GLOBAL_ZNODE);
            configurationSubject = configurationSubject.add(globalSubject);
        }

        return configurationSubject;
    }

    private ConfigurationSubject getCompositeScopesSubject() {
        RootedConfigurationSubject rootedConfigurationSubject = RootedConfigurationSubject.getEmptyRootSubject(CONFIG_ROOT_PATH);

        if (envScope != null)
            rootedConfigurationSubject = new CompositeScopeDecorator(rootedConfigurationSubject, envScope);
        if (serviceScope != null)
            rootedConfigurationSubject = new CompositeScopeDecorator(rootedConfigurationSubject, serviceScope);
        if (instanceScope != null)
            rootedConfigurationSubject = new CompositeScopeDecorator(rootedConfigurationSubject, instanceScope);

        return rootedConfigurationSubject;
    }
}

package com.estwd.safari.leopard.recipes.distributed;

import com.estwd.safari.leopard.scope.CompositeScopeDecorator;
import com.estwd.safari.leopard.scope.Scope;
import com.estwd.safari.utils.subject.ConfigurationSubject;
import com.estwd.safari.utils.subject.EmptySubject;
import com.estwd.safari.utils.subject.SingleSubject;
import com.estwd.safari.utils.subject.ZNode;

/**
 * Created by development on 21/03/16.
 */
public class DistributedSystemSubject {

    protected final static String CONFIG_ROOT_ZNODE = "/configuration";
    protected final static ZNode GLOBAL_ZNODE = new ZNode(CONFIG_ROOT_ZNODE + "/" + "global", "Values for the global scope");
    protected final static Scope ENV_SCOPE_ROOT = new Scope("environment", "Environment");
    protected final static Scope SERVICE_SCOPE_ROOT = new Scope("service", "Service");
    protected final static Scope INSTANCE_SCOPE_ROOT = new Scope("instance", "Instance");

    protected boolean isGlobalScopeUsed = false;
    protected Scope envScope;
    protected Scope serviceScope;
    protected Scope instanceScope;

    protected DistributedSystemSubject() {
    }

    public static DistributedSystemSubject builder() {
        return new DistributedSystemSubject();
    }

    public DistributedSystemSubject global() {
        isGlobalScopeUsed = true;
        return this;
    }

    public DistributedSystemSubject environment(String zNode) {
        envScope = addZNodeToScope(ENV_SCOPE_ROOT, zNode);
        return this;
    }

    public DistributedSystemSubject service(String zNode) {
        serviceScope = addZNodeToScope(SERVICE_SCOPE_ROOT, zNode);
        return this;
    }

    public DistributedSystemSubject instance(String zNode) {
        instanceScope = addZNodeToScope(INSTANCE_SCOPE_ROOT, zNode);
        return this;
    }

    private static Scope addZNodeToScope(Scope scope, String zNode) {
        return new Scope(scope.getzNode() + "/" + zNode, scope.getScopeName());
    }

    public ConfigurationSubject build() {
        ConfigurationSubject configurationSubject = new EmptySubject();

        if (envScope != null)
            configurationSubject = addToCompositeScope(configurationSubject, envScope);
        if (serviceScope != null)
            configurationSubject = addToCompositeScope(configurationSubject, serviceScope);
        if (instanceScope != null)
            configurationSubject = addToCompositeScope(configurationSubject, instanceScope);

        if (isGlobalScopeUsed) {
            SingleSubject globalScope = new SingleSubject(GLOBAL_ZNODE);
            configurationSubject = ConfigurationSubject.append(configurationSubject, globalScope);
        }

        return configurationSubject;
    }

    private CompositeScopeDecorator addToCompositeScope(ConfigurationSubject configurationSubject, Scope scope) {
        return new CompositeScopeDecorator(configurationSubject, scope, CONFIG_ROOT_ZNODE);
    }
}

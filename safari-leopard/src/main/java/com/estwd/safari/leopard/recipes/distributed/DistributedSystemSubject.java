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
    protected final static Scope ENV_SCOPE = new Scope("environment", "Environment");
    protected final static Scope SERVICE_SCOPE = new Scope("service", "Service");
    protected final static Scope INSTANCE_SCOPE = new Scope("instance", "Instance");

    protected boolean isGlobalScopeUsed = false;
    protected boolean isEnvScopeUsed = false;
    protected boolean isServiceScopeUsed = false;
    protected boolean isInstanceScopeUsed = false;

    protected DistributedSystemSubject() {
    }

    public static DistributedSystemSubject builder() {
        return new DistributedSystemSubject();
    }

    public DistributedSystemSubject global() {
        isGlobalScopeUsed = true;
        return this;
    }

    public DistributedSystemSubject environment() {
        isEnvScopeUsed = true;
        return this;
    }

    public DistributedSystemSubject service() {
        isServiceScopeUsed = true;
        return this;
    }

    public DistributedSystemSubject instance() {
        isInstanceScopeUsed = true;
        return this;
    }

    public ConfigurationSubject build() {
        ConfigurationSubject configurationSubject = new EmptySubject();

        if (isEnvScopeUsed)
            configurationSubject = addToCompositeScope(configurationSubject, ENV_SCOPE);
        if (isServiceScopeUsed)
            configurationSubject = addToCompositeScope(configurationSubject, SERVICE_SCOPE);
        if (isInstanceScopeUsed)
            configurationSubject = addToCompositeScope(configurationSubject, INSTANCE_SCOPE);

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

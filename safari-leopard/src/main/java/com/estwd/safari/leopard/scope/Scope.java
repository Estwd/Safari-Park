package com.estwd.safari.leopard.scope;

/**
 * Created by development on 26/03/16.
 */
public class Scope {
    private final String zNode;
    private final String scopeName;

    public Scope(String zNode, String scopeName) {
        this.zNode = zNode;
        this.scopeName = scopeName;
    }

    public String getzNode() {
        return zNode;
    }

    public String getScopeName() {
        return scopeName;
    }
}

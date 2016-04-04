package com.estwd.safari.leopard.scope;

/**
 * A specific scope for configuration values, which is a part of a classification
 * group (for example - services).
 *
 * @author Guni Y.
 */
public final class Scope {
    private final String name;
    private final String classification;

    /**
     * Constructs a {@link Scope} with the given classification and name.
     *
     * @param name           the specific name of the Scope in the classification
     *                       group (must be unique within that group)
     * @param classification the name of the classification group that this Scope
     *                       is a part of
     */
    public Scope(String name, String classification) {
        this.classification = classification;
        this.name = name;
    }

    /**
     * Returns the name of this {@link Scope}.
     *
     * @return the name of this Scope
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the name of the classification group of this {@link Scope}.
     *
     * @return the name of the classification group
     */
    public String getClassification() {
        return classification;
    }

}

package io.kwu.kythera;

import io.kwu.kythera.frontend.node.ExpressionNode;

import java.util.HashMap;

public class Scope {

    // keeps track of whether a break or return is allowed
    public enum ScopeType {
        GLOBAL,
        FUNCTION,
        CONTROL_FLOW
    }

    public final Scope parent;
    private HashMap<String, ExpressionNode> symbols = new HashMap<>();

    public final ScopeType scopeType;

    // root scope
    public Scope() {
        this.parent = null;
        this.scopeType = ScopeType.GLOBAL;
    }

    // child scope
    public Scope(Scope parent, ScopeType scopeType, ExpressionNode thisType) {
        this.parent = parent;

        if (thisType != null) {
            symbols.put("this", thisType);
        }

        this.scopeType = scopeType;
    }

    /**
     * Initialize variable. Throws error if already declared
     */
    public void create(String name, ExpressionNode typeExp) {
        if (this.symbols.containsKey(name)) {
            System.err.println(name + " is already bound in this scope.");
            System.exit(1);
        }

        this.symbols.put(name, typeExp);
    }

    /**
     * Get type of variable
     */
    public ExpressionNode getTypeOf(String name) {
        if (this.symbols.containsKey(name)) {
            return this.symbols.get(name);
        } else {
            if (this.parent == null) {
                System.err.println(name + " is not defined in this scope.");
                System.exit(1);
                return null;
            } else {
                return this.parent.getTypeOf(name);
            }
        }
    }

    /**
     * true if variable is accessible in this scope (including its parents),
     * false otherwise
     */
    public boolean has(String name) {
        if (symbols.containsKey(name)) {
            return true;
        } else {
            if (this.parent == null) {
                return false;
            } else {
                return this.parent.has(name);
            }
        }
    }
}

package io.kwu.kythera;

import io.kwu.kythera.parser.NodeType;

import java.util.HashMap;

public class Scope {
    public final Scope parent;
    private HashMap<String, NodeType> symbols = new HashMap<>();

    public Scope() {
        this.parent = null;
    }

    public Scope(Scope parent) {
        this.parent = parent;
    }

    /**
     * Initialize variable. Throws error if already declared
     */
    public void create(String name, NodeType type) throws Exception {
        if (this.symbols.containsKey(name)) {
            throw new Exception(name + " is already bound.");
        }

        this.symbols.put(name, type);
    }

    /**
     * Get type of variable
     */
    public NodeType get(String name) {
        if (this.symbols.containsKey(name)) {
            return this.symbols.get(name);
        } else {
            if(this.parent == null) {
                throw new Error(name + " is not defined.");
            } else {
                return this.parent.get(name);
            }
        }
    }

    /**
     * true if variable is accessible in this scope (including its parents), false otherwise
     */
    public boolean has(String name) {
        if(symbols.containsKey(name)) {
            return true;
        } else {
            if(this.parent == null) {
                return false;
            } else {
                return this.parent.has(name);
            }
        }
    }
}

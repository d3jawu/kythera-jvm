package me.dejawu.kythera.frontend;

import me.dejawu.kythera.frontend.node.*;

import java.util.List;

public class TypeChecker {
    private final List<StatementNode> program;

    public TypeChecker(List<StatementNode> program) {
        this.program = program;
    }

    // type-checks nodes
    // links statically known types to type literals
    // marks dynamically known types
    // makes sure identifiers and field accesses are valid
    public List<StatementNode> typeCheck() {
        return this.program;
    }
}

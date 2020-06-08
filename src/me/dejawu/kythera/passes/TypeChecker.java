package me.dejawu.kythera.passes;

import me.dejawu.kythera.ast.*;

import java.util.List;

// type-checks nodes
// links statically known types to type literals
// marks dynamically known types
// makes sure identifiers and field accesses are valid
public class TypeChecker extends Visitor {
    public TypeChecker(List<StatementNode> program) {
        super(program);
    }
}

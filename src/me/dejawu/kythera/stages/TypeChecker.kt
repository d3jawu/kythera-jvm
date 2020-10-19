package me.dejawu.kythera.stages

import me.dejawu.kythera.ast.StatementNode

// type-checks nodes
// links statically known types to type literals
// marks dynamically known types
// makes sure identifiers and field accesses are valid
class TypeChecker(program: List<StatementNode>) : Visitor(program)
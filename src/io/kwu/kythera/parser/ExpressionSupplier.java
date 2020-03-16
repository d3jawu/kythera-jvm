package io.kwu.kythera.parser;

import io.kwu.kythera.parser.node.ExpressionNode;

// interface for lambdas that take no argument and return an ExpressionNode
@FunctionalInterface
public interface ExpressionSupplier {
    ExpressionNode apply();
}

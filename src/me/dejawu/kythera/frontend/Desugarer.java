package me.dejawu.kythera.frontend;

import me.dejawu.kythera.frontend.node.*;
import me.dejawu.kythera.frontend.tokenizer.Symbol;

import java.util.ArrayList;
import java.util.List;

public class Desugarer {
    private final List<StatementNode> sugared;

    public Desugarer(List<StatementNode> program) {
        this.sugared = program;

    }

    // returns AST with syntactic sugar nodes removed
    public List<StatementNode> desugar() {
        List<StatementNode> desugared = new ArrayList<>();

        for (StatementNode st : sugared) {
            switch (st.kind) {
                // unary becomes function call
                case UNARY:
                    UnaryNode unaryNode = (UnaryNode) st;

                    desugared.add(
                        new CallNode(
                            new DotAccessNode(unaryNode.target, unaryNode.operator.symbol),
                            new ArrayList<>()
                        )
                    );
                    break;
                // binary infix becomes function call
                case BINARY:
                    BinaryNode binaryNode = (BinaryNode) st;

                    desugared.add(
                        new CallNode(
                            new DotAccessNode(binaryNode.left, binaryNode.operator.symbol),
                            new ArrayList<>() {
                                {
                                    add(binaryNode.right);
                                }
                            }
                        )
                    );
                    break;
                // op= assignments become function calls with normal assignment
                case ASSIGN:
                    AssignNode assignNode = (AssignNode) st;

                    if (assignNode.operator.equals(Symbol.EQUALS)) {
                        desugared.add(st);
                    } else {
                        // separate assignment, e.g. x += 10 becomes x = (x + 10)
                        desugared.add(new AssignNode(
                            Symbol.EQUALS,
                            assignNode.left,
                            new CallNode(
                                new DotAccessNode(assignNode.left, "" + assignNode.operator.symbol.charAt(0)),
                                new ArrayList<>() {
                                    {
                                        add(assignNode.right);
                                    }
                                }
                            )
                        ));
                    }

                    break;
                // code blocks become functions that take no parameter
                case BLOCK:
                default:
                    desugared.add(st);
            }
        }

        return desugared;
    }
}

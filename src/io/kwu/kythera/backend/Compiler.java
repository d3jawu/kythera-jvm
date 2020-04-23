package io.kwu.kythera.backend;

import io.kwu.kythera.frontend.node.LetNode;
import io.kwu.kythera.frontend.node.StatementNode;

import java.util.List;


public class Compiler {
    private final List<StatementNode> program;

    public Compiler(List<StatementNode> program) {
        this.program = program;
    }

    public byte[] compile(String programName) {
        // unlike Java, Kythera has a global scope, which we place inside of the
        // main method of the output class

        for (StatementNode st : this.program) {
            switch (st.kind) {
                case LET:
                    visitLet((LetNode) st);
                    break;
                case RETURN:
                    visitReturn();;
                    break;
                default:
                    visitExpression();

            }
        }

        return null;
    }

    public void visitLet(LetNode node) {

    }

    public void visitReturn() {

    }

    public void visitExpression() {

    }
}

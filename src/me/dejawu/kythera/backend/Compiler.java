package me.dejawu.kythera.backend;

import me.dejawu.kythera.frontend.node.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;


public class Compiler {
    final String CLASSPATH = "me/dejawu/kythera/backend/";

    private final List<StatementNode> program;
    private final ClassWriter cw;
    private final TraceClassVisitor tcv;
    // represents statements in the "global scope" (actually the main() method)
    // this allows Kythera to have a global scope while Java does not.
    private MethodVisitor mv;

    private SymbolTable symbolTable;

    public Compiler(List<StatementNode> program, String outputName) {
        this.program = program;
        this.cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        this.tcv = new TraceClassVisitor(this.cw, new PrintWriter(System.out, true));

        this.tcv.visit(V11, ACC_PUBLIC | ACC_SUPER, outputName, null, "java/lang/Object", null);
    }

    public byte[] compile() {
        this.mv = this.tcv.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        this.mv.visitCode();

        this.symbolTable = new SymbolTable(this.mv);

        for (StatementNode st : this.program) {
            this.visitStatement(st);
        }

        // cleanup
        this.mv.visitInsn(RETURN);
        this.mv.visitMaxs(0, 0);
        this.tcv.visitEnd();
        return this.cw.toByteArray();
    }

    public void visitStatement(StatementNode st) {
        switch (st.kind) {
            case LET:
                visitLet((LetNode) st);
                break;
            case RETURN:
                visitReturn();
                break;
            default:
                visitExpression((ExpressionNode) st);
        }
    }

    // declare a variable, associate it with an entry in the symbol table, and initialize it to the given value.
    public void visitLet(LetNode node) {
        // evaluate the RHS first to get the reference to that value
        this.visitExpression(node.value);
        this.symbolTable.addSymbol(node.identifier);
    }

    public void visitReturn() {
        this.mv.visitInsn(RETURN);
    }

    // all expressions leave their result value(s) on the top of the stack
    public void visitExpression(ExpressionNode node) {
        switch (node.kind) {
            case UNARY:
                this.visitUnary((UnaryNode) node);
                return;
            case BINARY:
                return;
            case ASSIGN:
                return;
            case LITERAL:
                // switch depending on which kind of literal
                if (node.equals(UnitLiteral.UNIT)) {
                    return;
                } else if (node.equals(BooleanLiteral.TRUE)) {
                    this.mv.visitFieldInsn(GETSTATIC, CLASSPATH + "KytheraValue", "TRUE", "L" + CLASSPATH + "KytheraValue;");
                    return;
                } else if (node.equals(BooleanLiteral.FALSE)) {
                    this.mv.visitFieldInsn(GETSTATIC, CLASSPATH + "KytheraValue", "FALSE", "L" + CLASSPATH + "KytheraValue;");
                    return;
                } else if (node instanceof IntLiteralNode) {
                    this.visitIntLiteral((IntLiteralNode) node);
                    return;
                } else if (node instanceof DoubleLiteralNode) {
                    break;
                } else if (node instanceof StructLiteralNode) {
                    break;
                } else if (node instanceof FnLiteralNode) {
                    break;
                } else if (node instanceof TypeLiteralNode) {
                    break;
                }
                return;
            case IDENTIFIER:
                this.visitIdentifier((IdentifierNode) node);
                return;
            case IF:
                this.visitIf((IfNode) node);
                return;
            case WHILE:
                this.visitWhile((WhileNode) node);
                return;
            case AS:
                this.visitAs((AsNode) node);
                return;
            case CALL:
                this.visitCall((CallNode) node);
                return;
            case ACCESS:
                // switch depending on type of access
                return;
            case BLOCK:
                this.visitBlock((BlockNode) node);
                return;
            case TYPEOF:
            default:
        }

        System.err.println("Unsupported or not implemented: " + node.kind.name());
        System.exit(1);
    }

    public void visitUnary(UnaryNode node) {

    }

    private void visitBlock(BlockNode node) {

    }

    private void visitCall(CallNode node) {

    }

    private void visitAs(AsNode node) {

    }

    // pull value from slot and push it onto the stack
    public void visitIdentifier(IdentifierNode node) {
        this.symbolTable.loadSymbol(node.name);
    }

    // TODO reuse int literal instances
    public void visitIntLiteral(IntLiteralNode node) {
        // create uninitialized KytheraValue object
        this.mv.visitTypeInsn(NEW, "me/dejawu/kythera/runtime/KytheraValue");

        // copy it (one is consumed by the constructor call)
        this.mv.visitInsn(DUP);

        // push int value on local stack
        if (node.value == -1) {
            this.mv.visitInsn(ICONST_M1);
        } else if (node.value == 0) {
            this.mv.visitInsn(ICONST_0);
        } else if (node.value == 1) {
            this.mv.visitInsn(ICONST_1);
        } else if (node.value == 2) {
            this.mv.visitInsn(ICONST_2);
        } else if (node.value == 3) {
            this.mv.visitInsn(ICONST_3);
        } else if (node.value == 4) {
            this.mv.visitInsn(ICONST_4);
        } else if (node.value == 5) {
            this.mv.visitInsn(ICONST_5);
        } else {
            this.mv.visitIntInsn(BIPUSH, node.value);
        }

        // convert int to Integer
        this.mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);

        // load reference to INT type literal constant
        this.mv.visitFieldInsn(GETSTATIC, "me/dejawu/kythera/runtime/KytheraValue", "INT", "Lme/dejawu/kythera/runtime/KytheraValue;");

        // call KytheraValue constructor
        this.mv.visitMethodInsn(INVOKESPECIAL, "me/dejawu/kythera/runtime/KytheraValue", "<init>", "(Ljava/lang/Object;Lme/dejawu/kythera/runtime/KytheraValue;)V", false);
    }

    public void visitIf(IfNode node) {

    }

    private void visitWhile(WhileNode node) {

    }
}

package me.dejawu.kythera.backend;

import me.dejawu.kythera.ast.*;
import me.dejawu.kythera.frontend.Visitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;


public class Compiler extends Visitor<Void, Void> {
    final String CLASSPATH = "me/dejawu/kythera/backend/";

    private final ClassWriter cw;
    private final TraceClassVisitor tcv;
    // represents statements in the "global scope" (actually the main() method)
    // this allows Kythera to have a global scope while Java does not.
    private MethodVisitor mv;

    private SymbolTable symbolTable;

    public Compiler(List<StatementNode> program, String outputName) {
        super(program);
        this.cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        this.tcv = new TraceClassVisitor(this.cw, new PrintWriter(System.out, true));

        this.tcv.visit(V11, ACC_PUBLIC | ACC_SUPER, outputName, null, "java/lang/Object", null);
    }

    public byte[] compile() {
        this.mv = this.tcv.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        this.mv.visitCode();

        this.symbolTable = new SymbolTable(this.mv);

        for (StatementNode st : this.input) {
            this.visitStatement(st);
        }

        // cleanup
        this.mv.visitInsn(RETURN);
        this.mv.visitMaxs(0, 0);
        this.tcv.visitEnd();
        return this.cw.toByteArray();
    }

    public Void visitStatement(StatementNode st) {
        switch (st.kind) {
            case LET:
                visitLet((LetNode) st);
                break;
            case RETURN:
                visitReturn((ReturnNode) st);
                break;
            default:
                visitExpression((ExpressionNode) st);
        }
        return null;
    }

    // declare a variable, associate it with an entry in the symbol table, and initialize it to the given value.
    public Void visitLet(LetNode node) {
        // evaluate the RHS first to get the reference to that value
        this.visitExpression(node.value);
        this.symbolTable.addSymbol(node.identifier);
        return null;
    }

    public Void visitReturn(ReturnNode node) {
        this.mv.visitInsn(RETURN);
        return null;
    }

    // all expressions leave their result value(s) on the top of the stack
    public Void visitExpression(ExpressionNode node) {
        switch (node.kind) {
            case ASSIGN:
                break;
            case LITERAL:
                // switch depending on which kind of literal
                if (node.equals(UnitLiteral.UNIT)) {
                    return null;
                } else if (node.equals(BooleanLiteral.TRUE)) {
                    this.mv.visitFieldInsn(GETSTATIC, CLASSPATH + "KytheraValue", "TRUE", "L" + CLASSPATH + "KytheraValue;");
                    return null;
                } else if (node.equals(BooleanLiteral.FALSE)) {
                    this.mv.visitFieldInsn(GETSTATIC, CLASSPATH + "KytheraValue", "FALSE", "L" + CLASSPATH + "KytheraValue;");
                    return null;
                } else if (node instanceof IntLiteralNode) {
                    this.visitIntLiteral((IntLiteralNode) node);
                    return null;
                } else if (node instanceof FloatLiteralNode) {
                    this.visitFloatLiteral((FloatLiteralNode) node);
                    return null;
                } else if (node instanceof DoubleLiteralNode) {
                    break;
                } else if (node instanceof StructLiteralNode) {
                    break;
                } else if (node instanceof FnLiteralNode) {
                    break;
                } else if (node instanceof TypeLiteralNode) {
                    break;
                }
            case IDENTIFIER:
                this.visitIdentifier((IdentifierNode) node);
                return null;
            case IF:
                this.visitIf((IfNode) node);
                return null;
            case WHILE:
                this.visitWhile((WhileNode) node);
                return null;
            case AS:
                this.visitAs((AsNode) node);
                return null;
            case CALL:
                this.visitCall((CallNode) node);
                return null;
            case ACCESS:
                // switch depending on type of access
            case BLOCK:
            case TYPEOF:
            case UNARY:
            case BINARY:
            default:
        }

        System.err.println("Unsupported or not implemented: " + node.kind.name());
        System.exit(1);

        return null;
    }

    public Void visitCall(CallNode node) {
        return null;
    }

    @Override
    protected Void visitDotAccess(DotAccessNode dotAccessNode) {
        return null;
    }

    @Override
    protected Void visitLiteral(LiteralNode literalNode) {
        return null;
    }

    public Void visitAs(AsNode node) {
        return null;
    }

    @Override
    protected Void visitAssign(AssignNode assignNode) {
        return null;
    }

    @Override
    protected Void visitBinary(BinaryNode binaryNode) {
        return null;
    }

    @Override
    protected Void visitBlock(BlockNode blockNode) {
        return null;
    }

    @Override
    protected Void visitBracketAccess(BracketAccessNode bracketAccessNode) {
        return null;
    }

    // pull value from slot and push it onto the stack
    public Void visitIdentifier(IdentifierNode node) {
        this.symbolTable.loadSymbol(node.name);
        return null;
    }

    // TODO move into visitLiteral?
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

    public void visitFloatLiteral(FloatLiteralNode node) {
        this.mv.visitTypeInsn(NEW, "me/dejawu/kythera/runtime/KytheraValue");

        this.mv.visitInsn(DUP);

        if (node.value == 0) {
            this.mv.visitInsn(FCONST_0);
        } else if(node.value == 1.0) {
            this.mv.visitInsn(FCONST_1);
        } else if(node.value == 2.0) {
            this.mv.visitInsn(FCONST_2);
        } else {
            this.mv.visitLdcInsn(node.value);
        }

        this.mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);

        this.mv.visitFieldInsn(GETSTATIC, "me/dejawu/kythera/runtime/KytheraValue", "FLOAT", "Lme/dejawu/kythera/runtime/KytheraValue;");

        this.mv.visitMethodInsn(INVOKESPECIAL, "me/dejawu/kythera/runtime/KytheraValue", "<init>", "(Ljava/lang/Object;Lme/dejawu/kythera/runtime/KytheraValue;)V", false);
    }

    public void visitFnLiteral(FnLiteralNode node) {

    }

    public Void visitIf(IfNode node) {
        return null;
    }

    @Override
    protected Void visitUnary(UnaryNode unaryNode) {
        return null;
    }

    public Void visitWhile(WhileNode node) {

        return null;
    }
}

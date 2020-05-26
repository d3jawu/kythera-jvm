package me.dejawu.kythera.passes;

import me.dejawu.kythera.ast.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

// generates unchecked bytecode
public class CodeGenerator extends Visitor<Void, Void> {
    final String KYTHERAVALUE_PATH = "me/dejawu/kythera/runtime/KytheraValue";

    private final ClassWriter cw;
    private final TraceClassVisitor tcv;
    // represents statements in the "global scope" (actually the main() method)
    // this allows Kythera to have a global scope while Java does not.
    private MethodVisitor mv;

    private SymbolTable symbolTable;

    public CodeGenerator(List<StatementNode> program, String outputName) {
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

    // ... => ...
    public Void visitLet(LetNode node) {
        // evaluate the RHS first to get the reference to that value
        this.visitExpression(node.value);
        this.symbolTable.addSymbol(node.identifier);
        return null;
    }

    // ... value => empty stack
    public Void visitReturn(ReturnNode node) {
        this.mv.visitInsn(RETURN);
        return null;
    }

    // Generally speaking: ... => ... expression result
    public Void visitExpression(ExpressionNode node) {
        switch (node.kind) {
            case ASSIGN:
                this.visitAssign((AssignNode) node);
                return null;
            case LITERAL:
                this.visitLiteral((LiteralNode) node);
                return null;
            case IDENTIFIER:
                this.visitIdentifier((IdentifierNode) node);
                return null;
            case IF:
                break;
            case WHILE:
                break;
            case AS:
                break;
            case CALL:
                this.visitCall((CallNode) node);
                return null;
            case TYPEOF:
                this.visitTypeof((TypeofNode) node);
                return null;
            case BLOCK:
            case UNARY:
            case BINARY:
            default:
            case ACCESS:
                if(node instanceof DotAccessNode) {
                    this.visitDotAccess((DotAccessNode) node);
                    return null;
                }
        }

        System.err.println("Unsupported or not implemented: " + node.kind.name());
        System.exit(1);

        return null;
    }

    // ... fn value => ... result of that fn value
    public Void visitCall(CallNode callNode) {
        this.visitExpression(callNode.target);

        // get internal function value
        this.mv.visitFieldInsn(GETFIELD, KYTHERAVALUE_PATH, "value", "Ljava/lang/Object;");
        this.mv.visitTypeInsn(CHECKCAST, "java/util/function/Function");

        int argCount = callNode.arguments.size();

        if(callNode.target instanceof DotAccessNode) {
            // make room for "self" variable
            argCount += 1;
        }

        // create array for arguments
        this.pushInt(argCount);
        this.mv.visitTypeInsn(ANEWARRAY, KYTHERAVALUE_PATH);

        if(callNode.target instanceof DotAccessNode) {
            callNode.arguments.add(0, ((DotAccessNode) callNode.target).target);
        }

        int n = 0;
        for (ExpressionNode arg : callNode.arguments) {
            // array reference is consumed by AASTORE, so keep a copy
            this.mv.visitInsn(DUP);
            // set position in arg array
            this.pushInt(n);

            // load argument value to top of stack
            this.visitExpression(arg);

            // put into arg array
            this.mv.visitInsn(AASTORE);

            n += 1;
        }

        // call method
        this.mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Function", "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", true);

        // cast result
        this.mv.visitTypeInsn(CHECKCAST, KYTHERAVALUE_PATH);

        return null;
    }

    // ... target value => ... value in that field
    @Override
    protected Void visitDotAccess(DotAccessNode dotAccessNode) {
        this.visitExpression(dotAccessNode.target);

        this.mv.visitFieldInsn(GETFIELD, KYTHERAVALUE_PATH, "fields", "Ljava/util/HashMap;");

        this.mv.visitLdcInsn(dotAccessNode.key);

        this.mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);

        this.mv.visitTypeInsn(CHECKCAST, KYTHERAVALUE_PATH);

        return null;
    }

    // ... => ... literal value
    @Override
    protected Void visitLiteral(LiteralNode literalNode) {
        // switch depending on which kind of literal
        if (literalNode.equals(BooleanLiteral.TRUE)) {
            this.mv.visitFieldInsn(GETSTATIC, KYTHERAVALUE_PATH, "TRUE", "L" + KYTHERAVALUE_PATH + ";");
            return null;
        } else if (literalNode.equals(BooleanLiteral.FALSE)) {
            this.mv.visitFieldInsn(GETSTATIC, KYTHERAVALUE_PATH, "FALSE", "L" + KYTHERAVALUE_PATH + ";");
            return null;
        } else if (literalNode instanceof IntLiteralNode) {
            IntLiteralNode intLiteralNode = (IntLiteralNode) literalNode;

            // push int value on local stack
            this.pushInt(intLiteralNode.value);

            this.mv.visitMethodInsn(INVOKESTATIC, KYTHERAVALUE_PATH, "getIntValue", "(I)Lme/dejawu/kythera/runtime/KytheraValue;", false);

            return null;
        } else if (literalNode instanceof FloatLiteralNode) {
            /*FloatLiteralNode floatLiteralNode = (FloatLiteralNode) literalNode;

            pushFloat(floatLiteralNode.value);

            this.mv.visitMethodInsn
            */
        } else if (literalNode instanceof DoubleLiteralNode) {
        } else if (literalNode instanceof StructLiteralNode) {
        } else if (literalNode instanceof FnLiteralNode) {
        } else if (literalNode instanceof TypeLiteralNode) {
        } else if (literalNode.equals(UnitLiteral.UNIT)) {
        }


        System.err.println("Unimplemented literal: ");
        literalNode.print(0, System.err);
        System.exit(1);

        return null;
    }

    public Void visitAs(AsNode literalNode) {
        return null;
    }

    // ... value to be assigned => ...
    @Override
    protected Void visitAssign(AssignNode assignNode) {
        // only + allowed here

        // put RHS result on top of stack
        this.visitExpression(assignNode.right);

        if(assignNode.left instanceof IdentifierNode) {
            IdentifierNode identifierNode = (IdentifierNode) assignNode.left;

            this.symbolTable.storeSymbol(identifierNode.name);

            return null;
        } else if(assignNode.left instanceof DotAccessNode) {
            // for now, all fields are mutable

        } else if(assignNode.left instanceof BracketAccessNode) {

        }
            System.err.println("Assignment LHS is invalid.");
            System.exit(1);

        return null;
    }

    @Override
    protected Void visitBinary(BinaryNode binaryNode) {
        System.err.println("Error: A binary node should not be present at code generation.");
        System.exit(1);
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

    // ... => ... value at identifier
    public Void visitIdentifier(IdentifierNode literalNode) {
        this.symbolTable.loadSymbol(literalNode.name);
        return null;
    }

    public Void visitIf(IfNode literalNode) {
        return null;
    }

    @Override
    protected Void visitUnary(UnaryNode unaryNode) {
        return null;
    }

    @Override
    public Void visitWhile(WhileNode whileNode) {
        return null;
    }

    // ... value => ... typeValue of value
    @Override
    public Void visitTypeof(TypeofNode typeofNode) {
        // put target value on top of stack
        this.visitExpression(typeofNode.target);

        this.mv.visitFieldInsn(
                GETFIELD, KYTHERAVALUE_PATH, "typeValue",
                "L" + KYTHERAVALUE_PATH + ";");
        return null;
    }

    private void pushInt(int value) {
        if (value == -1) {
            this.mv.visitInsn(ICONST_M1);
        } else if (value == 0) {
            this.mv.visitInsn(ICONST_0);
        } else if (value == 1) {
            this.mv.visitInsn(ICONST_1);
        } else if (value == 2) {
            this.mv.visitInsn(ICONST_2);
        } else if (value == 3) {
            this.mv.visitInsn(ICONST_3);
        } else if (value == 4) {
            this.mv.visitInsn(ICONST_4);
        } else if (value == 5) {
            this.mv.visitInsn(ICONST_5);
        } else {
            this.mv.visitIntInsn(BIPUSH, value);
        }
    }

    private void pushFloat(float value) {
        if (value == 0.0) {
            this.mv.visitInsn(FCONST_0);
        } else if (value == 1.0) {
            this.mv.visitInsn(FCONST_1);
        } else if (value == 2.0) {
            this.mv.visitInsn(FCONST_2);
        } else {
            this.mv.visitLdcInsn(value);
        }
    }
}

package me.dejawu.kythera.passes;

import me.dejawu.kythera.ast.*;
import me.dejawu.kythera.passes.tokenizer.Symbol;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

// generates unchecked bytecode
public class CodeGenerator extends Visitor<Void, Void> {
    final String KYTHERAVALUE_PATH = "me/dejawu/kythera/runtime/KytheraValue";

    private final ClassWriter cw;
    private final TraceClassVisitor tcv;

    // represents the "global scope" (actually the main(String[] args) method)
    // this allows Kythera to have a global scope while Java does not.
    // contains MethodVisitor for root scope
    private final SymbolTable rootScope;

    // current working scope
    private SymbolTable scope;

    // used to generate unique lambda names
    private int lambdaCount = 0;

    public CodeGenerator(List<StatementNode> program, String outputName) {
        super(program);

        this.cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        this.tcv = new TraceClassVisitor(this.cw, new PrintWriter(System.out, true));

        this.tcv.visit(V11, ACC_PUBLIC | ACC_SUPER, outputName, null, "java/lang/Object", null);
        MethodVisitor rootMv = this.tcv.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);

        // root-scope symbol table with root MethodVisitor
        this.rootScope = new SymbolTable(rootMv);
        this.scope = this.rootScope;
    }

    // kick off compilation process
    public byte[] compile() {
        this.scope.mv.visitCode();


        for (StatementNode st : this.input) {
            this.visitStatement(st);
        }

        // cleanup
        this.scope.mv.visitInsn(RETURN);
        this.scope.mv.visitMaxs(0, 0);
        this.tcv.visitEnd();
        return this.cw.toByteArray();
    }

    /*// generates code for a list of statements with whatever MethodVisitor is currently active
    // call after scope MV has been set but before it makes any calls
    private void generateBlock() {
        this.scope.mv.visitCode();

    }*/

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
        this.scope.addSymbol(node.identifier);
        return null;
    }

    // ... value => empty stack
    public Void visitReturn(ReturnNode node) {
        this.scope.mv.visitInsn(RETURN);
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
                if (node instanceof DotAccessNode) {
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
        this.scope.mv.visitFieldInsn(GETFIELD, KYTHERAVALUE_PATH, "value", "Ljava/lang/Object;");
        this.scope.mv.visitTypeInsn(CHECKCAST, "java/util/function/Function");

        int argCount = callNode.arguments.size();

        if (callNode.target instanceof DotAccessNode) {
            // make room for "self" variable
            argCount += 1;
        }

        // create array for arguments
        this.pushInt(argCount);
        this.scope.mv.visitTypeInsn(ANEWARRAY, KYTHERAVALUE_PATH);

        if (callNode.target instanceof DotAccessNode) {
            callNode.arguments.add(0, ((DotAccessNode) callNode.target).target);
        }

        int n = 0;
        for (ExpressionNode arg : callNode.arguments) {
            // array reference is consumed by AASTORE, so keep a copy
            this.scope.mv.visitInsn(DUP);
            // set position in arg array
            this.pushInt(n);

            // load argument value to top of stack
            this.visitExpression(arg);

            // put into arg array
            this.scope.mv.visitInsn(AASTORE);

            n += 1;
        }

        // call method
        this.scope.mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Function", "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", true);

        // cast result
        this.scope.mv.visitTypeInsn(CHECKCAST, KYTHERAVALUE_PATH);

        return null;
    }

    // ... target value => ... value in that field
    @Override
    protected Void visitDotAccess(DotAccessNode dotAccessNode) {
        this.visitExpression(dotAccessNode.target);

        this.scope.mv.visitFieldInsn(GETFIELD, KYTHERAVALUE_PATH, "fields", "Ljava/util/HashMap;");

        this.scope.mv.visitLdcInsn(dotAccessNode.key);

        this.scope.mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);

        this.scope.mv.visitTypeInsn(CHECKCAST, KYTHERAVALUE_PATH);

        return null;
    }

    // ... => ... literal value
    @Override
    protected Void visitLiteral(LiteralNode literalNode) {
        // switch depending on which kind of literal
        if (literalNode.equals(BooleanLiteral.TRUE)) {
            this.scope.mv.visitFieldInsn(GETSTATIC, KYTHERAVALUE_PATH, "TRUE", "L" + KYTHERAVALUE_PATH + ";");
            return null;
        } else if (literalNode.equals(BooleanLiteral.FALSE)) {
            this.scope.mv.visitFieldInsn(GETSTATIC, KYTHERAVALUE_PATH, "FALSE", "L" + KYTHERAVALUE_PATH + ";");
            return null;
        } else if (literalNode instanceof IntLiteralNode) {
            IntLiteralNode intLiteralNode = (IntLiteralNode) literalNode;

            // push int value on local stack
            this.pushInt(intLiteralNode.value);

            this.scope.mv.visitMethodInsn(INVOKESTATIC, KYTHERAVALUE_PATH, "getIntValue", "(I)Lme/dejawu/kythera/runtime/KytheraValue;", false);

            return null;
        } else if (literalNode instanceof FloatLiteralNode) {
            /*FloatLiteralNode floatLiteralNode = (FloatLiteralNode) literalNode;

            pushFloat(floatLiteralNode.value);

            this.mv.visitMethodInsn
            */
        } else if (literalNode instanceof DoubleLiteralNode) {
        } else if (literalNode instanceof StructLiteralNode) {
        } else if (literalNode instanceof FnLiteralNode) {
            // TODO distinguish capturing and non-capturing

            String name = this.getLambdaName();

            // invokedynamic call to LambdaMetaFactory
            this.scope.mv.visitInvokeDynamicInsn(
                "apply",
                "(Lme/dejawu/kythera/runtime/KytheraValue;)Ljava/util/function/Function;",
                // TODO make handle for LambdaMetaFactory.metafactory a constant?
                new Handle(
                    H_INVOKESTATIC,
                    "java/lang/invoke/LambdaMetafactory",
                    "metafactory",
                    "(Ljava/lang/invoke/MethodHandles$Lookup;" +
                        "Ljava/lang/String;" + "Ljava/lang/invoke/MethodType;" +
                        "Ljava/lang/invoke/MethodType;" + "Ljava/lang/invoke/MethodHandle;" +
                        "Ljava/lang/invoke/MethodType;)" + "Ljava/lang/invoke/CallSite;",
                    false
                ),
                Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;"),
                new Handle(
                    H_INVOKESTATIC,
                    "me/dejawu/kythera/Scratch",
                    name, // generated lambda name
                    "[Lme/dejawu/kythera/runtime/KytheraValue;)" + // normal variable list (always present)
                        "Lme/dejawu/kythera/runtime/KytheraValue;", // return val (always same)
                    false
                ),
                Type.getType(
                    "([Lme/dejawu/kythera/runtime/KytheraValue;)Lme/dejawu/kythera/runtime/KytheraValue;"
                ));

            // generate static method for lambda

            // parse block

            // assert: this.scope != this.rootScope

            // return to parent scope

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

        if (assignNode.left instanceof IdentifierNode) {
            IdentifierNode identifierNode = (IdentifierNode) assignNode.left;

            this.scope.storeSymbol(identifierNode.name);

            return null;
        } else if (assignNode.left instanceof DotAccessNode) {
            // for now, all fields are mutable

        } else if (assignNode.left instanceof BracketAccessNode) {

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
        this.scope.loadSymbol(literalNode.name);
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

        this.scope.mv.visitFieldInsn(
            GETFIELD, KYTHERAVALUE_PATH, "typeValue",
            "L" + KYTHERAVALUE_PATH + ";");
        return null;
    }

    // pushes JVM int to stack, using constants where available
    private void pushInt(int value) {
        if (value == -1) {
            this.scope.mv.visitInsn(ICONST_M1);
        } else if (value == 0) {
            this.scope.mv.visitInsn(ICONST_0);
        } else if (value == 1) {
            this.scope.mv.visitInsn(ICONST_1);
        } else if (value == 2) {
            this.scope.mv.visitInsn(ICONST_2);
        } else if (value == 3) {
            this.scope.mv.visitInsn(ICONST_3);
        } else if (value == 4) {
            this.scope.mv.visitInsn(ICONST_4);
        } else if (value == 5) {
            this.scope.mv.visitInsn(ICONST_5);
        } else {
            this.scope.mv.visitIntInsn(BIPUSH, value);
        }
    }

    // pushes JVM float to stack, using constants where available
    private void pushFloat(float value) {
        if (value == 0.0) {
            this.scope.mv.visitInsn(FCONST_0);
        } else if (value == 1.0) {
            this.scope.mv.visitInsn(FCONST_1);
        } else if (value == 2.0) {
            this.scope.mv.visitInsn(FCONST_2);
        } else {
            this.scope.mv.visitLdcInsn(value);
        }
    }

    // generates unique internal names for static functions representing lambdas
    private String getLambdaName() {
        String name = "lambda" + this.lambdaCount;
        this.lambdaCount += 1;
        return name;
    }
}

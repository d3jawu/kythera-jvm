package me.dejawu.kythera.stages.generators;

import me.dejawu.kythera.ast.*;
import org.objectweb.asm.*;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

// generates unchecked bytecode
public class JvmGenerator implements Generator {

    // keeps track of variable slots and associates MethodVisitors with scope
    // TODO also keep track of variable types?
    private static class SymbolTable extends HashMap<String, Integer> {
        public final SymbolTable parent;
        public final MethodVisitor mv;

        // root scope (no parent)
        public SymbolTable(MethodVisitor mv) {
            this.parent = null;
            this.mv = mv;
        }

        // scope with parent
        public SymbolTable(SymbolTable parent, MethodVisitor mv) {
            this.parent = parent;
            this.mv = mv;
        }

        // TODO use ASTORE_0-3 instructions

        // generates instructions that will store the variable on top of the
        // stack in a new slot
        public void addSymbol(String name) {
            // offset scope variables by 1 to leave room for parameter variable (KytheraValue[])
            final int slot = this.size() + 1;
            this.mv.visitVarInsn(ASTORE, slot);
            this.put(name, slot);
        }

        // TODO use ALOAD_0-3 instructions

        // generates instructions that will push the given (existing) symbol
        // on the stack
        public void loadSymbol(String name) {
            final int slot = this.get(name);
            this.mv.visitVarInsn(ALOAD, slot);
        }

        // generates instructions that will store the variable at the
        // top of the stack into the slot for the given (existing) symbol
        public void storeSymbol(String name) {
            final int slot = this.get(name);
            this.mv.visitVarInsn(ASTORE, slot);
        }
    }

    final static String KYTHERAVALUE_PATH = "me/dejawu/kythera/runtime/KytheraValue";

    // Java-side signature for all functions (fn's) in Kythera
    final static String KYTHERA_FN_SIGNATURE = "([Lme/dejawu/kythera/runtime/KytheraValue;)Lme/dejawu/kythera/runtime/KytheraValue;";

    private final ClassWriter cw;
    private final TraceClassVisitor tcv;

    // represents the "global scope" (actually the main(String[] args) method)
    // this allows Kythera to have a global scope while Java does not.
    // contains MethodVisitor for root scope
    private final SymbolTable rootSymbolTable;

    // current working scope
    private SymbolTable symbolTable;

    // used to generate unique lambda names
    private int lambdaCount = 0;

    // used for output class name
    private String outputName;

    protected final List<StatementNode> input;

    public JvmGenerator(List<StatementNode> program, String outputName) {
        this.input = program;

        this.outputName = outputName;

        this.cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        this.tcv = new TraceClassVisitor(this.cw, new PrintWriter(System.out, true));

        this.tcv.visit(V11, ACC_PUBLIC | ACC_SUPER, outputName, null, "java/lang/Object", null);

        this.tcv.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

        // create init method
        MethodVisitor methodVisitor = this.tcv.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLineNumber(10, label0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();

        MethodVisitor rootMv = this.tcv.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);

        // root-scope symbol table with root MethodVisitor
        this.rootSymbolTable = new SymbolTable(rootMv);
        this.symbolTable = this.rootSymbolTable;
    }

    // kick off compilation process
    @Override
    public byte[] compile() {
        this.symbolTable.mv.visitCode();

        for (StatementNode st : this.input) {
            this.visitStatement(st);
        }

        // cleanup
        this.symbolTable.mv.visitInsn(RETURN);
        this.symbolTable.mv.visitMaxs(0, 0);
        this.tcv.visitEnd();
        return this.cw.toByteArray();
    }

    /*// generates code for a list of statements with whatever MethodVisitor is currently active
    // call after scope MV has been set but before it makes any calls
    private void generateBlock() {
        this.scope.mv.visitCode();

    }*/

    public void visitStatement(StatementNode st) {
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
    }

    // ... => ...
    public void visitLet(LetNode node) {
        // evaluate the RHS first to get the reference to that value
        this.visitExpression(node.value);
        this.symbolTable.addSymbol(node.identifier);
    }

    // ... => ...
    public void visitReturn(ReturnNode node) {
        this.visitExpression(node.exp);
        this.symbolTable.mv.visitInsn(ARETURN);
    }

    // Generally speaking: ... => ... expression result
    public void visitExpression(ExpressionNode node) {
        switch (node.kind) {
            case ASSIGN:
                this.visitAssign((AssignNode) node);
                return;
            case LITERAL:
                this.visitLiteral((LiteralNode) node);
                return;
            case IDENTIFIER:
                this.visitIdentifier((IdentifierNode) node);
                return;
            case IF:
                break;
            case WHILE:
                break;
            case AS:
                break;
            case CALL:
                this.visitCall((CallNode) node);
                return;
            case TYPEOF:
                this.visitTypeof((TypeofNode) node);
                return;
            case BLOCK:
            case UNARY:
            case BINARY:
            case ACCESS:
                if (node instanceof DotAccessNode) {
                    this.visitDotAccess((DotAccessNode) node);
                    return;
                }
        }

        System.err.println("Unsupported or not implemented: " + node.kind.name());
        System.exit(1);
    }

    // ... fn value => ... result of that fn value
    public void visitCall(CallNode callNode) {
        this.visitExpression(callNode.target);

        // get internal function value
        this.symbolTable.mv.visitFieldInsn(GETFIELD, KYTHERAVALUE_PATH, "value", "Ljava/lang/Object;");
        this.symbolTable.mv.visitTypeInsn(CHECKCAST, "java/util/function/Function");

        int argCount = callNode.arguments.size();

        if (callNode.target instanceof DotAccessNode) {
            // make room for "self" variable
            argCount += 1;
        }

        // create array for arguments
        this.pushInt(argCount);
        this.symbolTable.mv.visitTypeInsn(ANEWARRAY, KYTHERAVALUE_PATH);

        if (callNode.target instanceof DotAccessNode) {
            // inject self variable
            // TODO bugfix: this causes self to be evaluated twice
            callNode.arguments.add(0, ((DotAccessNode) callNode.target).target);
        }

        int n = 0;
        for (ExpressionNode arg : callNode.arguments) {
            // array reference is consumed by AASTORE, so keep a copy
            this.symbolTable.mv.visitInsn(DUP);
            // set position in arg array
            this.pushInt(n);

            // load argument value to top of stack
            this.visitExpression(arg);

            // put into arg array
            this.symbolTable.mv.visitInsn(AASTORE);

            n += 1;
        }

        // call method
        this.symbolTable.mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Function", "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", true);

        // cast result
        this.symbolTable.mv.visitTypeInsn(CHECKCAST, KYTHERAVALUE_PATH);
    }

    // ... target value => ... value in that field
    protected void visitDotAccess(DotAccessNode dotAccessNode) {
        this.visitExpression(dotAccessNode.target);

        this.symbolTable.mv.visitFieldInsn(GETFIELD, KYTHERAVALUE_PATH, "fields", "Ljava/util/HashMap;");

        this.symbolTable.mv.visitLdcInsn(dotAccessNode.key);

        this.symbolTable.mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);

        this.symbolTable.mv.visitTypeInsn(CHECKCAST, KYTHERAVALUE_PATH);
    }

    // ... => ... literal value
    protected void visitLiteral(LiteralNode literalNode) {
        // switch depending on which kind of literal
        if (literalNode.equals(BooleanLiteral.TRUE)) {
            this.symbolTable.mv.visitFieldInsn(GETSTATIC, KYTHERAVALUE_PATH, "TRUE", "L" + KYTHERAVALUE_PATH + ";");
            return;
        } else if (literalNode.equals(BooleanLiteral.FALSE)) {
            this.symbolTable.mv.visitFieldInsn(GETSTATIC, KYTHERAVALUE_PATH, "FALSE", "L" + KYTHERAVALUE_PATH + ";");
            return;
        } else if (literalNode instanceof NumLiteralNode) {
        } else if (literalNode instanceof StructLiteralNode) {
        } else if (literalNode instanceof FnLiteralNode) {
            // TODO distinguish capturing and non-capturing

            FnLiteralNode fnLiteralNode = (FnLiteralNode) literalNode;

            String lambdaName = this.getLambdaName();

            // invokedynamic call to LambdaMetaFactory
            this.symbolTable.mv.visitInvokeDynamicInsn(
                "apply",
                "()Ljava/util/function/Function;", // TODO captured variables go in params here
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
                    this.outputName,
                    lambdaName,
                    KYTHERA_FN_SIGNATURE, // params and return val are always the same
                    false
                ),
                Type.getType(
                    KYTHERA_FN_SIGNATURE
                ));

            // get type value:

            // get type KytheraValue attached to fnLiteralNode
            this.visitExpression(fnLiteralNode.typeExp);

            // get .value field, pushing fn's InternalTypeValue on stack
            this.symbolTable.mv.visitFieldInsn(GETFIELD, KYTHERAVALUE_PATH, "value", "Ljava/lang/Object;");

            // cast value
            this.symbolTable.mv.visitTypeInsn(CHECKCAST, "me/dejawu/kythera/runtime/InternalTypeValue");

            // create fn KytheraValue from Function (from invokedynamic), InternalTypeValue (from .value field on fn literal's type expression)
            this.symbolTable.mv.visitMethodInsn(
                INVOKESTATIC,
                KYTHERAVALUE_PATH,
                "getFnValue",
                "(Ljava/util/function/Function;Lme/dejawu/kythera/runtime/InternalTypeValue;)Lme/dejawu/kythera/runtime/KytheraValue;",
                false);

            // generate static method for lambda
            final MethodVisitor mv = this.tcv.visitMethod(
                ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC,
                lambdaName,
                KYTHERA_FN_SIGNATURE,
                null, null
            );
            this.symbolTable = new SymbolTable(this.symbolTable, mv);
            this.symbolTable.mv.visitCode();

            // add boilerplate code that evaluates and reads parameters
            // TODO this would be a good place to evaluate type parameters and add them to scope

            // push parameter array
            this.symbolTable.mv.visitVarInsn(ALOAD, 0);

            int n = 0;

            // add parameters in Java array to scope
            for (String param : fnLiteralNode.parameterNames) {
                this.symbolTable.mv.visitInsn(DUP); // array ref is consumed on AASTORE
                this.pushInt(n);
                this.symbolTable.mv.visitInsn(AALOAD);
                this.symbolTable.addSymbol(param);

                n += 1;
            }

            // parse block
            for (StatementNode st : fnLiteralNode.body.body) {
                this.visitStatement(st);
            }

            // block *should* return on its own
            // remaining cleanup
            this.symbolTable.mv.visitMaxs(0, 0);
            this.symbolTable.mv.visitEnd();

            // assert: this.scope != this.rootScope

            // return to parent scope
            this.symbolTable = this.symbolTable.parent;

            return;
        } else if (literalNode instanceof TypeLiteralNode) {
            TypeLiteralNode typeLiteralNode = (TypeLiteralNode) literalNode;

            switch (typeLiteralNode.baseType) {
                case NUM:
                    this.symbolTable.mv.visitFieldInsn(GETSTATIC, KYTHERAVALUE_PATH, "NUM", "L" + KYTHERAVALUE_PATH + ";");
                    return;
                case FN:
                    FnTypeLiteralNode fnTypeLiteralNode = (FnTypeLiteralNode) typeLiteralNode;

                    // generate array of param types and push on stack
                    this.pushInt(fnTypeLiteralNode.parameterTypeExps.size());
                    this.symbolTable.mv.visitTypeInsn(ANEWARRAY, KYTHERAVALUE_PATH);

                    int n = 0;
                    for (ExpressionNode exp : fnTypeLiteralNode.parameterTypeExps) {
                        this.symbolTable.mv.visitInsn(DUP); // array ref is consumed on AASTORE, so keep a copy
                        this.pushInt(n);
                        this.visitExpression(exp);
                        this.symbolTable.mv.visitInsn(AASTORE);
                        n += 1;
                    }

                    // push return type on stack
                    this.visitExpression(fnTypeLiteralNode.returnTypeExp);

                    // call KytheraValue.getFnTypeValue
                    this.symbolTable.mv.visitMethodInsn(
                        INVOKESTATIC,
                        KYTHERAVALUE_PATH,
                        "getFnTypeValue",
                        "([Lme/dejawu/kythera/runtime/KytheraValue;Lme/dejawu/kythera/runtime/KytheraValue;)Lme/dejawu/kythera/runtime/KytheraValue;",
                        false);
                    return;
                default:
            }

            return;
        } else if (literalNode.equals(UnitLiteral.UNIT)) {
        }


        System.err.println("Unimplemented literal: ");
        literalNode.print(0, System.err);
        System.exit(1);
    }

    public void visitAs(AsNode literalNode) {
    }

    // ... value to be assigned => ...
    protected void visitAssign(AssignNode assignNode) {
        // only + allowed here

        // put RHS result on top of stack
        this.visitExpression(assignNode.right);

        if (assignNode.left instanceof IdentifierNode) {
            IdentifierNode identifierNode = (IdentifierNode) assignNode.left;

            this.symbolTable.storeSymbol(identifierNode.name);

            return;
        } else if (assignNode.left instanceof DotAccessNode) {
            // for now, all fields are mutable

        } else if (assignNode.left instanceof BracketAccessNode) {

        }
        System.err.println("Assignment LHS is invalid.");
        System.exit(1);

    }

    protected void visitBinary(BinaryNode binaryNode) {
        System.err.println("A binary node should not be present at code generation.");
        System.exit(1);
    }

    protected void visitBlock(BlockNode blockNode) {
        System.err.println("Block nodes not attached to a fn literal should not be present at code generation.");
        System.exit(1);
    }

    protected void visitBracketAccess(BracketAccessNode bracketAccessNode) {
    }

    // ... => ... value at identifier
    public void visitIdentifier(IdentifierNode identifierNode) {
        this.symbolTable.loadSymbol(identifierNode.name);
    }

    public void visitIf(IfNode literalNode) {
    }

    protected void visitUnary(UnaryNode unaryNode) {
    }

    public void visitWhile(WhileNode whileNode) {
    }

    // ... value => ... typeValue of value
    public void visitTypeof(TypeofNode typeofNode) {
        // put target value on top of stack
        this.visitExpression(typeofNode.target);

        this.symbolTable.mv.visitFieldInsn(
            GETFIELD, KYTHERAVALUE_PATH, "typeValue",
            "L" + KYTHERAVALUE_PATH + ";");
    }

    // pushes JVM int to stack, using constants where available
    private void pushInt(int value) {
        if (value == -1) {
            this.symbolTable.mv.visitInsn(ICONST_M1);
        } else if (value == 0) {
            this.symbolTable.mv.visitInsn(ICONST_0);
        } else if (value == 1) {
            this.symbolTable.mv.visitInsn(ICONST_1);
        } else if (value == 2) {
            this.symbolTable.mv.visitInsn(ICONST_2);
        } else if (value == 3) {
            this.symbolTable.mv.visitInsn(ICONST_3);
        } else if (value == 4) {
            this.symbolTable.mv.visitInsn(ICONST_4);
        } else if (value == 5) {
            this.symbolTable.mv.visitInsn(ICONST_5);
        } else {
            this.symbolTable.mv.visitIntInsn(BIPUSH, value);
        }
    }

    // pushes JVM float to stack, using constants where available
    private void pushFloat(float value) {
        if (value == 0.0) {
            this.symbolTable.mv.visitInsn(FCONST_0);
        } else if (value == 1.0) {
            this.symbolTable.mv.visitInsn(FCONST_1);
        } else if (value == 2.0) {
            this.symbolTable.mv.visitInsn(FCONST_2);
        } else {
            this.symbolTable.mv.visitLdcInsn(value);
        }
    }

    // generates unique internal names for static functions representing lambdas
    private String getLambdaName() {
        String name = "lambda" + this.lambdaCount;
        this.lambdaCount += 1;
        return name;
    }
}

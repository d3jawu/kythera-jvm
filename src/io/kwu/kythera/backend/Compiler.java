package io.kwu.kythera.backend;

import io.kwu.kythera.frontend.node.StatementNode;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;


public class Compiler {
    private List<StatementNode> program;

    public Compiler(List<StatementNode> program) {
        this.program = program;
    }

    public byte[] compile() {
        ClassWriter cw = new ClassWriter(0);

        cw.visit(
            V14,
            ACC_PUBLIC | ACC_SUPER,
            "pkg/ProgramName",
            null,
            "java/lang/Object",
            null);

        // unlike Java, Kythera has a global scope, which we use the main
        // method for
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_SUPER, "main",
            "([Ljava/lang/String;)V", null, null);

        for (StatementNode st : this.program) {
            switch (st.kind) {
            }
        }

        cw.visitEnd();
        return cw.toByteArray();
    }


}

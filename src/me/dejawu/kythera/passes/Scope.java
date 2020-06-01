package me.dejawu.kythera.passes;

import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

import java.util.HashMap;

public class Scope extends HashMap<String, Integer>  {
    public final Scope parent;
    public final MethodVisitor mv;

    // root scope (no parent)
    public Scope(MethodVisitor mv) {
        this.parent = null;
        this.mv = mv;
    }

    // scope with parent
    public Scope(Scope parent, MethodVisitor mv) {
        this.parent = parent;
        this.mv = mv;
    }

    // TODO use ASTORE_0-3 instructions

    // generates instructions that will store the variable on top of the
    // stack in a new slot
    public void addSymbol(String name) {
        final int slot = this.size();
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

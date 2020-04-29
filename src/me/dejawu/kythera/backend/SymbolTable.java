package me.dejawu.kythera.backend;

import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

import java.util.HashMap;

public class SymbolTable extends HashMap<String, Integer>  {
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

    // generates instructions that will store the variable on top of the
    // stack in a new slot
    public void addSymbol(String name) {
        final int slot = this.size();
        this.mv.visitVarInsn(ASTORE, slot);
        this.put(name, slot);
    }

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

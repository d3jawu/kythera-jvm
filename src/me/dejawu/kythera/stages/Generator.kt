package me.dejawu.kythera.stages

import me.dejawu.kythera.AstNode
import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*

class Generator(private val program: List<AstNode>) {
    private val classWriter: ClassWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES)

    fun generate(): ByteArray? {
        classWriter.visit(V14, ACC_PUBLIC or ACC_SUPER, "KMain", null, "java/lang/Object", null)

        // generate main
        val mainMethodVisitor = classWriter.visitMethod(ACC_PUBLIC or ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null)
        mainMethodVisitor.visitCode()
        mainMethodVisitor.visitInsn(RETURN)
        mainMethodVisitor.visitEnd()

        this.classWriter.visitEnd()
        return this.classWriter.toByteArray()
    }
}
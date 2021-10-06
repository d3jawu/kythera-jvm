package me.dejawu.kythera.runtime

object KRuntime {
    fun makeInt(value: Int): KVal<Int> {
        return KVal(
            value,

            )
    }

    class KVal<T>(val value: T, val typeValue: KVal<*>, val fields: Map<String, KVal<*>>)
}


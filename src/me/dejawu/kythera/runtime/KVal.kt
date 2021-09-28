package me.dejawu.kythera.runtime

class KVal<T>(val value: T, val typeValue: KVal<*>, val fields: Map<String, KVal<*>>)
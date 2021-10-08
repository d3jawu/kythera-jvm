package me.dejawu.kythera;

typealias FieldVals = Map<String, KVal<*>>;

// structs and types are actually the same data type (types are just structs that describe other structs)
typealias StructTypeKVal = KVal<FieldVals>

class KVal<T> {
    private val value: T
    private val typeValue: StructTypeKVal
    private val fieldVals: FieldVals;

    constructor(value: T, typeValue: StructTypeKVal, fieldVals: FieldVals) {
        this.value = value;
        this.typeValue = typeValue;
        this.fieldVals = fieldVals;
    }

    // for structs/types, the value *is* the field values
    constructor(typeValue: StructTypeKVal, fieldVals: FieldVals) {
        this.fieldVals = fieldVals;
        this.value = this.fieldVals as T;

        this.typeValue = typeValue;
    }

    private constructor(root: T) {
        this.value = root;
        this.typeValue = this as StructTypeKVal;
        this.fieldVals = root as FieldVals;
    }

    companion object {
        // type literals
        @JvmField
        // root type describes the fields that type-values have, and points to itself for its type value
        val rootType: StructTypeKVal = KVal(mapOf())

        @JvmField
        val intType = StructTypeKVal(
            rootType,
            mapOf(
                // describe int field types here
            ),
        )

        val intFields: FieldVals = mapOf(
            // implement int functions here
        )

        // value factories
        @JvmStatic
        fun makeInt(value: Int): KVal<Int> {
            return KVal(
                value, intType,
                intFields
            )
        }
    }
}
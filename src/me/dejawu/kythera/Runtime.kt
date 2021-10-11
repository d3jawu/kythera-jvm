package me.dejawu.kythera;

import java.util.function.Function;

typealias FieldVals = Map<String, KVal<*>>

typealias FnVal = Function<List<KVal<*>>, KVal<*>>
typealias FnKVal = KVal<FnVal>

typealias FnTypeVal = Pair<List<KVal<*>>, KVal<*>>
typealias FnTypeKVal = KVal<FnTypeVal>

class KVal<T> {
    private val value: T
    private var typeValue: KVal<*>
    private val fieldVals: FieldVals;

    /*
    constructor(value: T, typeValue: KVal<*>, fieldVals: FieldVals) {
        this.value = value
        this.typeValue = typeValue
        this.fieldVals = fieldVals
    }
    */

    private constructor(value: T, typeValue: KVal<*>, fieldVals: FieldVals)

    // for structs/types, the value *is* the field values
    private constructor(typeValue: KVal<*>, fieldVals: FieldVals) {
        this.fieldVals = fieldVals;
        this.value = this.fieldVals as T

        this.typeValue = typeValue;
    }

    // used only for root type, which references itself for its type-value
    private constructor(fieldValsFrom: Function1<KVal<*>, FieldVals>) {
        this.fieldVals = fieldValsFrom(this)
        this.value = this.fieldVals as T

        this.typeValue = this
    }

    // allow custom self-referential mapping
    private constructor(typeValue: KVal<*>, fieldValsFrom: Function1<KVal<*>, FieldVals>) {
        this.fieldVals = fieldValsFrom(this)
        this.value = this.fieldVals as T

        this.typeValue = typeValue;
    }

    companion object {
        // stores canonical references for type-values
        private val typePool = HashMap<KVal<*>, KVal<*>>()

        @JvmStatic
        // fetches canonical reference for type-value, creating one in the pool if necessary
        fun getType(value: KVal<*>): KVal<*> {
            val canonType = typePool[value];

            return if(canonType == null) {
                typePool[value] = value
                value
            } else {
                typePool[value] ?: throw Exception()
            }
        }

        val fnTypeFields: FieldVals = mapOf()

        // type literals
        @JvmField
        // root type describes the fields that type-values have, and points to itself for its type value
        val TYPE: KVal<*> = getType(KVal<FieldVals> { TYPE ->
            mapOf(
                "<:" to getType(
                    KVal<FnTypeVal>(
                        TYPE,
                        KVal(TYPE, Pair(listOf(TYPE, TYPE), TYPE)),
                        fnTypeFields
                    )
                )
            )
        })

        @JvmField
//        val INT: KVal<*> = getType(
//            KVal(TYPE) {
//                mapOf()
//            }
//        )


        val intFields: FieldVals = mapOf(
            // implement int functions here
        )

        // value factories
        @JvmStatic
//        fun makeInt(value: Int): KVal<Int> {
//            return KVal(
//                value, INT,
//                intFields
//            )
//        }
    }
}
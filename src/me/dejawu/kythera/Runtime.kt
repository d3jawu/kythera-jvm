package me.dejawu.kythera

import java.util.function.Function

typealias FieldVals = Map<String, KVal<*>>
typealias FnVal = Function<List<KVal<*>>, KVal<*>>
typealias FnTypeVal = Pair<List<KVal<*>>, KVal<*>>

class KVal<T> {
    val value: T
    val typeValue: KVal<*>
    val fieldVals: FieldVals

    private constructor(value: T, typeValue: KVal<*>, fieldVals: FieldVals) {
        this.value = value
        this.typeValue = typeValue
        this.fieldVals = fieldVals
    }

    // used only for root type, which references itself for its type-value
    private constructor(fieldValsFrom: Function1<KVal<*>, FieldVals>) {
        this.fieldVals = fieldValsFrom(this)
        this.value = this.fieldVals as T

        this.typeValue = this
    }

    // allow custom self-referential mapping for type-value definitions
    private constructor(typeValue: KVal<*>, fieldValsFrom: Function1<KVal<*>, FieldVals>) {
        this.fieldVals = fieldValsFrom(this)
        this.value = this.fieldVals as T

        this.typeValue = typeValue
    }

    companion object {
        // stores canonical references for type-values
        private val typePool = HashMap<KVal<*>, KVal<*>>()

        @JvmStatic
        // fetches canonical reference for type-value, creating one in the pool if necessary
        fun getType(value: KVal<*>): KVal<*> {
            val canonType = typePool[value]

            return if (canonType == null) {
                typePool[value] = value
                value
            } else {
                typePool[value] ?: throw Exception()
            }
        }

        // describes fields that fn types have (currently, nothing)
        private val fnTypeFields: FieldVals = mapOf()

        @JvmField
        // root type describes the fields that type-values have, and points to itself for its type value
        val TYPE: KVal<*> = getType(KVal<FieldVals> { TYPE ->
            mapOf(
                "<:" to getType(
                    KVal<FnTypeVal>(
                        Pair(listOf(TYPE, TYPE), TYPE),
                        TYPE,
                        fnTypeFields
                    )
                ),
                ":>" to getType(
                    KVal<FnTypeVal>(
                        Pair(listOf(TYPE, TYPE), TYPE),
                        TYPE,
                        fnTypeFields
                    )
                )
            )
        })

        @JvmField
        val BOOL: KVal<*> = getType(
            KVal<FieldVals>(
                TYPE,
                fun(BOOL: KVal<*>): FieldVals {
                    val boolBoolToBoolType = getType(
                        KVal<FnTypeVal>(
                            Pair(listOf(BOOL, BOOL), BOOL),
                            TYPE,
                            fnTypeFields
                        )
                    )

                    return mapOf(
                        "||" to boolBoolToBoolType,
                        "&&" to boolBoolToBoolType,
                        "!" to getType(
                            KVal<FnTypeVal>(
                                Pair(listOf(BOOL), BOOL),
                                TYPE,
                                fnTypeFields
                            )
                        )
                    )
                }
            )
        )

        @JvmField
        val INT: KVal<*> = getType(
            KVal<FieldVals>(
                TYPE,
                fun(INT: KVal<*>): FieldVals {
                    val intIntToIntType = getType(
                        KVal<FnTypeVal>(
                            Pair(listOf(INT, INT), INT),
                            TYPE,
                            fnTypeFields
                        )
                    )
                    // type-values of int fields
                    return mapOf(
                        "+" to intIntToIntType,
                        "-" to intIntToIntType,
                        "*" to intIntToIntType,
                        "/" to intIntToIntType,
                        "%" to intIntToIntType,
                    )
                }
            )
        )

        // implementations of fields of fn values
        private val fnFields: FieldVals = mapOf()


        fun makeFnKVal(
            value: FnVal,
            typeValue: KVal<*>,
        ): KVal<*> {
            return KVal(
                value,
                typeValue,
                fnFields
            )
        }

        private val boolBoolToBoolType = getType(
            KVal<FnTypeVal>(
                Pair(listOf(BOOL, BOOL), BOOL),
                TYPE,
                fnTypeFields
            )
        )

        private val boolToBoolType = getType(
            KVal<FnTypeVal>(
                Pair(listOf(BOOL), BOOL),
                TYPE,
                fnTypeFields
            )
        )

        // boolean value factory
        private val boolFields: FieldVals = mapOf(
            "||" to makeFnKVal(
                { p: List<KVal<Boolean>> ->
                    makeBoolVal(p[0].value || p[1].value)
                } as (List<KVal<*>>) -> KVal<*>,
                boolBoolToBoolType
            ),
            "&&" to makeFnKVal(
                { p: List<KVal<Boolean>> ->
                    makeBoolVal(p[0].value && p[1].value)
                } as (List<KVal<*>>) -> KVal<*>,
                boolBoolToBoolType
            ),
            "!" to makeFnKVal(
                { p: List<KVal<Boolean>> ->
                    makeBoolVal(!p[0].value)
                } as (List<KVal<*>>) -> KVal<*>,
                boolToBoolType
            ),
        )

        @JvmStatic
        fun makeBoolVal(value: Boolean): KVal<Boolean> {
            return KVal(
                value, BOOL,
                boolFields
            )
        }

        // int value factory
        private val intIntToIntType = getType(
            KVal<FnTypeVal>(
                Pair(listOf(INT, INT), INT),
                TYPE,
                fnTypeFields
            )
        )

        private val intFields: FieldVals = mapOf(
            "+" to makeFnKVal(
                { p: List<KVal<Int>> ->
                    makeIntKVal(p[0].value + p[1].value)
                } as (List<KVal<*>>) -> KVal<*>, // unfortunately an unchecked cast is needed here; the generated code will never fail it
                intIntToIntType,
            ),
            "-" to makeFnKVal(
                { p: List<KVal<Int>> ->
                    makeIntKVal(p[0].value - p[1].value)
                } as (List<KVal<*>>) -> KVal<*>,
                intIntToIntType,
            ),
            "*" to makeFnKVal(
                { p: List<KVal<Int>> ->
                    makeIntKVal(p[0].value * p[1].value)
                } as (List<KVal<*>>) -> KVal<*>,
                intIntToIntType,
            ),
            "/" to makeFnKVal(
                { p: List<KVal<Int>> ->
                    makeIntKVal(p[0].value / p[1].value)
                } as (List<KVal<*>>) -> KVal<*>,
                intIntToIntType,
            ),
            "%" to makeFnKVal(
                { p: List<KVal<Int>> ->
                    makeIntKVal(p[0].value % p[1].value)
                } as (List<KVal<*>>) -> KVal<*>,
                intIntToIntType,
            ),
        )

        @JvmStatic
        fun makeIntKVal(value: Int): KVal<Int> {
            return KVal(
                value, INT,
                intFields
            )
        }
    }
}
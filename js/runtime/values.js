// only Value is to be used directly
class Value {
  constructor(value, typeValue, fieldValues = {}) {
    this.value = value;
    this.typeValue = typeValue;
    this.fieldValues = fieldValues;
  }
}

// describes a type; is used in the 'value' field of type values. not to be used directly
class TypeValue {
  constructor(
    // see BaseType.java for valid strings. Remember the 
    baseType,

    // typeMeta contains additional information needed to fully describe compound types.
    typeMeta = {
      // every TypeValue has a fieldTypes typeMeta entry which lists the fields that instances of that type will have
      fieldTypes: {},

      // fn type values have param types and a return type
      // paramTypes: [],
      // returnType: [],

      // list type values have a contained type
      // containedType: null,

      // struct type values have an entryTypes object that contains entry names and their types
      // entryTypes: {},
    }
  ) {
    this.baseType = baseType; // see BaseType.java for valid basetypes; JS implementation uses NUM for all numbers
    this.typeMeta = typeMeta; // additional info needed to distinguish non-primitive types, e.g. function parameters to tell a fn(int) from an fn(char)
  }
}

export { Value, TypeValue };

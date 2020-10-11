// only Value is to be used directly
class Value {
  constructor(value, typeValue, fieldValues = {}) {
    this.value = value;
    this.typeValue = typeValue;
    this.fieldValues = fieldValues;
  }
}

// describes a type; is used in the 'value' field of type values.
class TypeValue {
  constructor(baseType, typeMeta = {
    // every TypeValue has a fieldTypes entry which lists the fields that instances of that type will have
    fieldTypes: {},
    // fn values have param types and a return type
    // paramTypes: [],
    // returnType: [],
    // list values have a list type
    // listType: null,
  }) {
    this.baseType = baseType; // see BaseType.java for valid basetypes
    this.typeMeta = typeMeta; // additional info needed to distinguish non-primitive types, e.g. function parameters to tell a fn(int) from an fn(char)
  }
}

export {
    Value,
    TypeValue,
}
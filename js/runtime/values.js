// only Value is to be used directly
class Value {
  constructor(value, typeValue, fields = {}) {
    this.value = value;
    this.typeValue = typeValue;
    this.fields = fields;
  }
}

// describes a type; is used in the 'value' field of type values.
class TypeValue {
  constructor(baseType, typeMeta = {}) {
    this.baseType = baseType; // see BaseType.java for valid basetypes
    this.typeMeta = typeMeta; // additional info needed to distinguish non-primitive types, e.g. function paramters
    // this.instanceFields = []; // this might not be necessary
  }
}

export {
    Value,
    TypeValue,
}
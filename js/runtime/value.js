// only Value is to be used directly
class Value {
  constructor(value, typeValue, fieldValues = {}) {
    this.value = value;
    this.typeValue = typeValue; // points to another Value (capital V)
    this.fieldValues = fieldValues;
  }
}

// describes a type; is used in the 'value' field of type values. not to be used directly
class TypeValue {
  constructor(
    // see BaseType.java for valid strings.
    baseType,

    // typeMeta contains additional information needed to fully describe compound types.
    // typeMeta should store Values, not TypeValues. (TBD)
    typeMeta = {
      // every TypeValue has a fieldTypes typeMeta entry which lists the fields that instances of that type will have
      fieldTypes: {},

      // fn type values have param types and a return type
      // paramTypes: [],
      // returnType: null,

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

// holds serialized types
const typeStore = {};

function serializeType(type) {
  switch (type.baseType) {
    // constant type values of which there is only one instance (see also TypeLiteralNode.java)
    case "BOOL":
    case "NUM":
    case "TYPE":
      return type.baseType + ";";
    case "FN":
      return `FN(${type.typeMeta.paramTypes
        .map(({ value }) => serializeType(value))
        .join(",")})=>${serializeType(type.typeMeta.returnType.value)};`;
    case "STRUCT":
      return `STRUCT{${Object.entries(type.typeMeta.fieldTypes)
        .sort(([k1], [k2]) => k1 > k2)
        .map(([k, v]) => `${k}:${serializeType(v.value)}`)
        .join(",")}};`;
    default:
      throw new Error("Can't serialize: '" + type.baseType + "'");
  }
}

// intern type values
const InternedTypeValue = new Proxy(TypeValue, {
  construct: (target, args, newTarget) => {
    const newTypeVal = new TypeValue(...args);
    const key = serializeType(newTypeVal);

    if (typeStore[key]) {
      // hit
      return typeStore[key];
    } else {
      // miss
      typeStore[key] = newTypeVal;
      return newTypeVal;
    }
  },
});

export { Value, InternedTypeValue as TypeValue };

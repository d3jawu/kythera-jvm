const runtime = {
  consts: {},
};

runtime.value = class {
  constructor(value, typeValue, fields = {}) {
    this.value = value;
    this.typeValue = typeValue;
    this.fields = fields;
  }
};

// describes a type; is used in the 'value' field of type values.
// not to be used directly as a value
runtime.typeValue = class {
  constructor(baseType, typeMeta = {}) {
    this.baseType = baseType; // see BaseType.java for valid basetypes
    this.typeMeta = typeMeta; // additional info needed to distinguish non-primitive types, e.g. function paramters
    // this.instanceFields = []; // this might not be necessary
  }
};

// bootstrap type
runtime.consts.TYPE = new runtime.value(
  new runtime.typeValue("TYPE", {}),
  null, // filled in after initialization
  {} // for now, type instances have no fields
  // {
  //     "<:": null,
  //     ">:": null
  // }
);
runtime.consts.TYPE.typeValue = runtime.consts.TYPE; // root type must self-reference
// TODO fill in fields in TYPE.value, should map <: and >: to function types

// wraps a typeValue into a proper value
const wrapTypeValue = (typeValue) =>
  new runtime.value(
    typeValue,
    TYPE,
    {} // TODO implement <: and >: here
  );

// reusable constants
runtime.consts.BOOL = new runtime.value(
  new runtime.typeValue("BOOL"),
  runtime.consts.TYPE
);

// boolean values
runtime.consts.TRUE = new runtime.value(true, runtime.consts.BOOL, {});
runtime.consts.FALSE = new runtime.value(false, runtime.consts.BOOL, {});

module.exports = runtime;

import { Value, TypeValue } from "./value";

// bootstrap type
const TYPE = new Value(
  new TypeValue("TYPE", {}),
  null, // filled in after initialization
  {
    // TODO: implementations of subtype/supertype functions
    ">:": null,
    "<:": null,
  }
);
TYPE.typeValue = TYPE; // root type must self-reference

// type literals
const BOOL = new Value(new TypeValue("BOOL"), TYPE);

// all number types are mapped to JS's Number type
const NUM = new Value(new TypeValue("NUM"), TYPE);

// member functions for bool
const boolBoolToBoolFnType = new TypeValue("FN", {
  paramTypes: [BOOL, BOOL],
  returnType: BOOL,
});

const boolEquiv = new Value(
  (self, other) => (self.value == other.value ? TRUE : FALSE),
  boolBoolToBoolFnType
)

const boolMembers = {
  "!": new Value(
    (self) => (self.value ? FALSE : TRUE),
    new TypeValue("FN", {
      paramTypes: [BOOL],
      returnType: BOOL,
    })
  ),
  "||": new Value(
    (self, other) => (self.value || other.value ? TRUE : FALSE),
    boolBoolToBoolFnType
  ),
  "&&": new Value(
    (self, other) => (self.value && other.value ? TRUE : FALSE),
    boolBoolToBoolFnType
  ),
  "==": boolEquiv,
  "===": boolEquiv,
};

// boolean values
const TRUE = new Value(true, BOOL, boolMembers);
const FALSE = new Value(false, BOOL, boolMembers);

export { TYPE, BOOL, NUM, TRUE, FALSE };

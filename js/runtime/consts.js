import { Value, TypeValue } from "./values";

// bootstrap type
const TYPE = new Value(
  new TypeValue("TYPE", {}),
  null, // filled in after initialization
  { // implementations of subtype/supertype functions
    ">:": null,
    "<:": null
  }
);
TYPE.typeValue = TYPE; // root type must self-reference

// type literals
const BOOL = new Value(
  new TypeValue("BOOL"),
  TYPE
);

// all number types are mapped to JS's Number type
const NUM = new Value(
  new TypeValue("NUM"),
  TYPE
)

// boolean values
const TRUE = new Value(true, BOOL, {});
const FALSE = new Value(false, BOOL, {});

export {
  TYPE,
  BOOL,
  NUM,
  TRUE,
  FALSE,
};

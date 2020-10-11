import { Value, TypeValue } from "./values";

// bootstrap type
const TYPE = new Value(
  new TypeValue("TYPE", {}),
  null, // filled in after initialization
  {} // for now, type instances have no fields
  // {
  //     "<:": null,
  //     ">:": null
  // }
);
TYPE.typeValue = TYPE; // root type must self-reference

// wraps a typeValue into a proper value
const wrapTypeValue = (typeValue) =>
  new Value(
    typeValue,
    TYPE,
    {} // TODO implement <: and >: here
  );

// type literals
const BOOL = new Value(
  new TypeValue("BOOL"),
  TYPE
);

// boolean values
const TRUE = new Value(true, BOOL, {});
const FALSE = new Value(false, BOOL, {});

export {
  TYPE,
  BOOL,
  TRUE,
  FALSE,
};

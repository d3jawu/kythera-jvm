import { TypeValue, Value } from "./value";
import { TYPE, BOOL, NUM, TRUE, FALSE } from "./consts";

// reuse type values
// TODO: intern this type
const numNumToNumFnType = new TypeValue("FN", {
  paramTypes: [NUM, NUM],
  returnType: NUM,
});

// declare member functions outside initializer so they can be reused
const add = new Value(
  (self, other) => num(self.value + other.value),
  numNumToNumFnType
);

const subtract = new Value(
  (self, other) => num(self.value - other.value),
  numNumToNumFnType
);

const multiply = new Value(
  (self, other) => num(self.value * other.value),
  numNumToNumFnType
);

const divide = new Value(
  (self, other) => num(self.value / other.value),
  numNumToNumFnType
);

const modulo = new Value(
  (self, other) => num(self.value % other.value),
  numNumToNumFnType
);

// factories for built-in types

const num = (val) =>
  new Value(val, NUM, {
    "+": add,
    "-": subtract,
    "*": multiply,
    "/": divide,
    "%": modulo,
  });

// takes a JS object with members as keys
const struct = (val, type) =>
  new Value(
    val,
    type,
    val // uniquely for structs, their fields are simply a pointer to their value (or vice versa)
  );

// TODO intern types
const type = (fieldTypes) =>
  new Value(
    new TypeValue(
      "STRUCT", // only structs are user-creatable types, so this is always a struct
      { fieldTypes }
    ),
    TYPE,
    {
      // TODO implementation of subtype/supertype
    }
  );

const fn = (lambda, fnType) =>
  new Value(
    lambda,
    fnType,
    {} // function values have no struct entries
  );

export default {
  bool: (val) => (val ? TRUE : FALSE),
  num,
  struct,
  type,
  fn,
};

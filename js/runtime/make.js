import { TypeValue, Value } from "./value";
import { TYPE, BOOL, NUM, TRUE, FALSE } from "./consts";

// reuse type values
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

const numEquiv = new Value(
  (self, other) => (self.value == other.value) ? TRUE : FALSE
)

const numNotEquiv = new Value(
  (self, other) => (self.value != other.value) ? TRUE : FALSE
)

const numLt = new Value(
  (self, other) => (self.value < other.value) ? TRUE : FALSE
)
const numLte = new Value(
  (self, other) => (self.value <= other.value) ? TRUE : FALSE
)
const numGt = new Value(
  (self, other) => (self.value > other.value) ? TRUE : FALSE
)
const numGte = new Value(
  (self, other) => (self.value >= other.value) ? TRUE : FALSE
)

// factories for built-in types

const num = (val) =>
  new Value(val, NUM, {
    "+": add,
    "-": subtract,
    "*": multiply,
    "/": divide,
    "%": modulo,
    "==": numEquiv,
    "===": numEquiv,
    "!=": numNotEquiv,
    "!==": numNotEquiv,
    "<": numLt,
    "<=": numLte,
    ">": numGt,
    ">=": numGte,
  });

// takes a JS object with members as keys
const struct = (val, type) =>
  new Value(
    val,
    type,
    val // uniquely for structs, their fields are simply a pointer to their value (or vice versa)
  );

const type = (fieldTypes) =>
  new Value(
    new TypeValue(
      "STRUCT", // only structs are user-creatable types, so this is always a struct
      { fieldTypes }
    ),
    TYPE, // type of a type value is always TYPE
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

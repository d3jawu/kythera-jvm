import { TypeValue, Value } from "./values";
import { NUM, TRUE, FALSE } from "./consts";

// reuse type values
const numNumToNumFnType= new TypeValue("FN", {
  paramTypes: [NUM],
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

const num= (val) =>
  new Value(val, NUM, {
    "+": add,
    "-": subtract,
    "*": multiply,
    "/": divide,
    "%": modulo,
  });

export default {
  bool: (val) => (val ? TRUE : FALSE),
  num,
};

import { TypeValue, Value } from "./values";
import { INT, TRUE, FALSE } from "./consts";

// reuse type values
const intIntToIntFnType = new TypeValue("FN", {
  paramTypes: [INT],
  returnType: INT,
});

const fn = () => new Value();

// declare member functions outside initializer so they can be reused
const intAdd = new Value(
  (self, other) => int(self.value + other.value),
  intIntToIntFnType
);

const intSubtract = new Value(
  (self, other) => int(self.value - other.value),
  intIntToIntFnType
);

const intMultiply = new Value(
  (self, other) => int(self.value * other.value),
  intIntToIntFnType
);

const intDivide = new Value(
  (self, other) => int(self.value / other.value),
  intIntToIntFnType
);

const intModulo = new Value(
  (self, other) => int(self.value % other.value),
  intIntToIntFnType
);

const int = (val) =>
  new Value(val, INT, {
    "+": intAdd,
    "-": intSubtract,
    "*": intMultiply,
    "/": intDivide,
    "%": intModulo,
  });

export default {
  bool: (val) => (val ? TRUE : FALSE),
  int,
  fn,
};

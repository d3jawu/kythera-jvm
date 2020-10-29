const { buildAndEval } = require("./util.js");

let res;

describe("Numbers", () => {
  beforeAll((done) => {
    buildAndEval("num").then(
      (value) => {
        res = value;
        done();
      },
      (reason) => {
        done(reason);
      }
    );
  });

  test("Assignment", () => {
    expect(res.x.value).toEqual(10);
    expect(res.y.value).toEqual(5);
  })

  test("Addition", () => {
    expect(res.sum.value).toEqual(15);
  });

  test("Subtraction", () => {
    expect(res.difference.value).toEqual(5);
  });

  test("Multiplication", () => {
    expect(res.product.value).toEqual(50);
  });

  test("Division", () => {
    expect(res.quotient.value).toEqual(2);
  });

  test("Modulo", () => {
    expect(res.remainder.value).toEqual(0);
  })
});

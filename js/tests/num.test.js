const { buildAndEval } = require("./util.js");

let res;

describe("Numbers", () => {
  beforeAll((done) => {
    buildAndEval("num")
      .then((value) => {
        res = value;
        done();
      })
      .catch((reason) => {
        done(reason);
      });
  });

  test("Literal values", () => {
    expect(res.numVal1.value).toEqual(1);
    expect(res.numVal2.value).toBeCloseTo(1.01);
  });

  test("Assignment", () => {
    expect(res.x.value).toEqual(10);
    expect(res.y.value).toEqual(5);
  });

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
  });

  test("Compound Expressions", () => {
    expect(res.compound1.value).toEqual(11);
    expect(res.compound2.value).toBeCloseTo(0.6);
  });

  test("Number types", () => {
    expect(res.numType1.value.baseType).toEqual("NUM");
    expect(res.numType2.value.baseType).toEqual("NUM");
  });
});

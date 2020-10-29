const { buildAndEval } = require("./util.js");

let res;

describe("Literals", () => {
  beforeAll((done) => {
    buildAndEval("literals")
      .then((value) => {
        res = value;
        done();
      })
      .catch((reason) => {
        done(reason);
      });
  });

  test("number (integer)", () => {
    expect(res.intNumLiteral.value).toBe(2);
  });

  test("number (floating-point)", () => {
    expect(res.floatNumLiteral.value).toBe(2.01);
  });
});

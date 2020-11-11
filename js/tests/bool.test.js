const { buildAndEval } = require("./util.js");

let res;

describe("Booleans", () => {
  beforeAll((done) => {
    buildAndEval("bool")
      .then((value) => {
        res = value;
        done();
      })
      .catch((reason) => {
        done(reason);
      });
  });

  test("Literal values", () => {
    expect(res.myTrue.value).toBe(true);
    expect(res.myFalse.value).toBe(false);
  });

  test("Basic ops", () => {
    expect(res.orResult.value).toBe(true);
    expect(res.andResult.value).toBe(false);
  });

  /*
  test("DeMorgan's Laws", () => {

  })
  */
});

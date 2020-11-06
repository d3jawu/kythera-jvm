const { buildAndEval } = require("./util.js");

let res;

describe("Functions", () => {
  beforeAll((done) => {
    buildAndEval("fn")
      .then((value) => {
        res = value;
        done();
      })
      .catch((reason) => {
        done(reason);
      });
  });

  test("Function with no parameters", () => {
    expect(res.noParamFnResult.value).toBe(2);
  });

  test("Struct type", () => {
    expect(res.oneParamFnResult.value).toBe(6);
    });

});

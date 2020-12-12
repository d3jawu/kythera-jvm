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
    expect(res.notTrue.value).toBe(false);
    expect(res.notFalse.value).toBe(true);
    expect(res.notMyTrue.value).toBe(false);
    expect(res.notMyFalse.value).toBe(true);

    expect(res.orResult.value).toBe(true);
    expect(res.andResult.value).toBe(false);
  });

  test("DeMorgan's Laws", () => {
    expect(res.dmTT.value).toBe(true);
    expect(res.dmTF.value).toBe(true);
    expect(res.dmFF.value).toBe(true);
    expect(res.dmFT.value).toBe(true);
  })
});

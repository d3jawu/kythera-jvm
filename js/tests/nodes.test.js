const { buildAndEval } = require("./util.js");

let res;

describe("Nodes", () => {
  beforeAll((done) => {
    buildAndEval("nodes").then(
      (value) => {
        res = value;
        done();
      },
      (reason) => {
        done(reason);
      }
    );
  });

  test("let", () => {
    expect(res.letNode.value).toBe(1);
  })
});

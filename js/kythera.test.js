const { parseModule } = require("esprima");
const fs = require("fs");
const { execSync } = require("child_process");
const rollup = require("rollup");

// relative to JS root
const TEST_DIR = "./tests";
const OUT_DIR = "./out";
const KYTHERA_JAR_PATH = "../out/artifacts/kythera_jar/kythera.jar";

// identifiers to ignore when testing files
const IDENTIFIER_EXCEPTIONS = ["_KY", "from"];

console.info("Make sure latest Jar has been built!");

const buildAndTest = (name, expected) => async () => {
  execSync(
    `java -jar ${KYTHERA_JAR_PATH} ${TEST_DIR}/${name} -o out/${name}.js`
  );

  // extract global variables from raw
  const raw = fs.readFileSync(`./out/${name}.js`, "utf-8");

  const globals = [];
  parseModule(raw)
    .body.filter(({ type }) => type === "VariableDeclaration") // picks up levels at top-level scope only
    .forEach(({ declarations }) => {
      declarations.forEach(({ id }) => {
        const { name } = id;
        globals.push(name);
      });
    });

  const bundle = await rollup.rollup({
    input: `${OUT_DIR}/${name}.js`,
  });
  const {
    output: [{ code }],
  } = await bundle.generate({});

  // inject code to make results accessible
  let injected = "\nvar out = {\n";
  globals.forEach((global) => {
    injected += `\t${global},\n`;
  });
  injected += "};";

  const result = code + injected;
  eval(result);

  expect(out).toEqual(expected);
};

describe("Kythera integration tests", () => {
  // test("int", buildAndTest("int", {}));
});

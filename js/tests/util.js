const { parseModule } = require("esprima");
const fs = require("fs");
const { execSync } = require("child_process");
const rollup = require("rollup");

// relative to /js
const TEST_DIR = "./tests/ky";
const OUT_DIR = "./out";
const KYTHERA_JAR_PATH = "../out/artifacts/kythera_jar/kythera.jar";

// identifiers to ignore when testing files
const IDENTIFIER_EXCEPTIONS = ["_KY", "from"];

console.info("Make sure latest Jar has been built!");

const buildAndEval = async (fileName) => {
  execSync(
    `java -jar ${KYTHERA_JAR_PATH} ${TEST_DIR}/${fileName} -p js -o out/${fileName}.js`,
    { stdio: "inherit" }
  );

  // extract global variables from raw
  const raw = fs.readFileSync(`./out/${fileName}.js`, "utf-8");

  const globals = [];
  parseModule(raw)
    .body.filter(({ type }) => type === "VariableDeclaration") // picks up variables at top-level scope only
    .forEach(({ declarations }) => {
      declarations.forEach(({ id }) => {
        const { name } = id;
        globals.push(name);
      });
    });

  const bundle = await rollup.rollup({
    input: `${OUT_DIR}/${fileName}.js`,
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

  eval(code + injected);
  return out; // 'out' variable is introduced to local scope when eval is run
};

module.exports = {
  buildAndEval,
};

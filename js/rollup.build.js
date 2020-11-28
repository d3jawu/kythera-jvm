import { terser } from "rollup-plugin-terser";

export default {
  input: "out/out.js",
  output: {
    file: "out/out.bundle.js",
    compact: true,
  },
  plugins: [terser()],
};

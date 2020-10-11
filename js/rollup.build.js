import { terser } from "rollup-plugin-terser";

// config for dev
export default {
  input: "out.js",
  output: {
    file: "out.bundle.js",
    compact: true,
  },
  plugins: [terser()],
};
